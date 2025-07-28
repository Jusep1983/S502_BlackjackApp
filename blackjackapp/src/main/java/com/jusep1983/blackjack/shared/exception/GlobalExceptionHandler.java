package com.jusep1983.blackjack.shared.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.jusep1983.blackjack.shared.response.ErrorResponse;
import com.jusep1983.blackjack.shared.response.MyApiResponse;
import com.jusep1983.blackjack.shared.response.ResponseBuilder;
import com.mongodb.DuplicateKeyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private ErrorResponse buildError(HttpStatus status, String message, ServerHttpRequest request) {
        return new ErrorResponse(status.value(), message, request.getPath().toString(), LocalDateTime.now());
    }

    private Mono<ResponseEntity<MyApiResponse<ErrorResponse>>> response(HttpStatus status, String message, ServerHttpRequest request) {
        ErrorResponse error = buildError(status, message, request);
        return Mono.just(ResponseEntity.status(status).body(new MyApiResponse<>(status.value(), status.getReasonPhrase(), error)));
    }

    @ExceptionHandler({
            GameNotFoundException.class,
            PlayerNotFoundException.class
    })
    public Mono<ResponseEntity<MyApiResponse<ErrorResponse>>> handleNotFound(RuntimeException ex, ServerHttpRequest request) {
        log.warn("Not found: {} - {}", ex.getClass().getSimpleName(), ex.getMessage());
        return response(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler({
            FieldEmptyException.class,
            UsernameAlreadyExistsException.class,
            GameAlreadyFinishedException.class
    })
    public Mono<ResponseEntity<MyApiResponse<ErrorResponse>>> handleConflict(RuntimeException ex, ServerHttpRequest request) {
        log.warn("Conflict: {} - {}", ex.getClass().getSimpleName(), ex.getMessage());
        return response(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(UnauthorizedGameAccessException.class)
    public Mono<ResponseEntity<MyApiResponse<ErrorResponse>>> handleForbidden(UnauthorizedGameAccessException ex, ServerHttpRequest request) {
        log.warn("Unauthorized game access: {}", ex.getMessage());
        return response(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Mono<ResponseEntity<MyApiResponse<ErrorResponse>>> handleAccessDenied(AccessDeniedException ex, ServerHttpRequest request) {
        log.warn("Access denied: {}", ex.getMessage());
        return response(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    @ExceptionHandler(JWTVerificationException.class)
    public Mono<ResponseEntity<MyApiResponse<ErrorResponse>>> handleJwt(JWTVerificationException ex, ServerHttpRequest request) {
        log.warn("JWT verification failed: {}", ex.getMessage());
        return response(HttpStatus.UNAUTHORIZED, "Invalid or expired token", request);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<MyApiResponse<ErrorResponse>>> handleValidation(WebExchangeBindException ex, ServerHttpRequest request) {
        String message = ex.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("Validation failed: {}", message);
        return response(HttpStatus.BAD_REQUEST, "Validation failed: " + message, request);
    }

    @ExceptionHandler(AliasAlreadyExistsException.class)
    public Mono<ResponseEntity<MyApiResponse<ErrorResponse>>> handleAliasAlreadyExistsException(AliasAlreadyExistsException ex, ServerHttpRequest request) {
        log.warn("Alias conflict: {}", ex.getMessage());
        return response(HttpStatus.BAD_REQUEST, "Alias conflict: " + ex.getMessage(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<MyApiResponse<ErrorResponse>>> handleIllegalArgument(IllegalArgumentException ex, ServerHttpRequest request) {
        log.error("Illegal argument: {}", ex.getMessage());
        if (ex.getMessage().contains("SUPER_USER")) {
            return response(HttpStatus.FORBIDDEN, ex.getMessage(), request);
        }
        return response(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<MyApiResponse<ErrorResponse>>> handleGeneral(Exception ex, ServerHttpRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
    }

}
