package com.crimsonlogic.payrollmanagementsystem.exception;

/**
 * Exception thrown when a user attempts to request leave but has exhausted their leave balance.
 * This exception extends from the base Exception class.
 *
 * @author abdulmanan
 */
public class LeavesExhaustedException extends Exception {

    /**
     * Constructs a new LeavesExhaustedException with the specified detail message.
     *
     * @param msg the detail message
     */
    public LeavesExhaustedException(String msg) {
        super(msg);
    }
}
