package com.example.cchiv.jiggles.utilities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.example.cchiv.jiggles.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Tools {

    public static final String TAG = "Tools";

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

    public static void resolveAuthToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.AUTH_TOKEN, Context.MODE_PRIVATE);

        String token = sharedPreferences.getString(Constants.TOKEN, null);
        if(token == null) {
            ((Activity) context).finish();
        }
    }

    public static void setStatusBarColor(Context context, int color) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = ((Activity) context).getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            float[] hsv = new float[3];
            Color.colorToHSV(color, hsv);
            hsv[2] = hsv[2] * 0.4f;
            int darkColor = Color.HSVToColor(hsv);

            window.setStatusBarColor(darkColor);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean checkInternetConnectivity(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager == null)
            return false;

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

}
