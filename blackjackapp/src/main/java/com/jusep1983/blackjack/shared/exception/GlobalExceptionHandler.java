package com.jusep1983.blackjack.shared.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.jusep1983.blackjack.shared.response.ErrorResponse;
import com.jusep1983.blackjack.shared.response.MyApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

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
        return response(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler({
            FieldEmptyException.class,
            UsernameAlreadyExistsException.class,
            GameAlreadyFinishedException.class
    })
    public Mono<ResponseEntity<MyApiResponse<ErrorResponse>>> handleConflict(RuntimeException ex, ServerHttpRequest request) {
        return response(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(UnauthorizedGameAccessException.class)
    public Mono<ResponseEntity<MyApiResponse<ErrorResponse>>> handleForbidden(UnauthorizedGameAccessException ex, ServerHttpRequest request) {
        return response(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    @ExceptionHandler(JWTVerificationException.class)
    public Mono<ResponseEntity<MyApiResponse<ErrorResponse>>> handleJwt(JWTVerificationException ex, ServerHttpRequest request) {
        return response(HttpStatus.UNAUTHORIZED, "Invalid or expired token", request);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<MyApiResponse<ErrorResponse>>> handleValidation(WebExchangeBindException ex, ServerHttpRequest request) {
        String message = ex.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return response(HttpStatus.BAD_REQUEST, "Validation failed: " + message, request);
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<MyApiResponse<ErrorResponse>>> handleGeneral(Exception ex, ServerHttpRequest request) {
        ex.printStackTrace();
        return response(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
    }
}

