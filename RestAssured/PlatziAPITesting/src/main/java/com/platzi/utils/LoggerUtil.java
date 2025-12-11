package com.platzi.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtil {

    private static Logger logger;

    public static Logger getLogger(Class<?> clazz) {
        logger = LoggerFactory.getLogger(clazz);
        return logger;
    }

    public static void info(String message) {
        if (logger != null) {
            logger.info(message);
        } else {
            System.out.println("[INFO] " + message);
        }
    }

    public static void error(String message) {
        if (logger != null) {
            logger.error(message);
        } else {
            System.err.println("[ERROR] " + message);
        }
    }

    // Test step logging
    public static void step(String stepDescription) {
        info("STEP: " + stepDescription);
    }

    // Test result logging
    public static void pass(String message) {
        info("✓ PASS: " + message);
    }

    public static void fail(String message) {
        error("✗ FAIL: " + message);
    }
}
