package io.lightplugins.crit.util;

import io.lightplugins.crit.master.LightMaster;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class LightPrinter {

    // ANSI escape codes for colors
    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    public static void print(String message) {
        // print message with timestamp
        // create a timestamp
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatedDate = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        System.out.println(LightMaster.getPrefix() + "[" + localDateTime.format(formatedDate)+ "] [" + BLUE + "INFO" + RESET + "] " + message);
    }

    public static void printWatchdog(String message) {
        // print message with timestamp
        // create a timestamp
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatedDate = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        System.out.println(LightMaster.getPrefix() + "[" + localDateTime.format(formatedDate)+ "] [" + YELLOW + "WATCHDOG" + RESET + "] " + message);
    }

    public static void printError(String message) {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatedDate = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        System.err.println(LightMaster.getPrefix() + "[" + localDateTime.format(formatedDate)+ "] [" + RED + "ERROR" + RESET + "] " + message);
    }

    public static void printDebug(String message) {
        boolean debug = false;
        if(!debug) return;
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatedDate = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        System.err.println(LightMaster.getPrefix() + "[" + localDateTime.format(formatedDate)+ "] [" + PURPLE + "DEBUG" + RESET + "] " + message);
    }
}
