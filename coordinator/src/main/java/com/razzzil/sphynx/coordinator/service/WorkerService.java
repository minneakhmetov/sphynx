package com.razzzil.sphynx.coordinator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.razzzil.sphynx.commons.model.key.WorkerCredential;
import com.razzzil.sphynx.commons.model.payload.Payload;
import com.razzzil.sphynx.commons.model.worker.SupportedVersions;
import com.razzzil.sphynx.commons.model.worker.WorkerConfigurationModel;
import com.razzzil.sphynx.commons.model.worker.WorkerModel;
import com.razzzil.sphynx.commons.model.worker.connectionstate.ConnectionState;
import com.razzzil.sphynx.commons.util.Condition;
import com.razzzil.sphynx.commons.util.KeyUtil;
import com.razzzil.sphynx.commons.validation.ValidationResult;
import com.razzzil.sphynx.coordinator.exception.*;
import com.razzzil.sphynx.coordinator.model.form.request.worker.*;
import com.razzzil.sphynx.coordinator.model.form.response.exception.DeletionDeniedExceptionResponse;
import com.razzzil.sphynx.coordinator.model.form.response.exception.EntityNotFoundExceptionResponse;
import com.razzzil.sphynx.coordinator.model.form.response.exception.IllegalFieldExceptionResponse;
import com.razzzil.sphynx.coordinator.model.form.response.exception.UnsuccessfulOperationExceptionResponse;
import com.razzzil.sphynx.coordinator.repository.WorkerRepository;
import com.razzzil.sphynx.coordinator.util.WorkerCache;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WorkerService {

    public static final String ENTITY_NAME = "Worker";

    @Value("${sphynx.host}")
    private String host;

    @Autowired
    private WorkerRepository workerRepository;
    @Autowired
    private WorkerCache workerCache;
    @Autowired
    @Qualifier("socketObjectMapper")
    private ObjectMapper objectMapper;

    @Autowired
    private DatabaseService databaseService;

    public WorkerDisplayForm getDisplayWorkerById(Integer id) {
        return workerCache.getDisplayFormById(id);
    }

    public List<WorkerDisplayForm> getDisplayWorkers() {
        return workerCache.getDisplayWorkers();
    }

    public List<WorkerModel> getWorkerModels() {
        return workerRepository.getAllWorkers();
    }

    public List<Worker> getWorkersConfiguration() {
        return workerRepository
                .getAllWorkers()
                .stream()
                .map(workerModel -> new Worker(workerModel, ConnectionState.DISCONNECTED))
                .collect(Collectors.toList());
    }

    public List<Worker> getWorkers(){
        return List.copyOf(workerCache.getWorkers().values());
    }

    public WorkerModel save(WorkerForm workerForm) {
        Stack<ValidationResult> validationResults = workerForm.validate();
        if (!validationResults.isEmpty()) {
            throw new IllegalFieldException(IllegalFieldExceptionResponse.fromValidationResult(validationResults));
        }
        SecretKey key = KeyUtil.generateKey();
        WorkerModel toDb = WorkerModel.builder()
                .alias(workerForm.getAlias())
                .key(KeyUtil.serializeKey(key))
                .version(workerForm.getVersion())
                .token(UUID.randomUUID().toString())
                .build();
        WorkerModel fromDb = workerRepository.save(toDb);
        WorkerCredential workerCredential = WorkerCredential.builder()
                .key(key)
                .coordinatorHost(host)
                .workerConfigurationModel(WorkerConfigurationModel.builder()
                        .token(fromDb.getToken())
                        .id(fromDb.getId())
                        .version(fromDb.getVersion())
                        .build())
                .build();
        Worker worker = Worker.builder()
                .workerModel(fromDb)
                .connectionState(ConnectionState.DISCONNECTED)
                .secretKey(key)
                .sessionKey(null)
                .socket(null)
                .build();
        workerCache.addWorker(worker);
        return WorkerModel.builder()
                .alias(fromDb.getAlias())
                .key(workerCredential.serialize())
                .id(fromDb.getId())
                .version(fromDb.getVersion())
                .build();
    }

    @SneakyThrows
    public WorkerDisplayForm update(WorkerForm workerForm, Integer id) {
        Stack<ValidationResult> validationResults = workerForm.validate();
        if (!validationResults.isEmpty()) {
            throw new IllegalFieldException(IllegalFieldExceptionResponse.fromValidationResult(validationResults));
        }
        WorkerModel existing = workerRepository.getById(id)
                .orElseThrow(() -> new EntityNotFoundException(new EntityNotFoundExceptionResponse(
                        ENTITY_NAME, String.format("Worker with id %d is not found", id))));

        Worker worker = workerCache.getById(existing.getId());

        existing.setAlias(Objects.nonNull(workerForm.getAlias()) ? workerForm.getAlias() : existing.getAlias());
        existing.setVersion(Objects.nonNull(workerForm.getVersion()) ? workerForm.getVersion() : existing.getVersion());
        worker.setWorkerModel(existing);

        Condition.of(workerRepository.update(existing))
                .ifFalseThrow(() -> new UnsuccessfulOperationException(new UnsuccessfulOperationExceptionResponse(ENTITY_NAME,
                        String.format("There was an exception while updating worker configs to database; Worker: %d", existing.getId()))));

        return WorkerDisplayForm.builder()
                .alias(existing.getAlias())
                .connectionState(worker.getConnectionState())
                .id(existing.getId())
                .version(existing.getVersion())
                .build();
    }

    public boolean isAlive(Integer workerId){
        return ConnectionState.CONNECTED.equals(workerCache.getById(workerId).getConnectionState());
    }

//    @SneakyThrows
//    public WorkerRenameForm renameWorker(WorkerRenameForm workerRenameForm) {
//        Stack<ValidationResult> validationResults = workerRenameForm.validate();
//        if (!validationResults.isEmpty()) {
//            throw new IllegalFieldException(IllegalFieldExceptionResponse.fromValidationResult(validationResults));
//        }
//        WorkerModel existing = workerRepository.getById(workerRenameForm.getId())
//                .orElseThrow(() -> new EntityNotFoundException(new EntityNotFoundExceptionResponse(
//                        ENTITY_NAME, String.format("Worker with id %d is not found", workerRenameForm.getId()))));
//
//        Worker worker = workerCache.getById(existing.getId());
//        Payload payload = Payload.builder()
//                .endpoint("renameWorker")
//                .body(workerRenameForm)
//                .build();
//        sendMessage(worker, payload);
//
//        WorkerModel toDb = WorkerModel.builder()
//                .id(workerRenameForm.getId())
//                .alias(workerRenameForm.getAlias())
//                .key(existing.getKey())
//                .version(existing.getVersion())
//                .build();
//
//        Condition.of(workerRepository.update(toDb))
//                .ifFalseThrow(() -> new UnsuccessfulOperationException(new UnsuccessfulOperationExceptionResponse(ENTITY_NAME,
//                        String.format("There was an exception while updating worker configs to database; Worker: %d", toDb.getId()))));
//
//        worker.setWorkerModel(toDb);
//
//        return workerRenameForm;
//    }
//
//    public WorkerChangeVersionForm changeVersion(WorkerChangeVersionForm workerChangeVersionForm) {
//        Stack<ValidationResult> validationResults = workerChangeVersionForm.validate();
//        if (!validationResults.isEmpty()) {
//            throw new IllegalFieldException(IllegalFieldExceptionResponse.fromValidationResult(validationResults));
//        }
//        WorkerModel existing = workerRepository.getById(workerChangeVersionForm.getId())
//                .orElseThrow(() -> new EntityNotFoundException(new EntityNotFoundExceptionResponse(
//                        ENTITY_NAME, String.format("Worker with id %d is not found", workerChangeVersionForm.getId()))));
//        Worker worker = workerCache.getById(existing.getId());
//        Payload payload = Payload.builder()
//                .endpoint("changeVersion")
//                .body(workerChangeVersionForm)
//                .build();
//        sendMessage(worker, payload);
//
//        WorkerModel toDb = WorkerModel.builder()
//                .id(workerChangeVersionForm.getId())
//                .alias(existing.getKey())
//                .key(existing.getKey())
//                .version(workerChangeVersionForm.getVersion())
//                .build();
//
//        Condition.of(workerRepository.update(toDb))
//                .ifFalseThrow(() -> new UnsuccessfulOperationException(new UnsuccessfulOperationExceptionResponse(ENTITY_NAME,
//                        String.format("There was an exception while updating worker configs to database; Worker: %d", toDb.getId()))));
//
//        worker.setWorkerModel(toDb);
//
//        return workerChangeVersionForm;
//    }


    @SneakyThrows
    public WorkerModel resetKey(Integer id) {
        WorkerModel existing = workerRepository.getById(id)
                .orElseThrow(() -> new EntityNotFoundException(new EntityNotFoundExceptionResponse(
                        ENTITY_NAME, String.format("Worker with id %d is not found", id))));

        SecretKey key = KeyUtil.generateKey();
        WorkerModel toDb = WorkerModel.builder()
                .alias(existing.getAlias())
                .key(KeyUtil.serializeKey(key))
                .id(id)
                .version(existing.getVersion())
                .token(existing.getToken())
                .build();

        Condition.of(workerRepository.update(toDb))
                .ifFalseThrow(() -> new UnsuccessfulOperationException(new UnsuccessfulOperationExceptionResponse(ENTITY_NAME,
                        String.format("There was an exception while updating worker configs to database; Worker: %d", toDb.getId()))));

        WorkerCredential workerCredential = WorkerCredential.builder()
                .key(key)
                .coordinatorHost(host)
                .workerConfigurationModel(WorkerConfigurationModel.builder()
                        .token(toDb.getToken())
                        .id(toDb.getId())
                        .version(toDb.getVersion())
                        .build())
                .build();

        Worker worker = workerCache.getById(toDb.getId());
        worker.setSecretKey(key);
        worker.setWorkerModel(toDb);
        worker.close();

        toDb.setKey(workerCredential.serialize());
        return toDb;
    }

    public void delete(Integer id) {
        int connectedDatabases = databaseService.haveWorkerConnectedDatabases(id);
        Condition.of(connectedDatabases == 0)
                .ifFalseThrow(() -> new DeletionDeniedException(new DeletionDeniedExceptionResponse(ENTITY_NAME,
                        String.format("Worker %d cannot be deleted because %d databases depend on it", id, connectedDatabases))));

        workerCache.deleteById(id);
//        Condition.isNull(workerCache.deleteById(id))
//                .ifFalseThrow(() -> new UnsuccessfulOperationException(new UnsuccessfulOperationExceptionResponse(ENTITY_NAME,
//                        String.format("There was an exception while deleting worker from cache; Worker: %d", id))));
        Condition.of(workerRepository.delete(id))
                .ifFalseThrow(() -> new UnsuccessfulOperationException(new UnsuccessfulOperationExceptionResponse(ENTITY_NAME,
                        String.format("There was an exception while deleting worker; Worker: %d", id))));
    }

    @SneakyThrows
    public void sendMessage(Worker worker, Payload payload) {
        if (worker.isConnectionAlive()) {
            payload.setSessionKey(worker.getSessionKey());
            String encrypted = KeyUtil.encrypt(objectMapper.writeValueAsString(payload), worker.getSecretKey());
            PrintWriter out = new PrintWriter(worker.getSocket().getOutputStream(), true);
            out.println(encrypted);
        } else throw new WorkerNotAvailableException(worker);
    }

    @SneakyThrows
    public void sendMessage(Integer workerId, Payload payload) {
        sendMessage(workerCache.getById(workerId), payload);
    }

//    public Payload sendMessageWithResponse(Integer workerId, Payload payload) {
//        return sendMessageWithResponse(workerCache.getById(workerId), payload);
//    }
//
//    @SneakyThrows
//    public Payload sendMessageWithResponse(Worker worker, Payload payload) {
//        if (worker.isConnectionAlive()) {
//            payload.setSessionKey(worker.getSessionKey());
//            String encrypted = KeyUtil.encrypt(objectMapper.writeValueAsString(payload), worker.getSecretKey());
//            PrintWriter out = new PrintWriter(worker.getSocket().getOutputStream(), true);
//            out.println(encrypted);
//        } else throw new WorkerNotAvailableException(worker);
//    }

    public List<String> versions(){
        return SupportedVersions.getVersions();
    }

}
