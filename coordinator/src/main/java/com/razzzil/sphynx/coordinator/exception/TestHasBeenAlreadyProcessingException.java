package com.razzzil.sphynx.coordinator.exception;

import com.razzzil.sphynx.coordinator.model.form.response.exception.TestHasBeenAlreadyProcessingExceptionResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TestHasBeenAlreadyProcessingException extends RuntimeException {

    private TestHasBeenAlreadyProcessingExceptionResponse testHasBeenAlreadyProcessingExceptionResponse;
}
