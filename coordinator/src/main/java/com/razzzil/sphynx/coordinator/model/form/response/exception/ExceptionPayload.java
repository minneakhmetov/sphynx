package com.razzzil.sphynx.coordinator.model.form.response.exception;

import lombok.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExceptionPayload<T extends ExceptionResponse> {
    private List<T> error;

    public static <T extends ExceptionResponse> ExceptionPayload<T> singletonError(T exceptionResponse){
        return new ExceptionPayload<T>(Collections.singletonList(exceptionResponse));
    }

    @SafeVarargs
    public static  <T extends ExceptionResponse> ExceptionPayload<T> asList(T... exceptionResponses){
        return new ExceptionPayload<T>(Arrays.asList(exceptionResponses));
    }

    public static <T extends ExceptionResponse> ExceptionPayload<T> asList(List<T> exceptionResponses){
        return new ExceptionPayload<T>(exceptionResponses);
    }

}
