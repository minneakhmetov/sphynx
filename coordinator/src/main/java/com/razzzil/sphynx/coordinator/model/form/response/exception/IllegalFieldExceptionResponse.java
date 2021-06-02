package com.razzzil.sphynx.coordinator.model.form.response.exception;

import com.razzzil.sphynx.commons.validation.ValidationResult;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class IllegalFieldExceptionResponse extends ExceptionResponse {

    private static final String CODE_FIELD = "000002";

    private String fieldName;
    private String description;

    public IllegalFieldExceptionResponse() {
        super(CODE_FIELD);
    }

    public IllegalFieldExceptionResponse(String fieldName, String description) {
        super(CODE_FIELD);
        this.fieldName = fieldName;
        this.description = description;
    }

    public IllegalFieldExceptionResponse(ValidationResult validationResult){
        this(validationResult.getFieldName(), validationResult.getDescription());
    }

    public static IllegalFieldExceptionResponse fromValidationResult(ValidationResult validationResult){
        return new IllegalFieldExceptionResponse(validationResult);
    }

    public static List<IllegalFieldExceptionResponse> fromValidationResult(Collection<ValidationResult> validationResult){
        return validationResult
                .stream()
                .map(IllegalFieldExceptionResponse::new)
                .collect(Collectors.toList());
    }




}
