package com.s1.LogiTrack.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice//Esta anotación captura los errores globales.
public class GlobalExceptionHandler {
    //Cuando no existe una respuesta: 404
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlerNotFound(EntityNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.NOT_FOUND.value(),
                        ex.getMessage() /*o cualquier mensaje*/,
                        "RESOURCE_NOT_FOUND / Recurso no encontrado"));
    }

    /**Captura los Valid con NotNull NotBlank y todo lo usado para DTOs.*/
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("errorCode", "VALIDATION_FAILED");

        Map<String, String> errors = new HashMap<>();
        //Se recorren los posibles errores, @NotBlank, @NotNull, @Size...
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        response.put("errors", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //Para errores propios de la clase. Fallas internas o el tipico 500
    // Captura cualquier excepción no controlada previamente (error 500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericErrors(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error interno del servidor", "INTERNAL_SERVER_ERROR")
        );
    }

    //Usado para cuando sucede una regla de negocio, ejemplo: total negativo,
    //stock superior al que existe.
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRuleException(BusinessRuleException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage(), "BUSINESS_RULE_VIOLATION")
        );
    }

    //Usado cuando no existe una ruta correcta en la solicitud HTTP.
    //Requiere configuracion adicional en la application.properties.
    //Usada solo cuando la ruta no conoce el controller: localhost:8080/api/wwww
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoHandlerFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.NOT_FOUND.value(),
                        "Ruta no encontrada o incompleta",
                        "ERR_NO_HANDLER_FOUND"
                )
        );
    }

    //Útil cuando deseamos que se sepa que el DTO o json esta incompleto.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleParsingErrors(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                        "El cuerpo de la solicitud no es válido.",
                        "BAD_REQUEST")
        );
    }
}
