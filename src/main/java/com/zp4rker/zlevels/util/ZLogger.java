package com.zp4rker.zlevels.util;


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

}
