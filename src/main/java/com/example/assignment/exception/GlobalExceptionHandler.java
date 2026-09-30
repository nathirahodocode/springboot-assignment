package com.example.assignment.exception;

import com.example.assignment.dto.response.ApiErrorResponse;
import com.example.assignment.util.CorrelationIdHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ---------- Business Exceptions ----------

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        log.warn("Resource not found: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req, null);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicate(DuplicateResourceException ex, HttpServletRequest req) {
        log.warn("Duplicate resource: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, ex.getMessage(), req, null);
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ApiErrorResponse> handleExternal(ExternalServiceException ex, HttpServletRequest req) {
        log.error("External service failure: {}", ex.getMessage(), ex);
        return build(HttpStatus.BAD_GATEWAY, ex.getMessage(), req, null);
    }

    // ---------- Validation Exceptions ----------

    /** Classic @Valid on @RequestBody (Spring Boot 3.x). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpServletRequest req) {

        Map<String, String> errors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        log.warn("Validation failed (MethodArgumentNotValid): {}", errors);
        return build(HttpStatus.BAD_REQUEST, "Validation failed", req, errors);
    }

    /** Constraint violations on @RequestParam / @PathVariable. */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest req) {

        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(cv ->
                errors.put(cv.getPropertyPath().toString(), cv.getMessage()));
        log.warn("Constraint violation: {}", errors);
        return build(HttpStatus.BAD_REQUEST, "Validation failed", req, errors);
    }

    // ---------- Catch-all ----------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex, HttpServletRequest req) {
        String className = ex.getClass().getSimpleName();
        log.error("Unhandled exception: {} — {}", ex.getClass().getName(), ex.getMessage(), ex);

        // Spring Boot 4 throws ValidationException subtypes — treat them as 400.
        // ================================================================
        // Spring Boot 4 (Spring Framework 7) refactored the validation
        // exception hierarchy. Historically, Spring Boot 3 threw
        // MethodArgumentNotValidException for @Valid failures. Spring Boot 4
        // uses HandlerMethodValidationException and related types.
        //
        // To maintain compatibility across both versions, this catch-all
        // treats any exception whose class name contains "Validation" or
        // "MethodArgument" as a 400 Bad Request.
        //
        // A future improvement would use the new Visitor pattern
        // (HandlerMethodValidationException.visitResults(Visitor)) for
        // structured field-level error messages.
        // ================================================================
        if (className.contains("Validation") || className.contains("MethodArgument")) {
            Map<String, String> errors = new HashMap<>();
            errors.put("validation", ex.getMessage());
            return build(HttpStatus.BAD_REQUEST, "Validation failed", req, errors);
        }

        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred", req, null);
    }

    // ---------- Helper ----------

    private ResponseEntity<ApiErrorResponse> build(HttpStatus status, String message,
                                                   HttpServletRequest req,
                                                   Map<String, String> validationErrors) {
        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(req.getRequestURI())
                .correlationId(CorrelationIdHolder.get())
                .validationErrors(validationErrors)
                .build();
        return ResponseEntity.status(status).body(body);
    }
}