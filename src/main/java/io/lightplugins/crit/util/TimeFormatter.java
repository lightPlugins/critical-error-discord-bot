package io.lightplugins.crit.util;

public class TimeFormatter {

    public static String formatTime(double seconds) {
        int hours = (int) seconds / 3600;
        int minutes = (int) (seconds % 3600) / 60;
        int secs = (int) seconds % 60;

        StringBuilder formattedTime = new StringBuilder();
        if (hours > 0) {
            if(hours == 1) {
                formattedTime.append(hours).append(" Stunde ");
            } else {
                formattedTime.append(hours).append(" Stunden ");
            }
        }
        if (minutes > 0) {
            if(minutes == 1) {
                formattedTime.append(minutes).append(" Minute ");
            } else {
                formattedTime.append(minutes).append(" Minuten ");
            }
        }
        if (secs > 0 || formattedTime.isEmpty()) {
            if(secs == 1) {
                formattedTime.append(secs).append(" Sekunde ");
            } else {
                formattedTime.append(secs).append(" Sekunden ");
            }
        }
        return formattedTime.toString().trim();
    }
}
