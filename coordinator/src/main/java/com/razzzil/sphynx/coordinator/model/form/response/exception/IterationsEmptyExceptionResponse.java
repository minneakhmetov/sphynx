package com.razzzil.sphynx.coordinator.model.form.response.exception;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class IterationsEmptyExceptionResponse extends ExceptionResponse {

    private static final String CODE_FIELD = "000008";

    private String description;

    public IterationsEmptyExceptionResponse(String description) {
        super(CODE_FIELD);
        this.description = description;
    }
}
