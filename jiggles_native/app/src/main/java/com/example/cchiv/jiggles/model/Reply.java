package com.example.cchiv.jiggles.model;

import android.util.Log;

import com.example.cchiv.jiggles.data.ContentContract.CommentEntry;
import com.example.cchiv.jiggles.data.ContentContract.ThreadEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Reply {

    private static final String TAG = "Reply";

    private String _id;
    private User author;
    private String parent;
    private int depth;
    private String content;
    private Thread thread;
    private HashMap<String, Boolean> likes;

    public Reply() {}

    public Reply(String _id, User author, String parent, int depth, String content, HashMap<String, Boolean> likes) {
        this._id = _id;
        this.author = author;
        this.parent = parent;
        this.depth = depth;
        this.content = content;
        this.likes = likes;
    }

    public String getId() {
        return _id;
    }

    public User getAuthor() {
        return author;
    }

    public String getParent() {
        return parent;
    }

    public int getDepth() {
        return depth;
    }

    public String getContent() {
        return content;
    }

    public HashMap<String, Boolean> getLikes() {
        return likes;
    }

    public JSONObject encodeJSONObject() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(ThreadEntry._ID, thread.getId());
            jsonObject.put(CommentEntry._ID, _id);
        } catch(JSONException e) {
            Log.v(TAG, e.toString());
        }

        return jsonObject;
    }
}
