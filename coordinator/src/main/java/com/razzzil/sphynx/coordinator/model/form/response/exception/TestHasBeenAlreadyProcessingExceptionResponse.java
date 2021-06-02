package com.razzzil.sphynx.coordinator.model.form.response.exception;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class TestHasBeenAlreadyProcessingExceptionResponse extends ExceptionResponse {

    private String description;
    private static final String CODE_FIELD = "000008";

    public TestHasBeenAlreadyProcessingExceptionResponse(Integer testId) {
        super(CODE_FIELD);
        this.description = String.format("Test %d has been already processing", testId);
    }
}
