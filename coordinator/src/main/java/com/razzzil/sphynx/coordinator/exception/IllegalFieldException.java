package com.razzzil.sphynx.coordinator.exception;

import com.razzzil.sphynx.coordinator.model.form.response.exception.IllegalFieldExceptionResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class IllegalFieldException extends RuntimeException {

    private List<IllegalFieldExceptionResponse> illegalFieldExceptionResponses;

    public IllegalFieldException(IllegalFieldExceptionResponse illegalFieldExceptionResponses) {
        this(Collections.singletonList(illegalFieldExceptionResponses));
    }
}
