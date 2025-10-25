package com.rentmate.service.delivery.shared.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


        @ExceptionHandler(NotFoundException.class)
        public ResponseEntity<?> handleNotFound(NotFoundException ex) {
            return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
        }

        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<?> handleBadRequest(BadRequestException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<?> handleGeneric(Exception ex) {
            return ResponseEntity.status(500).body(Map.of("error", "Internal Server Error", "message", ex.getMessage()));
        }
    }


