package com.razzzil.sphynx.coordinator.model.form.response.exception;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class EntityNotFoundExceptionResponse extends ExceptionResponse {

    private static final String CODE_FIELD = "000001";

    private String description;
    private String entityName;

    public EntityNotFoundExceptionResponse() {
        super(CODE_FIELD);
    }

    public EntityNotFoundExceptionResponse(String entityName, String description) {
        super(CODE_FIELD);
        this.entityName = entityName;
        this.description = description;
    }
}
