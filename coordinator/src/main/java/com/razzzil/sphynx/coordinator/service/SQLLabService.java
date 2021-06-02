package com.razzzil.sphynx.coordinator.service;

import com.razzzil.sphynx.commons.model.database.DatabaseModel;
import com.razzzil.sphynx.commons.model.payload.Payload;
import com.razzzil.sphynx.commons.model.savedquery.RequestQueryForm;
import com.razzzil.sphynx.commons.model.savedquery.SQLLabState;
import com.razzzil.sphynx.commons.model.savedquery.SavedQueryModel;
import com.razzzil.sphynx.commons.util.Condition;
import com.razzzil.sphynx.commons.validation.ValidationResult;
import com.razzzil.sphynx.coordinator.exception.DeletionDeniedException;
import com.razzzil.sphynx.coordinator.exception.EntityNotFoundException;
import com.razzzil.sphynx.coordinator.exception.IllegalFieldException;
import com.razzzil.sphynx.coordinator.exception.UnsuccessfulOperationException;
import com.razzzil.sphynx.coordinator.model.form.request.query.SQLLabQueryForm;
import com.razzzil.sphynx.coordinator.model.form.response.exception.DeletionDeniedExceptionResponse;
import com.razzzil.sphynx.coordinator.model.form.response.exception.EntityNotFoundExceptionResponse;
import com.razzzil.sphynx.coordinator.model.form.response.exception.IllegalFieldExceptionResponse;
import com.razzzil.sphynx.coordinator.model.form.response.exception.UnsuccessfulOperationExceptionResponse;
import com.razzzil.sphynx.coordinator.repository.IterationRepository;
import com.razzzil.sphynx.coordinator.repository.SavedQueriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Stack;

@Service
public class SQLLabService {

    private static final String ENTITY_NAME = "SAVED_QUERY";

    @Autowired
    private SavedQueriesRepository savedQueriesRepository;

    @Autowired
    private IterationRepository iterationRepository;

    @Autowired
    private WorkerService workerService;

    @Autowired
    private DatabaseService databaseService;

    public List<SavedQueryModel> getSavedQueries(Integer userId) {
        return savedQueriesRepository.getSavedQueriesByUserId(userId);
    }

    public SavedQueryModel getDetailed(Integer id, Integer userId) {
        return savedQueriesRepository.getDetailedSavedQueriesByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException(new EntityNotFoundExceptionResponse(
                        ENTITY_NAME, String.format("Saved Query with id %d and user id %s is not found", id, userId))));
    }

    public SavedQueryModel getSavedQueryByIdAndUserId(Integer id, Integer userId) {
        return savedQueriesRepository.getSavedQueriesByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException(new EntityNotFoundExceptionResponse(
                        ENTITY_NAME, String.format("Saved Query with id %d and user id %s is not found", id, userId))));
    }

    public void deleteById(Integer id, Integer userId) {
        Integer connectedIterations = iterationRepository.haveSavedQueryConnectedIterations(id, userId)
                .orElseThrow(() -> new UnsuccessfulOperationException(new UnsuccessfulOperationExceptionResponse(ENTITY_NAME,
                        String.format("There was an exception while deleting query state to database; UserId: %d, Id: %d", userId, id))));
        if (connectedIterations > 0){
            throw new DeletionDeniedException(new DeletionDeniedExceptionResponse(ENTITY_NAME,
                    String.format("Query %d cannot be deleted because %d databases depend on it", id, connectedIterations)));
        }
        Condition.of(savedQueriesRepository.deleteByIdAndUserId(id, userId))
                .ifFalseThrow(() -> new UnsuccessfulOperationException(new UnsuccessfulOperationExceptionResponse(ENTITY_NAME,
                        String.format("There was an exception while deleting query state to database; UserId: %d, Id: %d", userId, id))));
    }

    public SavedQueryModel query(SQLLabQueryForm form, Integer userId) {
        Stack<ValidationResult> validationResults = form.validate();
        if (!validationResults.isEmpty()) {
            throw new IllegalFieldException(IllegalFieldExceptionResponse.fromValidationResult(validationResults));
        }
        savedQueriesRepository.deleteUnused(userId);
        DatabaseModel databaseModel = databaseService.getDatabaseByIdAndUser(userId, form.getDatabaseId());
        SavedQueryModel savedQueryModel = SavedQueryModel.builder()
                .databaseId(databaseModel.getId())
                .userId(userId)
                .sql(form.getSql())
                .state(SQLLabState.PROCESSING)
                .build();
        savedQueryModel.setId(savedQueriesRepository.save(savedQueryModel).getId());
        Payload payload = Payload.builder()
                .endpoint("query")
                .body(RequestQueryForm.builder()
                        .databaseModel(databaseModel)
                        .savedQueryModel(savedQueryModel)
                        .build())
                .build();
        workerService.sendMessage(databaseModel.getWorkerId(), payload);
        return savedQueryModel;
    }

    public SavedQueryModel updateState(SavedQueryModel savedQueryModel) {
        Condition.of(savedQueriesRepository.update(savedQueryModel))
                .ifFalseThrow(() -> new UnsuccessfulOperationException(new UnsuccessfulOperationExceptionResponse(ENTITY_NAME,
                        String.format("There was an exception while updating query state to database; DatabaseId: %d, UserId: %d, Id: %d",
                                savedQueryModel.getDatabaseId(), savedQueryModel.getUserId(), savedQueryModel.getId()))));
        return savedQueryModel;
    }

    public void saveQuery(Integer id, Integer userId, String name) {
        Condition.of(savedQueriesRepository.updateSaved(id, userId, name))
                .ifFalseThrow(() -> new UnsuccessfulOperationException(new UnsuccessfulOperationExceptionResponse(ENTITY_NAME,
                        String.format("There was an exception while updating query state to database; UserId: %d, Id: %d", userId, id))));

    }


}
