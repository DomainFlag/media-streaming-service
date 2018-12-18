package com.example.cchiv.jiggles.model;

import android.util.Log;

import com.example.cchiv.jiggles.data.ContentContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class Post extends FeedItem {

    private static final String TAG = "Post";

    private Store store;

    public Post(String _id, User author, Store store, String content, HashMap<String, Boolean> likes, List<Reply> replies) {
        super(_id, author, content, likes, replies);

        this.store = store;
    }

    public Album getAlbum() {
        List<Album> albums = store.getAlbums();
        if(!albums.isEmpty())
            return albums.get(0);

        return null;
    }

    public Store getStore() {
        return store;
    }

    public JSONObject encodeJSONObject() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(ContentContract.PostEntry._ID, get_id());
        } catch(JSONException e) {
            Log.v(TAG, e.toString());
        }

        return jsonObject;
    }

    @Override
    public int getViewType() {
        return ViewType.VIEW_POST;
    }
}
