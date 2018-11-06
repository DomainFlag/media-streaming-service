package com.example.cchiv.jiggles.player;

import java.nio.charset.Charset;

public class RemoteProtocol {

    private static final String TAG = "JigglesProtocol";

    public static final String ACTION_PAUSE = "pause";
    public static final String ACTION_RESUME = "resume";
    public static final String ACTION_STREAM = "stream";

    public static final String ACTION_BODY_LENGTH = "length";
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

    public static byte[] createStreamAction(byte[] data) {
        String action = ACTION_STREAM + "\n" +
                ACTION_BODY_LENGTH + ":" + data.length + "\n" +
                ACTION_BODY + "\n";

        byte[] prov = encodeAction(action);
        byte[] result = new byte[prov.length + data.length];

        System.arraycopy(prov, 0, result, 0, prov.length);
        System.arraycopy(data, 0, result, prov.length, data.length);

        return result;
    }

    public static String decodeAction(byte[] bytes) {
        String action = new String(bytes, Charset.forName("UTF-8"));

        return action;
    }
}
