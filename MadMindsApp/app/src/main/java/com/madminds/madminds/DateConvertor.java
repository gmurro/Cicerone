package com.madminds.madminds;

import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConvertor {
    public static String convertFormat(String date, String oldFormat, String newFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(oldFormat);
        Date d = null;
        try {
            d = sdf.parse(date);
        } catch (ParseException e) {
        }
        sdf.applyPattern(newFormat);
        return sdf.format(d);
    }
}
