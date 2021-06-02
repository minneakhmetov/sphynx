package com.razzzil.sphynx.coordinator.model.form.response.exception;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class TokenIsExpiredExceptionResponse extends ExceptionResponse {

    private static final String CODE_FIELD = "000003";

    private String description;

    public TokenIsExpiredExceptionResponse() {
        super(CODE_FIELD);
        this.description = "Token is expired. Please, request a new one by signing in.";
    }

    public TokenIsExpiredExceptionResponse(String description) {
        super(CODE_FIELD);
        this.description = description;
    }
}
