package com.crimsonlogic.payrollmanagementsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for managing exceptions across the application.
 * This class intercepts exceptions thrown by controllers and provides
 * appropriate HTTP responses.
 *
 * @author abdulmanan
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles ResourceNotFoundException and returns a BAD_REQUEST response.
     *
     * @param e the ResourceNotFoundException to handle
     * @return a ResponseEntity with the error message
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    /**
     * Handles ResourceExistsException and returns a BAD_REQUEST response.
     *
     * @param ex the ResourceExistsException to handle
     * @return a ResponseEntity with the error message
     */
    @ExceptionHandler(ResourceExistsException.class)
    public ResponseEntity<String> handleResourceExistsException(ResourceExistsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * Handles LeavesExhaustedException and returns a BAD_REQUEST response.
     *
     * @param e the LeavesExhaustedException to handle
     * @return a ResponseEntity with the error message
     */
    @ExceptionHandler(LeavesExhaustedException.class)
    public ResponseEntity<String> handleLeavesExhaustedException(LeavesExhaustedException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    /**
     * Handles IncorrectPasswordException and returns a BAD_REQUEST response.
     *
     * @param e the IncorrectPasswordException to handle
     * @return a ResponseEntity with the error message
     */
    @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<String> handleIncorrectPasswordException(IncorrectPasswordException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    /**
     * Handles generic exceptions and returns an INTERNAL_SERVER_ERROR response.
     *
     * @param e the Exception to handle
     * @return a ResponseEntity with a generic error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unexpected error, please try again later.");
    }
}
