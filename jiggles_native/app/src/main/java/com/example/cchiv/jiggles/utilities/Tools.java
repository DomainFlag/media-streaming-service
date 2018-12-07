package com.example.cchiv.jiggles.utilities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.cchiv.jiggles.Constants;
import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.model.Review;
import com.example.cchiv.jiggles.model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Tools {

    public static final String TAG = "Tools";

    private static final Float IMPACT_THRESHOLD = 8.0f;

    private static User user;

    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm", Locale.US);

    public static String parseDate(String objectId) {
        Date date = new Date(Long.parseLong(objectId.substring(0, 8), 16) * 1000);

        return simpleDateFormat.format(date);
    }

    private static final HashMap<Integer, String> intervals = new HashMap<>();
    static {
        intervals.put(60000, "s");
        intervals.put(3600000, "m");
        intervals.put(86400000, "h");
        intervals.put(604800000, "d");
    }

    public static String parseSocialDate(String objectId) {
        Date date = new Date(Long.parseLong(objectId.substring(0, 8), 16) * 1000);
        Date now = new Date();

        long diff = now.getTime() - date.getTime();
        for(Integer key : intervals.keySet()) {
            if(diff < key) {
                return intervals.get(key);
            }
        }

        return "w+";
    }

    public static void resolveUser(Context context) {
        String token = Tools.getToken(context);

        NetworkUtilities.FetchUser fetchUser = new NetworkUtilities.FetchUser(user -> {
            Tools.user = user;
        }, token);
    }

    public static User getUser() {
        return user;
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

    public static void setWeightedGradientBackground(Context context, View view, int color) {
        int darkMutedColor = ContextCompat.getColor(context, R.color.primaryTextColor);

        PaintDrawable paintDrawable = new PaintDrawable();
        ShapeDrawable.ShaderFactory shaderFactory = new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                return new LinearGradient(width / 2, 0, width / 2, height,
                        new int[] { color, darkMutedColor, darkMutedColor },
                        new float[] {0, 0.2f, 1}, Shader.TileMode.MIRROR);
            }
        };
        paintDrawable.setShape(new RectShape());
        paintDrawable.setShaderFactory(shaderFactory);
        ViewCompat.setBackground(view, paintDrawable);
    }

    public static void setGradientBackground(Context context, View view, int color, int defaultColor, int alpha) {
        int darkMutedColor = ContextCompat.getColor(context, defaultColor);

        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[] {
                        color,
                        darkMutedColor
                }
        );

        gradientDrawable.setAlpha(alpha);
        ViewCompat.setBackground(view, gradientDrawable);
    }

    public static void setGradientBackground(Context context, View view, int color, int alpha) {
        setGradientBackground(context, view, color, R.color.primaryTextColor, alpha);
    }

    public static String getToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.AUTH_TOKEN, Context.MODE_PRIVATE);

        return sharedPreferences.getString(Constants.TOKEN, null);
    }

    public static int getPaletteColor(Context context, Bitmap bitmap) {
        Palette palette = Palette.from(bitmap).generate();

        int defaultColor = ContextCompat.getColor(context, R.color.iconsTextColor);

        int color = palette.getVibrantColor(defaultColor);
        if(defaultColor == color)
            color = palette.getLightVibrantColor(defaultColor);


        if(defaultColor == color)
            color = palette.getDominantColor(defaultColor);

        return color;
    }

    public static String onComputeScore(Context context, List<Review> reviews, ScoreView scoreView, View detailedLayout) {
        float total = 0;

        for(int it = 0; it < reviews.size(); it++) {
            Review review = reviews.get(it);

            int color, score = review.getScore();
            if(score < 60)
                color = ContextCompat.getColor(context, R.color.colorScoreOther);
            else if(score < 80)
                color = ContextCompat.getColor(context, R.color.colorScorePossitive);
            else color = ContextCompat.getColor(context, R.color.colorScoreAcclaim);

            if(detailedLayout != null) {
                LinearLayout root = detailedLayout.findViewById(R.id.release_reviews);

                if(root != null) {
                    View view = LayoutInflater.from(context)
                            .inflate(R.layout.feature_review_highligh_layout, root, false);

                    ((RatingBar) view.findViewById(R.id.review_score)).setRating(score / 20);
                    ((TextView) view.findViewById(R.id.review_author)).setText(review.getAuthor());
                    ((TextView) view.findViewById(R.id.review_content)).setText(review.getContent());

                    root.addView(view);
                }
            }

            total += review.getScore();
        }

        total /= reviews.size() * 10.0f;

        String totalScore = String.format(Locale.US, "%.1f", total);

        if(scoreView != null && detailedLayout != null) {
            if(total < IMPACT_THRESHOLD)
                scoreView.imageView.setVisibility(View.GONE);

            scoreView.textView.setText(totalScore);
        }

        return totalScore;
    }

    public static String onComputeScore(Context context, List<Review> reviews, ScoreView scoreView) {
        return onComputeScore(context, reviews, scoreView, null);
    }

    public static class ScoreView {
        private TextView textView;
        private ImageView imageView;

        public ScoreView(TextView textView, ImageView imageView) {
            this.textView = textView;
            this.imageView = imageView;
        }
    }
}
