package com.mm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler({ProductAlreadyExistsEx.class, InsufficientQuantityEx.class, ReserveAlreadyExistsEx.class})
    public ResponseEntity<RestApiError> conflictHandler(Exception ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler({ProductNotFoundEx.class, ReservationNotFoundEx.class})
    public ResponseEntity<RestApiError> notFoundHandler(Exception ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestApiError> validationHandler(MethodArgumentNotValidException ex) {
        List<String> messages = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList();
        return build(HttpStatus.BAD_REQUEST, messages);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RestApiError> malformedJsonHandler(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, "Malformed or unreadable request body.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestApiError> handleGeneric(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again later.");
    }

    private ResponseEntity<RestApiError> build(HttpStatus status, String message) {
        String safeMessage = Objects.requireNonNullElse(message, status.getReasonPhrase());
        return build(status, List.of(safeMessage));
    }

    private ResponseEntity<RestApiError> build(HttpStatus status, List<String> messages) {
        RestApiError error = RestApiError.builder()
                .code(status.value())
                .status(status)
                .timeStamp(LocalDateTime.now())
                .errors(messages)
                .build();

        return ResponseEntity.status(status).body(error);
    }
}