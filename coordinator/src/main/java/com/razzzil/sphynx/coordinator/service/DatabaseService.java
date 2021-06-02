package com.razzzil.sphynx.coordinator.service;

import com.razzzil.sphynx.commons.constant.ValidationPresets;
import com.razzzil.sphynx.commons.model.payload.Payload;
import com.razzzil.sphynx.commons.model.worker.connectionstate.ConnectionState;
import com.razzzil.sphynx.commons.util.Condition;
import com.razzzil.sphynx.commons.model.database.DatabaseModel;
import com.razzzil.sphynx.commons.model.database.configs.DatabaseConfigs;
import com.razzzil.sphynx.commons.model.type.DatabaseType;
import com.razzzil.sphynx.commons.validation.ValidationResult;
import com.razzzil.sphynx.coordinator.exception.EntityNotFoundException;
import com.razzzil.sphynx.coordinator.exception.IllegalFieldException;
import com.razzzil.sphynx.coordinator.exception.UnsuccessfulOperationException;
import com.razzzil.sphynx.coordinator.model.form.request.worker.WorkerDisplayForm;
import com.razzzil.sphynx.coordinator.model.form.response.exception.EntityNotFoundExceptionResponse;
import com.razzzil.sphynx.coordinator.model.form.response.exception.IllegalFieldExceptionResponse;
import com.razzzil.sphynx.coordinator.model.form.response.exception.UnsuccessfulOperationExceptionResponse;
import com.razzzil.sphynx.coordinator.repository.DatabaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

import static com.razzzil.sphynx.commons.constant.Regex.VALID_LOGIN_REGEX;

@Service
public class DatabaseService {

    @Autowired
    private DatabaseRepository databaseRepository;

    @Autowired
    private TestIterationWebhookService testIterationWebhookService;

    @Autowired
    private WorkerService workerService;

    private static final String ENTITY_NAME = "Database";
    private static final String ALIAS_FIELD_NAME = "alias";

    public <T extends DatabaseConfigs<? extends DataSource>> DatabaseModel createDatabase(Integer userId, DatabaseType databaseType, String configs, Integer workerId, String alias) {
        if (Objects.isNull(alias)) {
            throw new IllegalFieldException(IllegalFieldExceptionResponse.fromValidationResult(ValidationResult.builder()
                    .fieldName(ALIAS_FIELD_NAME)
                    .description(ValidationPresets.formatIsNull(ALIAS_FIELD_NAME))
                    .build()));
        } else if (!VALID_LOGIN_REGEX.matcher(alias).find()) {
            throw new IllegalFieldException(IllegalFieldExceptionResponse.fromValidationResult(ValidationResult.builder()
                    .fieldName(ALIAS_FIELD_NAME)
                    .description(ValidationPresets.formatIsIncorrect(ALIAS_FIELD_NAME))
                    .build()));
        }
        WorkerDisplayForm worker = workerService.getDisplayWorkerById(workerId);

        T databaseConfigs = DatabaseModel.deserialize(configs, databaseType);

        Stack<ValidationResult> validationResults = databaseConfigs.validate();
        if (!validationResults.isEmpty()) {
            throw new IllegalFieldException(IllegalFieldExceptionResponse.fromValidationResult(validationResults));
        }

        DatabaseModel databaseModel = DatabaseModel.builder()
                .configs(databaseConfigs)
                .userId(userId)
                .workerId(workerId)
                .alias(alias)
                .connectionState(ConnectionState.DISCONNECTED)
                .build();

        DatabaseModel fromDb = databaseRepository.save(databaseModel);
        requestCheckDatabase(fromDb);
        return fromDb;
    }

    public List<DatabaseModel> getUsersDatabases(Integer userId) {
        return databaseRepository.getAllByUser(userId);
    }

