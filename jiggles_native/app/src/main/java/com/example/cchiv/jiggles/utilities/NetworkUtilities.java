package com.example.cchiv.jiggles.utilities;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.cchiv.jiggles.Constants;
import com.example.cchiv.jiggles.model.News;
import com.example.cchiv.jiggles.model.Post;
import com.example.cchiv.jiggles.model.Release;
import com.example.cchiv.jiggles.model.Reply;
import com.example.cchiv.jiggles.model.Store;
import com.example.cchiv.jiggles.model.Thread;
import com.example.cchiv.jiggles.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
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
    public static class FetchNews extends AsyncNetworkTask<List<News>> {

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
    public static class FetchFreshRelease extends AsyncNetworkTask<List<Release>> {

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
    public static class FetchReleases extends AsyncNetworkTask<List<Release>> {

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
    public static class FetchThreads extends AsyncNetworkTask<List<Thread>> {

        public FetchThreads(NetworkCallbacks<List<Thread>> networkCallbacks) {
            super(networkCallbacks);

            Uri uri = new Uri.Builder()
                    .scheme(Constants.SCHEME)
                    .authority(Constants.AUTHORITY)
                    .appendPath(Constants.FEED)
                    .appendPath(Constants.THREAD)
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
            Type type = new TypeToken<List<Thread>>() {}.getType();

            return gson.fromJson(body, type);
        }
    }

    /* Fetch Posts */
    public static class FetchPosts extends AsyncNetworkTask<List<Post>> {

        public FetchPosts(NetworkCallbacks<List<Post>> networkCallbacks, String token) {
            super(networkCallbacks);

            Uri uri = new Uri.Builder()
                    .scheme(Constants.SCHEME)
                    .authority(Constants.AUTHORITY)
                    .appendPath(Constants.FEED)
                    .appendPath(Constants.POST)
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
        public List<Post> onParseNetworkCallback(Gson gson, Headers headers, String body) {
            Type type = new TypeToken<List<Post>>() {}.getType();

            return gson.fromJson(body, type);
        }
    }

    /* Resolve Thread Like */
    public static class ResolveThreadLike extends AsyncNetworkTask<Thread> {

        public ResolveThreadLike(NetworkCallbacks<Thread> networkCallbacks, JSONObject body, String type, String token) {
            super(networkCallbacks);

            Uri uri = new Uri.Builder()
                    .scheme(Constants.SCHEME)
                    .authority(Constants.AUTHORITY)
                    .appendPath(Constants.FEED)
                    .appendPath(Constants.THREAD)
                    .appendPath(Constants.LIKE)
                    .build();

            RequestBody requestBody = RequestBody.create(JSON, body.toString());

            Request request = new RequestAdaptBuilder()
                    .setType(requestBody, type)
                    .url(uri.toString())
                    .addHeader("Content-Type", "application/json")
                    .addHeader("X-Auth", token)
                    .build();

            execute(request);
        }

        @Override
        public Thread onParseNetworkCallback(Gson gson, Headers headers, String body) {
            Type type = new TypeToken<Thread>() {}.getType();

            return gson.fromJson(body, type);
        }
    }

    /* Resolve Reply Like */
    public static class ResolveCommentLike extends AsyncNetworkTask<Reply> {

        public ResolveCommentLike(NetworkCallbacks<Reply> networkCallbacks, JSONObject jsonObject, String type, String token) {
            super(networkCallbacks);

            Uri uri = new Uri.Builder()
                    .scheme(Constants.SCHEME)
                    .authority(Constants.AUTHORITY)
                    .appendPath(Constants.FEED)
                    .appendPath(Constants.THREAD)
                    .appendPath(Constants.REPLY)
                    .appendPath(Constants.LIKE)
                    .build();

            RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());

            Request request = new RequestAdaptBuilder()
                    .setType(requestBody, type)
                    .url(uri.toString())
                    .addHeader("Content-Type", "application/json")
                    .addHeader("X-Auth", token)
                    .build();

            execute(request);
        }

        @Override
        public Reply onParseNetworkCallback(Gson gson, Headers headers, String body) {
            Type type = new TypeToken<Reply>() {}.getType();

            return gson.fromJson(body, type);
        }
    }

    /* Resolve Reply Like */
    public static class ResolveReplyAction extends AsyncNetworkTask<Thread> {

        public ResolveReplyAction(NetworkCallbacks<Thread> networkCallbacks, JSONObject jsonObject, String type, String token) {
            super(networkCallbacks);

            Uri uri = new Uri.Builder()
                    .scheme(Constants.SCHEME)
                    .authority(Constants.AUTHORITY)
                    .appendPath(Constants.FEED)
                    .appendPath(Constants.THREAD)
                    .appendPath(Constants.REPLY)
                    .build();

            RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());

            Request request = new RequestAdaptBuilder()
                    .setType(requestBody, type)
                    .url(uri.toString())
                    .addHeader("Content-Type", "application/json")
                    .addHeader("X-Auth", token)
                    .build();

            execute(request);
        }

        @Override
        public Thread onParseNetworkCallback(Gson gson, Headers headers, String body) {
            Type type = new TypeToken<Thread>() {}.getType();

            return gson.fromJson(body, type);
        }
    }

    /* Fetch Search Results */
    public static class FetchSearchResults extends AsyncNetworkTask<Store> {

        public FetchSearchResults(NetworkCallbacks<Store> networkCallbacks, String query, String token) {
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
        public Store onParseNetworkCallback(Gson gson, Headers headers, String body) {
            Type type = new TypeToken<Store>() {}.getType();

            return gson.fromJson(body, type);
        }
    }

    /* Resolve Thread Creation */
    public static class ResolveCreateThread extends AsyncNetworkTask<Thread> {

        public ResolveCreateThread(NetworkCallbacks<Thread> networkCallbacks, JSONObject jsonObject, String token) {
            super(networkCallbacks);

            Uri uri = new Uri.Builder()
                    .scheme(Constants.SCHEME)
                    .authority(Constants.AUTHORITY)
                    .appendPath(Constants.FEED)
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

    /* Fetch User  */
    public static class FetchUser extends AsyncNetworkTask<User> {

        public FetchUser(NetworkCallbacks<User> networkCallbacks, String token) {
            super(networkCallbacks);

            Uri uri = new Uri.Builder()
                    .scheme(Constants.SCHEME)
                    .authority(Constants.AUTHORITY)
                    .appendPath(Constants.USER)
                    .appendPath(Constants.ME)
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
        public User onParseNetworkCallback(Gson gson, Headers headers, String body) {
            Type type = new TypeToken<User>() {}.getType();

            return gson.fromJson(body, type);
        }
    }

    /* Resolve Store */
    public static class ResolveStore extends AsyncNetworkTask<Store> {

        public ResolveStore(NetworkCallbacks<Store> networkCallbacks, Store store, String token) {
            super(networkCallbacks);

            Uri uri = new Uri.Builder()
                    .scheme(Constants.SCHEME)
                    .authority(Constants.AUTHORITY)
                    .appendPath(Constants.USER)
                    .appendPath(Constants.STORE)
                    .build();

            Gson gson = new Gson();
            RequestBody requestBody = RequestBody.create(JSON, gson.toJson(store));

            Request request = new Request.Builder()
                    .url(uri.toString())
                    .addHeader("Content-Type", "application/json")
                    .addHeader("X-Auth", token)
                    .post(requestBody)
                    .build();

            execute(request);
        }

        @Override
        public Store onParseNetworkCallback(Gson gson, Headers headers, String body) {
            Type type = new TypeToken<Store>() {}.getType();

            return gson.fromJson(body, type);
        }
    }

    /* Resolve Auth */
    public static class ResolveAuth extends AsyncNetworkTask<String> {

        public ResolveAuth(NetworkCallbacks<String> networkCallbacks, String authType,
                           String email, String password, String name) {
            super(networkCallbacks);

            Uri.Builder builder = new Uri.Builder()
                    .scheme(Constants.SCHEME)
                    .authority(Constants.AUTHORITY)
                    .appendPath(Constants.USER);

            if(authType.equals(Constants.AUTH_SIGN_IN))
                builder.appendPath(Constants.LOGIN);

            Uri uri = builder.build();

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", email);
                jsonObject.put("password", password);
                jsonObject.put("name", name);

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

    public static class RequestAdaptBuilder extends Request.Builder {
        private static final String TYPE_POST = "TYPE_POST";
        private static final String TYPE_DELETE = "TYPE_DELETE";

        public Request.Builder setType(RequestBody requestBody, String type) {
            switch(type) {
                case TYPE_POST : return post(requestBody);
                case TYPE_DELETE : return delete(requestBody);
                default: return this;
            }
        }

        public static String getType(boolean ownership) {
            return ownership ? TYPE_DELETE : TYPE_POST;
        }
    }

    public abstract static class AsyncNetworkTask<T> extends AsyncTask<Request, Void, T> {

        private static Gson gson = new Gson();

        public interface NetworkCallbacks<T> {
            void onPostNetworkCallback(T result);
        }

        private NetworkCallbacks<T> networkCallbacks;

        private OkHttpClient client = new OkHttpClient();

        public abstract T onParseNetworkCallback(Gson gson, Headers headers, String body);

        public AsyncNetworkTask(NetworkCallbacks<T> networkCallbacks) {
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
