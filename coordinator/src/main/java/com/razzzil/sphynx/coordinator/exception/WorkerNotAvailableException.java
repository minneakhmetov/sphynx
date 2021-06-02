package com.razzzil.sphynx.coordinator.exception;

import com.razzzil.sphynx.coordinator.model.form.request.worker.Worker;
import com.razzzil.sphynx.coordinator.model.form.response.exception.WorkerNotAvailableExceptionResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class WorkerNotAvailableException extends RuntimeException {

    private WorkerNotAvailableExceptionResponse workerNotAvailableExceptionResponse;

    public WorkerNotAvailableException(Worker worker) {
        this.workerNotAvailableExceptionResponse = new WorkerNotAvailableExceptionResponse(worker);
    }




}