    public DatabaseModel getDatabaseByIdAndUser(Integer userId, Integer id) {
        return databaseRepository.getByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException(new EntityNotFoundExceptionResponse(
                        ENTITY_NAME, String.format("Database with id %d for user %d is not found", id, userId))));
    }

    public <T extends DatabaseConfigs<? extends DataSource>> DatabaseModel updateDatabase(Integer userId, String configs, Integer id, Integer workerId, String alias) {
        if (Objects.nonNull(alias) && !VALID_LOGIN_REGEX.matcher(alias).find()) {
            throw new IllegalFieldException(IllegalFieldExceptionResponse.fromValidationResult(ValidationResult.builder()
                    .fieldName(ALIAS_FIELD_NAME)
                    .description(ValidationPresets.formatIsIncorrect(ALIAS_FIELD_NAME))
                    .build()));
        }
        if (Objects.nonNull(workerId)) {
            WorkerDisplayForm worker = workerService.getDisplayWorkerById(workerId);
        }
        DatabaseModel existing = databaseRepository
                .getByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException(new EntityNotFoundExceptionResponse(
                        ENTITY_NAME, String.format("Database with id %d is not found", id))));

        T databaseConfigs = Objects.nonNull(configs) ? DatabaseModel.deserialize(configs, existing.getType()) : null;

        Stack<ValidationResult> validationResults = databaseConfigs.validate();
        if (!validationResults.isEmpty()) {
            throw new IllegalFieldException(IllegalFieldExceptionResponse.fromValidationResult(validationResults));
        }

        DatabaseModel databaseModel = DatabaseModel.builder()
                .userId(userId)
                .id(id)
                .build();

        databaseModel.setAlias(Objects.nonNull(alias) ? alias : existing.getAlias());
        databaseModel.setWorkerId(Objects.nonNull(workerId) ? workerId : existing.getWorkerId());
        databaseModel.setConfigs(Objects.nonNull(configs) ? configs : existing.getConfigs());
        databaseModel.setType(existing.getType());
        databaseModel.setConnectionState(ConnectionState.DISCONNECTED);

        Condition.of(databaseRepository.update(databaseModel))
                .ifFalseThrow(() -> new UnsuccessfulOperationException(new UnsuccessfulOperationExceptionResponse(ENTITY_NAME,
                        String.format("There was an exception while updating database configs to database; DatabaseId: %d, UserId: %d", id, userId))));

        requestCheckDatabase(databaseModel);

        return databaseModel;
    }

    public DatabaseModel updateConnectionState(Integer userId, Integer id, ConnectionState connectionState) {
        DatabaseModel existing = databaseRepository
                .getByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException(new EntityNotFoundExceptionResponse(
                        ENTITY_NAME, String.format("Database with id %d is not found", id))));
        existing.setConnectionState(connectionState);
        Condition.of(databaseRepository.update(existing))
                .ifFalseThrow(() -> new UnsuccessfulOperationException(new UnsuccessfulOperationExceptionResponse(ENTITY_NAME,
                        String.format("There was an exception while updating database configs to database; DatabaseId: %d, UserId: %d", id, userId))));
        return existing;
    }

    public void delete(Integer userId, Integer id) {
        try {
            testIterationWebhookService.deleteByDatabaseId(id, userId);
        } catch (UnsuccessfulOperationException ignored) {}
        Condition.of(databaseRepository.deleteByUser(id, userId))
                .ifFalseThrow(() -> new UnsuccessfulOperationException(new UnsuccessfulOperationExceptionResponse(ENTITY_NAME,
                        String.format("There was an exception while writing to database; DatabaseId: %d; UserId: %d", id, userId))));
    }

    public int haveWorkerConnectedDatabases(Integer workerId) {
        return databaseRepository.getAllByWorkerId(workerId).size();
    }


    public void requestCheckDatabase(DatabaseModel databaseModel){
        Payload payload = Payload.builder()
                .endpoint("databasePing")
                .body(databaseModel)
                .build();
        workerService.sendMessage(databaseModel.getWorkerId(), payload);
    }

    public void requestCheckDatabases(Integer userId){
        databaseRepository
                .getAllByUser(userId)
                .forEach(this::requestCheckDatabase);
    }
}
