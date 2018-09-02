package com.example.cchiv.jiggles.Utilities;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.cchiv.jiggles.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkUtilities {

    public interface OnPostNetworkCallback {
        void onPostNetworkCallback(Response response);
    }

    private static final String TAG = "NetworkUtilities";

    private OnPostNetworkCallback onPostNetworkCallback = null;

    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public NetworkUtilities() {}

    public NetworkUtilities(OnPostNetworkCallback onPostNetworkCallback) {
        this.onPostNetworkCallback = onPostNetworkCallback;
    }

    public void userAuthLogging(String email, String password) {
        Uri uri = new Uri.Builder()
                .scheme(Constants.SCHEME)
                .authority(Constants.AUTHORITY)
                .appendPath(Constants.USERS)
                .appendPath(Constants.LOGIN)
                .build();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("password", password);

            RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());
            Request request = new Request.Builder()
                    .url(uri.toString())
                    .addHeader("Content-Type", "application/json")
                    .post(requestBody)
                    .build();

            AsyncNetworkTask asyncNetworkTask;
            if(onPostNetworkCallback != null)
                asyncNetworkTask = new AsyncNetworkTask(onPostNetworkCallback);
            else asyncNetworkTask = new AsyncNetworkTask();

            asyncNetworkTask.execute(request);
        } catch(JSONException e) {
            Log.v(TAG, e.toString());
        }
    }

    private static class AsyncNetworkTask extends AsyncTask<Request, Void, Response> {

        private OnPostNetworkCallback onPostNetworkCallback = null;

        private OkHttpClient client;

        AsyncNetworkTask() {
            client = new OkHttpClient();
        }

        AsyncNetworkTask(OnPostNetworkCallback onPostNetworkCallback) {
            client = new OkHttpClient();
            this.onPostNetworkCallback = onPostNetworkCallback;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Response doInBackground(Request... requests) {
            try {
                return client.newCall(requests[0]).execute();
            } catch(IOException e) {
                Log.v(TAG, e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);

            try {
                Log.v(TAG, response.body().string());

                if(onPostNetworkCallback != null)
                    onPostNetworkCallback.onPostNetworkCallback(response);
            } catch(IOException e) {
                Log.v(TAG, e.toString());
            }
        }
    }
}
