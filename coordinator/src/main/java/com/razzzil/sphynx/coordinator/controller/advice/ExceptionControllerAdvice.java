package com.razzzil.sphynx.coordinator.controller.advice;

import com.razzzil.sphynx.coordinator.exception.*;
import com.razzzil.sphynx.coordinator.model.form.response.exception.ExceptionPayload;
import com.razzzil.sphynx.coordinator.model.form.response.exception.IllegalFieldExceptionResponse;
import com.razzzil.sphynx.coordinator.model.form.response.exception.TokenIsExpiredExceptionResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(IllegalFieldException.class)
    public ResponseEntity<ExceptionPayload<?>> illegalFieldHandler(IllegalFieldException exception){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionPayload.asList(exception.getIllegalFieldExceptionResponses()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionPayload<?>> userNotFoundHandler(EntityNotFoundException exception){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ExceptionPayload.singletonError(exception.getUserNotFoundExceptionResponse()));
    }

    @ExceptionHandler({WrongCredentialsException.class})
    public ResponseEntity<ExceptionPayload<?>> wrongCredentialsHandler(WrongCredentialsException exception){
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ExceptionPayload.singletonError(exception.getWrongCredentialsExceptionResponse()));
    }

    @ExceptionHandler({ExpiredJwtException.class, MalformedJwtException.class})
    public ResponseEntity<ExceptionPayload<?>> tokenIsExpiredHandler(JwtException exception){
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ExceptionPayload.singletonError(new TokenIsExpiredExceptionResponse()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionPayload<?>> methodArgumentTypeMismatchHandler(MethodArgumentTypeMismatchException exception){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionPayload.singletonError(
                        new IllegalFieldExceptionResponse(exception.getName(), String.format("Illegal value: %s", exception.getValue()))));
    }

    @ExceptionHandler(UnsuccessfulOperationException.class)
    public ResponseEntity<ExceptionPayload<?>> methodArgumentTypeMismatchHandler(UnsuccessfulOperationException exception){
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionPayload.singletonError(exception.getUnsuccessfulOperationExceptionResponse()));
    }

    @ExceptionHandler(WorkerNotAvailableException.class)
    public ResponseEntity<ExceptionPayload<?>> workerNotAvailableHandler(WorkerNotAvailableException exception){
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ExceptionPayload.singletonError(exception.getWorkerNotAvailableExceptionResponse()));
    }

    @ExceptionHandler(DeletionDeniedException.class)
    public ResponseEntity<ExceptionPayload<?>> deletionDeniedHandler(DeletionDeniedException exception){
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ExceptionPayload.singletonError(exception.getDeletionDeniedExceptionResponse()));
    }

    @ExceptionHandler(TestHasBeenAlreadyProcessingException.class)
    public ResponseEntity<ExceptionPayload<?>> testHasBeenAlreadyProcessingHandler(TestHasBeenAlreadyProcessingException exception){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionPayload.singletonError(exception.getTestHasBeenAlreadyProcessingExceptionResponse()));
    }

    @ExceptionHandler(IterationsEmptyException.class)
    public ResponseEntity<ExceptionPayload<?>> testHasBeenAlreadyProcessingHandler(IterationsEmptyException exception){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionPayload.singletonError(exception.getEmptyExceptionResponse()));
    }



}
