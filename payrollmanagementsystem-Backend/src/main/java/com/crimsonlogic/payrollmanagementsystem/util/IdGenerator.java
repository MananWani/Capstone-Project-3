package com.crimsonlogic.payrollmanagementsystem.util;

import java.security.SecureRandom;

/**
 * Utility class for generating random IDs.
 * The IDs are generated within a specified range to ensure uniqueness.
 *
 * @author abdulmanan
 */
public class IdGenerator {

    // Private constructor to prevent instantiation of this utility class
    private IdGenerator() {
    }

    // Lower bound for the random ID generation
    private static final int LOWER_BOUND = 1000000;

    // Upper bound for the random ID generation
    private static final int UPPER_BOUND = 9999999;

    // SecureRandom instance for generating cryptographically strong random numbers
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Generates a random ID within the specified range.
     *
     * @return a randomly generated integer ID between LOWER_BOUND and UPPER_BOUND (inclusive)
     */
    public static int generateRandomID() {
        // Calculate and return a random integer within the specified bounds
        return SECURE_RANDOM.nextInt(UPPER_BOUND - LOWER_BOUND + 1) + LOWER_BOUND;
    }
}
