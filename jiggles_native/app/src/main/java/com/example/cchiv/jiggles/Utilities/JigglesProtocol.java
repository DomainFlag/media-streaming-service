package com.example.cchiv.jiggles.utilities;

import java.nio.charset.Charset;

public class JigglesProtocol {

    private static final String TAG = "JigglesProtocol";

    public static final String ACTION_PAUSE = "pause";
    public static final String ACTION_RESUME = "resume";

    public static final String ACTION_BODY = "body";

    private static byte[] encodeAction(String action) {
        return action.getBytes(Charset.forName("UTF-8"));
    }

    public static byte[] createResumeAction() {
        String action = ACTION_RESUME + "\n" +
                ACTION_BODY + "\n";

        return encodeAction(action);
    }

    public static byte[] createPauseAction() {
        String action = ACTION_PAUSE + "\n" +
                ACTION_BODY + "\n";

        return encodeAction(action);
    }

    public static String decodeAction(byte[] bytes) {
        String action = new String(bytes, Charset.forName("UTF-8"));

        return action;
    }
}
