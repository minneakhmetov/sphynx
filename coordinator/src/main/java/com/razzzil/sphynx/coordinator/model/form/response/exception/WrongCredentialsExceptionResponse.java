package com.razzzil.sphynx.coordinator.model.form.response.exception;


import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class WrongCredentialsExceptionResponse extends ExceptionResponse {

    private static final String CODE_FIELD = "000004";

    private String description;

    public WrongCredentialsExceptionResponse(String description) {
        super(CODE_FIELD);
        this.description = description;
    }

    public WrongCredentialsExceptionResponse() {
        super(CODE_FIELD);
    }
}
