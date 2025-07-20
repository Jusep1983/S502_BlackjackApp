package com.jusep1983.blackjack.shared.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public class ResponseBuilder {
    // Éxito 200 OK
    public static <T> ResponseEntity<MyApiResponse<T>> ok(String message, T data) {
        return ResponseEntity.ok(new MyApiResponse<>(200, message, data));
    }

    // Éxito 201 Created
    public static <T> ResponseEntity<MyApiResponse<T>> created(String message, T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new MyApiResponse<>(201, message, data));
    }

    // Error 401 Unauthorized
    public static ResponseEntity<MyApiResponse<ErrorResponse>> unauthorized(String path, String message) {
        return error(HttpStatus.UNAUTHORIZED, path, message);
    }

    // Error 409 Conflict
    public static ResponseEntity<MyApiResponse<ErrorResponse>> conflict(String path, String message) {
        return error(HttpStatus.CONFLICT, path, message);
    }

    // Error 400 Bad Request
    public static ResponseEntity<MyApiResponse<ErrorResponse>> badRequest(String path, String message) {
        return error(HttpStatus.BAD_REQUEST, path, message);
    }

    // Generic method for errors
    private static ResponseEntity<MyApiResponse<ErrorResponse>> error(HttpStatus status, String path, String message) {
        ErrorResponse error = new ErrorResponse(status.value(), message, path, LocalDateTime.now());
        return ResponseEntity.status(status)
                .body(new MyApiResponse<>(status.value(), status.getReasonPhrase(), error));
    }

}
