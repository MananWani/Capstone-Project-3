package com.crimsonlogic.payrollmanagementsystem.exception;

/**
 * Exception thrown when an attempt is made to create a resource that already exists.
 * This exception extends from the base Exception class.
 *
 * @author abdulmanan
 */
public class ResourceExistsException extends Exception {

    /**
     * Constructs a new ResourceExistsException with the specified detail message.
     *
     * @param msg the detail message
     */
    public ResourceExistsException(String msg) {
        super(msg);
    }
}
