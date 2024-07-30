package com.example.demo.hybrid.exception;

import com.example.demo.hybrid.until.constant.LogMessageConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final Map<Class<? extends Exception>, String> exceptionMappings = new HashMap<>();

    static {
        exceptionMappings.put(InvalidPathException.class, "Invalid file path");
        exceptionMappings.put(IOException.class, "Error reading file");
        exceptionMappings.put(IllegalArgumentException.class, "Error decoding key");
        exceptionMappings.put(NoSuchAlgorithmException.class, "RSA algorithm not available");
        exceptionMappings.put(InvalidKeySpecException.class, "Invalid key format");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        log.error(LogMessageConstants.RUNTIME_EXCEPTION, ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(NotNullException.class)
    public ResponseEntity<String> handleNotNullException(NotNullException ex) {
        log.error(LogMessageConstants.NOT_NULL_EXCEPTION, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ChangeSetPersister.NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        log.error(LogMessageConstants.NOT_FOUND_EXCEPTION, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler({InvalidPathException.class, IOException.class, IllegalArgumentException.class,
            NoSuchAlgorithmException.class, InvalidKeySpecException.class})
    public ResponseEntity<String> handleMappedException(Exception ex) {
        String message = exceptionMappings.getOrDefault(ex.getClass(), "An unexpected error occurred");
        log.error("{}: {}", message, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message + ": " + ex.getMessage());
    }

    // Phương thức xử lý cho các ngoại lệ không được map cụ thể
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        log.error("An unexpected error occurred: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
    }
}