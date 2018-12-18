package com.example.cchiv.jiggles.model;

import android.util.Log;

import com.example.cchiv.jiggles.data.ContentContract.ReplyEntry;
import com.example.cchiv.jiggles.data.ContentContract.ThreadEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Reply extends FeedItem {

    private static final String TAG = "Reply";

    private String parent;
    private int depth;
    private Thread thread;

    public Reply(String _id, User author, String parent, int depth, String content, HashMap<String, Boolean> likes) {
        super(_id, author, content, likes, null);

        this.parent = parent;
        this.depth = depth;
    }

    public String getParent() {
        return parent;
    }

    public int getDepth() {
        return depth;
    }

    public JSONObject encodeJSONObject() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(ThreadEntry._ID, thread.get_id());
            jsonObject.put(ReplyEntry._ID, get_id());
        } catch(JSONException e) {
            Log.v(TAG, e.toString());
        }

        return jsonObject;
    }

    @Override
    public int getViewType() {
        return ViewType.VIEW_REPLY;
    }
}
