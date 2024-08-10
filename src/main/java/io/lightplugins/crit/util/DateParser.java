package io.lightplugins.crit.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateParser {

    public static Date parseDate(String dateString) {
        String[] dateFormats = {"d.M.yyyy", "dd.MM.yyyy"};
        for (String format : dateFormats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sdf.setLenient(false);
                return sdf.parse(dateString);
            } catch (ParseException e) {
                // Continue to the next format
                return null;
            }
        }
        throw new IllegalArgumentException("Invalid date format: " + dateString);
    }

}
