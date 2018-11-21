package com.example.cchiv.jiggles.utilities;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.cchiv.jiggles.Constants;
import com.example.cchiv.jiggles.model.Collection;
import com.example.cchiv.jiggles.model.News;
import com.example.cchiv.jiggles.model.Release;
import com.example.cchiv.jiggles.model.Thread;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NetworkUtilities {

    private static final String TAG = "NetworkUtilities";

    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    /* Fetch News */
    public static class FetchNews extends AsyncCustomNetworkTask<List<News>> {

        public FetchNews(NetworkCallbacks<List<News>> networkCallbacks) {
            super(networkCallbacks);

            Uri uri = new Uri.Builder()
                    .scheme(Constants.SCHEME)
                    .authority(Constants.AUTHORITY)
                    .appendPath(Constants.MAIN)
                    .appendPath(Constants.NEWS)
                    .build();

            Request request = new Request.Builder()
                    .url(uri.toString())
                    .addHeader("Content-Type", "application/json")
                    .get()
                    .build();

            execute(request);
        }

        @Override
        public List<News> onParseNetworkCallback(Gson gson, Headers headers, String body) {
            Type type = new TypeToken<List<News>>() {}.getType();

            return gson.fromJson(body, type);
        }
    }

    /* Fetch Fresh Release */
    public static class FetchFreshRelease extends AsyncCustomNetworkTask<List<Release>> {

        public FetchFreshRelease(NetworkCallbacks<List<Release>> networkCallbacks, String token) {
            super(networkCallbacks);

            Uri uri = new Uri.Builder()
                    .scheme(Constants.SCHEME)
                    .authority(Constants.AUTHORITY)
                    .appendPath(Constants.MAIN)
                    .appendPath(Constants.FRESH)
                    .build();

            Request request = new Request.Builder()
                    .url(uri.toString())
                    .addHeader("Content-Type", "application/json")
                    .addHeader("X-Auth", token)
                    .get()
                    .build();

            execute(request);
        }

        @Override
        public List<Release> onParseNetworkCallback(Gson gson, Headers headers, String body) {
            Type type = new TypeToken<List<Release>>() {}.getType();

            return gson.fromJson(body, type);
        }
    }

    /* Fetch Releases */
    public static class FetchReleases extends AsyncCustomNetworkTask<List<Release>> {

        public FetchReleases(NetworkCallbacks<List<Release>> networkCallbacks) {
            super(networkCallbacks);

            Uri uri = new Uri.Builder()
                    .scheme(Constants.SCHEME)
                    .authority(Constants.AUTHORITY)
                    .appendPath(Constants.MAIN)
                    .appendPath(Constants.RELEASES)
                    .build();

            Request request = new Request.Builder()
                    .url(uri.toString())
                    .addHeader("Content-Type", "application/json")
                    .get()
                    .build();

            execute(request);
        }

        @Override
        public List<Release> onParseNetworkCallback(Gson gson, Headers headers, String body) {
            Type type = new TypeToken<List<Release>>() {}.getType();

            return gson.fromJson(body, type);
        }
    }

    /* Fetch Threads */
    public static class FetchThreads extends AsyncCustomNetworkTask<List<Thread>> {

        public FetchThreads(NetworkCallbacks<List<Thread>> networkCallbacks) {
            super(networkCallbacks);

            Uri uri = new Uri.Builder()
                    .scheme(Constants.SCHEME)
                    .authority(Constants.AUTHORITY)
                    .appendPath(Constants.FORUM)
                    .appendPath(Constants.THREADS)
                    .build();

            Request request = new Request.Builder()
                    .url(uri.toString())
                    .addHeader("Content-Type", "application/json")
                    .get()
                    .build();

            execute(request);
        }

        @Override
        public List<Thread> onParseNetworkCallback(Gson gson, Headers headers, String body) {
            Type type = new TypeToken<ArrayList<Thread>>() {}.getType();

            return gson.fromJson(body, type);
        }
    }

    /* Fetch Search Results */
    public static class FetchSearchResults extends AsyncCustomNetworkTask<Collection> {

        public FetchSearchResults(NetworkCallbacks<Collection> networkCallbacks, String query, String token) {
            super(networkCallbacks);

            Uri uri = new Uri.Builder()
                    .scheme(Constants.SCHEME)
                    .authority(Constants.AUTHORITY)
                    .appendPath(Constants.QUERY)
                    .appendPath(Constants.ALL)
                    .appendQueryParameter(Constants.ALL, query)
                    .build();

            Request request = new Request.Builder()
                    .url(uri.toString())
                    .addHeader("Content-Type", "application/json")
                    .addHeader("X-Auth", token)
                    .get()
                    .build();

            execute(request);
        }

        @Override
        public Collection onParseNetworkCallback(Gson gson, Headers headers, String body) {
            Log.v(TAG, body);
            Type type = new TypeToken<Collection>() {}.getType();

            return gson.fromJson(body, type);
        }
    }

    /* Resolve Thread Creation */
    public static class ResolveCreateThread extends AsyncCustomNetworkTask<Thread> {

        public ResolveCreateThread(NetworkCallbacks<Thread> networkCallbacks, JSONObject jsonObject, String token) {
            super(networkCallbacks);

            Uri uri = new Uri.Builder()
                    .scheme(Constants.SCHEME)
                    .authority(Constants.AUTHORITY)
                    .appendPath(Constants.FORUM)
                    .appendPath(Constants.THREAD)
                    .build();

            RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());

            Request request = new Request.Builder()
                    .url(uri.toString())
                    .addHeader("Content-Type", "application/json")
                    .addHeader("X-Auth", token)
                    .post(requestBody)
                    .build();

            execute(request);
        }

        @Override
        public Thread onParseNetworkCallback(Gson gson, Headers headers, String body) {
            Type type = new TypeToken<Thread>() {}.getType();

            return gson.fromJson(body, type);
        }
    }

    /* Resolve Auth Credentials */
    public static class ResolveAuthLogging extends AsyncCustomNetworkTask<String> {

        public ResolveAuthLogging(NetworkCallbacks<String> networkCallbacks, String email, String password) {
            super(networkCallbacks);

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

                execute(request);
            } catch(JSONException e) {
                Log.v(TAG, e.toString());
            }
        }

        @Override
        public String onParseNetworkCallback(Gson gson, Headers headers, String body) {
            return headers.get("X-Auth");
        }
    }

    public abstract static class AsyncCustomNetworkTask<T> extends AsyncTask<Request, Void, T> {

        private static Gson gson = new Gson();

        public interface NetworkCallbacks<T> {
            void onPostNetworkCallback(T result);
        }

        private NetworkCallbacks<T> networkCallbacks;

        private OkHttpClient client = new OkHttpClient();

        public abstract T onParseNetworkCallback(Gson gson, Headers headers, String body);

        public AsyncCustomNetworkTask(NetworkCallbacks<T> networkCallbacks) {
            this.networkCallbacks = networkCallbacks;
        }

        @Override
        protected T doInBackground(Request... requests) {
            try {
                Response response = client.newCall(requests[0]).execute();

                ResponseBody body = response.body();
                if(body == null)
                    return null;

                String resString = body.string();
                if(resString == null)
                    return null;

                return onParseNetworkCallback(gson, response.headers(), resString);
            } catch(IOException e) {
                Log.v(TAG, e.toString());
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(T result) {
            super.onPostExecute(result);

            networkCallbacks.onPostNetworkCallback(result);
        }
    }
}
