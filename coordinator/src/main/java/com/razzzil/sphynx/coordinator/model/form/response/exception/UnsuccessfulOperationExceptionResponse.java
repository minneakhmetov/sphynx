package com.razzzil.sphynx.coordinator.model.form.response.exception;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class UnsuccessfulOperationExceptionResponse extends ExceptionResponse {

    private static final String CODE_FIELD = "000005";
    private String entityName;
    private String description;

    public UnsuccessfulOperationExceptionResponse() {
        super(CODE_FIELD);
    }

    public UnsuccessfulOperationExceptionResponse(String entityName, String description) {
        super(CODE_FIELD);
        this.entityName = entityName;
        this.description = description;
    }
}
