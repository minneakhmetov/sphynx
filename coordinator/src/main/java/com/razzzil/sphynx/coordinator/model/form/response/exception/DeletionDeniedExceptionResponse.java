package com.razzzil.sphynx.coordinator.model.form.response.exception;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class DeletionDeniedExceptionResponse extends ExceptionResponse {

    private static final String CODE_FIELD = "000007";

    private String description;
    private String entityName;

    public DeletionDeniedExceptionResponse() {
        super(CODE_FIELD);
    }

    public DeletionDeniedExceptionResponse(String entityName, String description) {
        super(CODE_FIELD);
        this.entityName = entityName;
        this.description = description;
    }
}
