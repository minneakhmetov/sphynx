package com.razzzil.sphynx.coordinator.model.form.response.exception;

import com.razzzil.sphynx.coordinator.model.form.request.worker.Worker;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class WorkerNotAvailableExceptionResponse extends ExceptionResponse {

    private static final String CODE_FIELD = "000006";
    private Integer id;
    private String alias;
    private String description;

    public WorkerNotAvailableExceptionResponse() {
        super(CODE_FIELD);
    }

    public WorkerNotAvailableExceptionResponse(Worker worker) {
        super(CODE_FIELD);
        this.id = worker.getWorkerModel().getId();
        this.alias = worker.getWorkerModel().getAlias();
        this.description = String.format("Worker %s with id %d is not available", alias, id);
    }

    public WorkerNotAvailableExceptionResponse(Integer id, String alias) {
        super(CODE_FIELD);
        this.id = id;
        this.alias = alias;
        this.description = String.format("Worker %s with id %d is not available", alias, id);
    }

    public WorkerNotAvailableExceptionResponse(Integer id, String alias, String description) {
        super(CODE_FIELD);
        this.id = id;
        this.alias = alias;
        this.description = description;
    }
}
