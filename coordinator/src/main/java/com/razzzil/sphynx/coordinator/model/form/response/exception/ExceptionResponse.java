package com.razzzil.sphynx.coordinator.model.form.response.exception;

import lombok.*;

@Getter
public class ExceptionResponse {
    private String code;
    private String name;

    public ExceptionResponse(String code) {
        this.code = code;
        this.name = getClass().getSimpleName().replace("Response", "");
    }
}
