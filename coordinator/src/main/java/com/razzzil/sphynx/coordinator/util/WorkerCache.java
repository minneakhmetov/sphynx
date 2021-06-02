package com.razzzil.sphynx.coordinator.util;

import com.razzzil.sphynx.coordinator.exception.EntityNotFoundException;
import com.razzzil.sphynx.coordinator.model.form.request.worker.WorkerDisplayForm;
import com.razzzil.sphynx.coordinator.model.form.request.worker.Worker;
import com.razzzil.sphynx.coordinator.model.form.response.exception.EntityNotFoundExceptionResponse;
import com.razzzil.sphynx.coordinator.service.WorkerService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope("singleton")
public class WorkerCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerCache.class);

    @Getter
    private final Map<Integer, Worker> workers;

    @Autowired
    public WorkerCache(WorkerService workerService) {
        LOGGER.info("Initializing workers");
        this.workers = new HashMap<>();
        workerService
                .getWorkersConfiguration()
                .forEach(worker -> workers.put(worker.getWorkerModel().getId(), worker));
        LOGGER.info("{} workers in configuration", workers.size());
    }

    public List<WorkerDisplayForm> getDisplayWorkers() {
        return workers
                .entrySet()
                .stream()
                .map(entry -> WorkerDisplayForm
                        .builder()
                        .alias(entry.getValue().getWorkerModel().getAlias())
                        .id(entry.getValue().getWorkerModel().getId())
                        .version(entry.getValue().getWorkerModel().getVersion())
                        .connectionState(entry.getValue().getConnectionState())
                        .build())
                .collect(Collectors.toList());
    }

    public Worker addWorker(Worker worker) {
        return workers.put(worker.getWorkerModel().getId(), worker);
    }

    public void updateWorker(Worker worker) {
        addWorker(worker);
    }

    public Worker deleteById(Integer id) {
        return workers.remove(id);
    }

    public Worker getById(Integer id) {
        return Optional
                .ofNullable(workers.get(id))
                .orElseThrow(() -> new EntityNotFoundException(new EntityNotFoundExceptionResponse(
                        WorkerService.ENTITY_NAME, String.format("Worker with id %d is not found", id))));
    }

    public WorkerDisplayForm getDisplayFormById(Integer id) {
        Worker worker = getById(id);
        return WorkerDisplayForm.builder()
                .alias(worker.getWorkerModel().getAlias())
                .id(worker.getWorkerModel().getId())
                .connectionState(worker.getConnectionState())
                .version(worker.getWorkerModel().getVersion())
                .build();

    }


}
