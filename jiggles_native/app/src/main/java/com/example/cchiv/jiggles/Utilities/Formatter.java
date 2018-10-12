package com.example.cchiv.jiggles.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Formatter {

    public static String parseDate(String objectId) {
        Date date = new Date(Long.parseLong(objectId.substring(0, 8), 16) * 1000);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm", Locale.US);
        return simpleDateFormat.format(date);
    }

}
