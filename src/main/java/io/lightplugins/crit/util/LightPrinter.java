package io.lightplugins.crit.util;

import io.lightplugins.crit.master.LightMaster;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class LightPrinter {

    public static void print(String message) {
        // print message with timestamp
        // create a timestamp
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatedDate = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        System.out.println(LightMaster.getPrefix() + "[" + localDateTime.format(formatedDate)+ "] " + message);
    }

    public  static void printError(String message) {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatedDate = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        System.err.println(LightMaster.getPrefix() + "[" + localDateTime.format(formatedDate)+ "] [ERROR]" + message);
    }
}
