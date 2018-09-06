package com.example.cchiv.jiggles.Utilities;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.cchiv.jiggles.Constants;
import com.example.cchiv.jiggles.Model.Collection;
import com.example.cchiv.jiggles.Model.News;
import com.example.cchiv.jiggles.Model.Release;
import com.example.cchiv.jiggles.Model.Review;
import com.example.cchiv.jiggles.Model.Track;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

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

    public NetworkUtilities() {}

    /* Fetch News */
    public void fetchNews(FetchNews.OnPostNetworkCallback onPostNetworkCallback) {
        new FetchNews(onPostNetworkCallback);
    }

    public static class FetchNews extends AsyncNetworkTask<ArrayList<News>> {

        public interface OnPostNetworkCallback {
            void onPostNetworkCallback(ArrayList<News> news);
        }

        private OnPostNetworkCallback onPostNetworkCallback;

        FetchNews(OnPostNetworkCallback onPostNetworkCallback) {
            this.onPostNetworkCallback = onPostNetworkCallback;

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
        protected ArrayList<News> doInBackground(Request... requests) {
            try {
                Response response = getClient().newCall(requests[0]).execute();
                return onParseNetworkResponse(response);
            } catch(IOException e) {
                Log.v(TAG, e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<News> news) {
            super.onPostExecute(news);

            if(this.onPostNetworkCallback != null)
                this.onPostNetworkCallback.onPostNetworkCallback(news);
        }

        private ArrayList<News> onParseNetworkResponse(Response response) {
            ResponseBody body = response.body();
            if(body == null)
                return null;

            try {
                String resString = body.string();
                if(resString == null)
                    return null;

                try {
                    ArrayList<News> news = new ArrayList<>();

                    JSONArray jsonArray = new JSONArray(resString);
                    for(int it = 0; it < jsonArray.length(); it++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(it);

                        int _id = jsonObject.optInt("_id");
                        String author = jsonObject.optString("author");
                        String header = jsonObject.optString("header");
                        String caption = jsonObject.optString("caption");

                        news.add(new News(_id, author, header, caption));
                    }

                    return news;
                } catch(JSONException e) {
                    Log.v(TAG, e.toString());
                }
            } catch(IOException e) {
                Log.v(TAG, e.toString());
            }

            return null;
        }
    }

    /* Fetch Releases */
    public void fetchReleases(FetchReleases.OnPostNetworkCallback onPostNetworkCallback) {
        new FetchReleases(onPostNetworkCallback);
    }

    public static class FetchReleases extends AsyncNetworkTask<ArrayList<Release>> {

        public interface OnPostNetworkCallback {
            void onPostNetworkCallback(ArrayList<Release> releases);
        }

        private OnPostNetworkCallback onPostNetworkCallback = null;

        FetchReleases(OnPostNetworkCallback onPostNetworkCallback) {
            this.onPostNetworkCallback = onPostNetworkCallback;

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
        protected ArrayList<Release> doInBackground(Request... requests) {
            try {
                Response response = getClient().newCall(requests[0]).execute();
                return onParseNetworkResponse(response);
            } catch(IOException e) {
                Log.v(TAG, e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Release> releases) {
            super.onPostExecute(releases);

            if(this.onPostNetworkCallback != null)
                this.onPostNetworkCallback.onPostNetworkCallback(releases);
        }

        private Review parseReviewJSONObject(JSONObject jsonObject) {
            String _id  = jsonObject.optString("_id");
            String author = jsonObject.optString("author");
            String content = jsonObject.optString("content");
            String url = jsonObject.optString("url");

            int score = jsonObject.optInt("score");

            return new Review(_id, author, content, url, score);
        }

        private ArrayList<Release> onParseNetworkResponse(Response response) {
            ResponseBody body = response.body();
            if(body == null)
                return null;

            try {
                String resString = body.string();
                if(resString == null)
                    return null;

                try {
                    ArrayList<Release> releases = new ArrayList<>();

                    JSONArray jsonArray = new JSONArray(resString);
                    for(int it = 0; it < jsonArray.length(); it++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(it);

                        String _id = jsonObject.optString("_id");
                        String title = jsonObject.optString("title");
                        String artist = jsonObject.optString("artist");
                        String url = jsonObject.optString("url");

                        ArrayList<Review> reviews = new ArrayList<>();
                        JSONArray jsonReviewArray = jsonObject.getJSONArray("reviews");
                        for(int g = 0; g < jsonReviewArray.length(); g++) {
                            reviews.add(parseReviewJSONObject(jsonReviewArray.getJSONObject(g)));
                        }

                        releases.add(new Release(_id, title, artist, url, reviews));
                    }

                    return releases;
                } catch(JSONException e) {
                    Log.v(TAG, e.toString());
                }
            } catch(IOException e) {
                Log.v(TAG, e.toString());
            }

            return null;
        }
    }

    /* Fetch Search Results */
    public void fetchSearchResults(FetchSearchResults.OnPostNetworkCallback onPostNetworkCallback, String query, String token) {
        new FetchSearchResults(onPostNetworkCallback, query, token);
    }

    public static class FetchSearchResults extends AsyncNetworkTask<Collection> {

        public interface OnPostNetworkCallback {
            void onPostNetworkCallback(Collection collection);
        }

        private OnPostNetworkCallback onPostNetworkCallback = null;

        FetchSearchResults(OnPostNetworkCallback onPostNetworkCallback, String query, String token) {
            this.onPostNetworkCallback = onPostNetworkCallback;

            Uri uri = new Uri.Builder()
                    .scheme(Constants.SCHEME)
                    .authority(Constants.AUTHORITY)
                    .appendPath(Constants.QUERY)
                    .appendPath(Constants.TRACK)
                    .appendQueryParameter(Constants.TRACK, query)
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
        protected Collection doInBackground(Request... requests) {
            try {
                Response response = getClient().newCall(requests[0]).execute();
                return onParseNetworkResponse(response);
            } catch(IOException e) {
                Log.v(TAG, e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Collection collection) {
            super.onPostExecute(collection);

            if(this.onPostNetworkCallback != null)
                this.onPostNetworkCallback.onPostNetworkCallback(collection);
        }

        private Collection onParseNetworkResponse(Response response) {
            ResponseBody body = response.body();
            if(body == null)
                return null;

            try {
                String resString = body.string();
                if(resString == null)
                    return null;

                Gson gson = new Gson();

                try {
                    ArrayList<Track> tracks = new ArrayList<>();

                    JSONObject jsonObject = new JSONObject(resString);
                    JSONObject jsonTracksObject = jsonObject.getJSONObject("tracks");
                    JSONArray jsonItemsObject = jsonTracksObject.getJSONArray("items");
                    for(int it = 0; it < jsonItemsObject.length(); it++) {
                        JSONObject jsonItemObject = jsonItemsObject.getJSONObject(it);

                        tracks.add(gson.fromJson(jsonItemObject.toString(), Track.class));
                    }

                    return new Collection(null, tracks, null);
                } catch(JSONException e) {
                    Log.v(TAG, e.toString());
                }
            } catch(IOException e) {
                Log.v(TAG, e.toString());
            }

            return null;
        }
    }

    /* Resolve Auth Credentials */
    public void authLogging(Context context, String email, String password, AuthLogging.OnPostNetworkCallback onPostNetworkCallback) {
        new AuthLogging(context, email, password, onPostNetworkCallback);
    }

    public static class AuthLogging extends AsyncNetworkTask<String> {

        public interface OnPostNetworkCallback {
            void onPostNetworkCallback(Context context, String token);
        }

        private WeakReference<Context> contextWeakReference;

        private OnPostNetworkCallback onPostNetworkCallback = null;

        private AuthLogging(Context context, String email, String password, OnPostNetworkCallback onPostNetworkCallback) {
            this.contextWeakReference = new WeakReference<>(context);
            this.onPostNetworkCallback = onPostNetworkCallback;

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
        protected String doInBackground(Request... requests) {
            try {
                Response response = getClient().newCall(requests[0]).execute();
                return response.header("X-Auth");
            } catch(IOException e) {
                Log.v(TAG, e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String token) {
            super.onPostExecute(token);

            if(this.onPostNetworkCallback != null) {
                Context context = contextWeakReference.get();
                if(context != null)
                    this.onPostNetworkCallback.onPostNetworkCallback(context, token);
            }
        }
    }

    private abstract static class AsyncNetworkTask<T> extends AsyncTask<Request, Void, T> {

        private OkHttpClient client = new OkHttpClient();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        public OkHttpClient getClient() {
            return client;
        }
    }
}
