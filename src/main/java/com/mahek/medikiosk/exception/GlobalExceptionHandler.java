
package com.mahek.medikiosk.exception;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> buildErrorResponse(
            int statusCode,
            String message,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.resolve(statusCode);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("status", statusCode);
        body.put("error", status != null
                ? status.getReasonPhrase()
                : "Request failed");
        body.put("message", message);
        body.put("path", request.getRequestURI());

        return body;
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(
            ResponseStatusException ex,
            HttpServletRequest request) {

        int statusCode = ex.getStatusCode().value();

        String message = ex.getReason() != null
                ? ex.getReason()
                : "Request could not be processed";

        return ResponseEntity.status(statusCode)
                .body(buildErrorResponse(
                        statusCode, message, request));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> validationErrors =
                ex.getBindingResult().getFieldErrors()
                        .stream()
                        .collect(Collectors.toMap(
                                error -> error.getField(),
                                error -> error.getDefaultMessage() != null
                                        ? error.getDefaultMessage()
                                        : "Invalid value",
                                (first, second) -> first,
                                LinkedHashMap::new
                        ));

        Map<String, Object> body = buildErrorResponse(
                400, "Please correct the invalid fields", request);

        body.put("validationErrors", validationErrors);

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidBody(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        return ResponseEntity.badRequest().body(
                buildErrorResponse(
                        400,
                        "Invalid JSON or incorrectly formatted field value",
                        request));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleLargeUpload(
            MaxUploadSizeExceededException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(buildErrorResponse(
                        413,
                        "Uploaded file exceeds the permitted size",
                        request));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataConflict(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildErrorResponse(
                        409,
                        "The operation conflicts with database constraints",
                        request));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpectedError(
            Exception ex,
            HttpServletRequest request) {

        // Log details securely in production; do not expose stack traces
        // or database details to API clients.

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorResponse(
                        500,
                        "An unexpected server error occurred",
                        request));
    }
}
