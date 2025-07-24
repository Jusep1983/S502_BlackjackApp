package com.jusep1983.blackjack.shared.response;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Slf4j
public class ResponseBuilder {
    // Éxito 200 OK
    public static <T> ResponseEntity<MyApiResponse<T>> ok(String message, T data) {
        log.info("Response 200 OK: {}", message);
        return ResponseEntity.ok(new MyApiResponse<>(200, message, data));
    }

    // Éxito 201 Created
    public static <T> ResponseEntity<MyApiResponse<T>> created(String message, T data) {
        log.info("Response 201 Created: {}", message);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MyApiResponse<>(201, message, data));
    }

    // Error 401 Unauthorized
    public static ResponseEntity<MyApiResponse<ErrorResponse>> unauthorized(String path, String message) {
        log.warn("Response 401 Unauthorized on '{}': {}", path, message);
        return error(HttpStatus.UNAUTHORIZED, path, message);
    }

    // Error 409 Conflict
    public static ResponseEntity<MyApiResponse<ErrorResponse>> conflict(String path, String message) {
        log.warn("Response 409 Conflict on '{}': {}", path, message);
        return error(HttpStatus.CONFLICT, path, message);
    }

    // Error 400 Bad Request
    public static ResponseEntity<MyApiResponse<ErrorResponse>> badRequest(String path, String message) {
        log.warn("Response 400 Bad Request on '{}': {}", path, message);
        return error(HttpStatus.BAD_REQUEST, path, message);
    }

    // Generic method for errors
    public static ResponseEntity<MyApiResponse<ErrorResponse>> error(HttpStatus status, String path, String message) {
        log.error("Response {} {} on '{}': {}", status.value(), status.getReasonPhrase(), path, message);
        ErrorResponse error = new ErrorResponse(status.value(), message, path, LocalDateTime.now());
        return ResponseEntity.status(status)
                .body(new MyApiResponse<>(status.value(), status.getReasonPhrase(), error));
    }

}
