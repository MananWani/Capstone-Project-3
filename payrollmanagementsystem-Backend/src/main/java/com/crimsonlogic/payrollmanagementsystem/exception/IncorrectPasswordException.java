package com.crimsonlogic.payrollmanagementsystem.exception;

/**
 * Exception thrown when a user attempts to log in with an incorrect password.
 * This exception extends from the base Exception class.
 *
 * @author abdulmanan
 */
public class IncorrectPasswordException extends Exception {

    /**
     * Constructs a new IncorrectPasswordException with the specified detail message.
     *
     * @param msg the detail message
     */
    public IncorrectPasswordException(String msg) {
        super(msg);
    }
}
