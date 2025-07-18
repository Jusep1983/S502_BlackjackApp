package com.jusep1983.blackjack.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GameNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGameNotFound(GameNotFoundException ex, ServerHttpRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getURI().getPath(),
                LocalDateTime.now()
        );
        System.out.println("Manejando GameNotFoundException");
        ex.getMessage();
        return Mono.just(new ResponseEntity < > (errorResponse, HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(PlayerNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGameNotFound(PlayerNotFoundException ex, ServerHttpRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getURI().getPath(),
                LocalDateTime.now()
        );
        System.out.println("Manejando PlayerNotFoundException");
        ex.getMessage();
        return Mono.just(new ResponseEntity < > (errorResponse, HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(FieldEmptyException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGameNotFound(FieldEmptyException ex, ServerHttpRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getURI().getPath(),
                LocalDateTime.now()
        );
        System.out.println("Manejando FieldEmptyException");
        ex.getMessage();
        return Mono.just(new ResponseEntity < > (errorResponse, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGameNotFound(UsernameAlreadyExistsException ex, ServerHttpRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                request.getURI().getPath(),
                LocalDateTime.now()
        );
        System.out.println("Manejando UsernameAlreadyExistsException");
        ex.getMessage();
        return Mono.just(new ResponseEntity < > (errorResponse, HttpStatus.CONFLICT));
    }

    @ExceptionHandler(GameAlreadyFinishedException .class)
    public Mono<ResponseEntity<ErrorResponse>> handleGameNotFound(GameAlreadyFinishedException  ex, ServerHttpRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                request.getURI().getPath(),
                LocalDateTime.now()
        );
        System.out.println("Manejando GameAlreadyFinishedException ");
        ex.getMessage();
        return Mono.just(new ResponseEntity < > (errorResponse, HttpStatus.CONFLICT));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGeneral(Exception ex, ServerHttpRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                request.getURI().getPath(),
                LocalDateTime.now()
        );
        System.out.println("Manejando Exception");
        ex.getMessage();
        ex.printStackTrace();
        return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR));
    }

}
