package com.zp4rker.zlevels.core.util;


/**
 * @author ZP4RKER
 */
public class ZLogger {

    public static void info(String message) {
        // Append new line
        System.out.println("[INFO] " + message + "\n");
    }

    public static void warn(String message) {
        // Append new line
        System.out.println("[WARNING] " + message + "\n");
    }

    public static void debug(String message) {
        // Append new line
        System.out.println("[DEBUG] " + message + "\n");
    }

    public static void blankLine() {
        // Blank line
        System.out.println();
    }

}
