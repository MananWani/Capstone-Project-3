package com.crimsonlogic.payrollmanagementsystem.exception;

/**
 * Exception thrown when a requested resource cannot be found.
 * This exception extends from the base Exception class.
 *
 * @author abdulmanan
 */
public class ResourceNotFoundException extends Exception {

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     *
     * @param msg the detail message
     */
    public ResourceNotFoundException(String msg) {
        super(msg);
    }
}
