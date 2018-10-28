package com.example.cchiv.jiggles.utilities;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Formatter {

    public static final String TAG = "Formatter";

    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm", Locale.US);

    public static String parseDate(String objectId) {
        Date date = new Date(Long.parseLong(objectId.substring(0, 8), 16) * 1000);

        return simpleDateFormat.format(date);
    }

    public static Date parseStringDate(String source) {
        Date date = new Date();
        try {
            date = simpleDateFormat.parse(source);
        } catch(ParseException e) {
            Log.v(TAG, e.toString());
        }

        return date;
    }

}
