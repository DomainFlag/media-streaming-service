package com.example.cchiv.jiggles.model;

import android.util.Log;

import com.example.cchiv.jiggles.data.ContentContract;
import com.example.cchiv.jiggles.utilities.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Thread extends FeedItem {

    private static final String TAG = "Thread";

    private String _id;
    private User author;
    private String caption;
    private String content;
    private Boolean ownership = null;
    private HashMap<String, Boolean> likes;
    private List<Reply> replies = new ArrayList<>();

    public Thread() {}

    public Thread(String _id, User author, String caption, String content, HashMap<String, Boolean>  likes, List<Reply> replies) {
        this._id = _id;
        this.author = author;
        this.caption = caption;
        this.content = content;
        this.likes = likes;
        this.replies = replies;
        this.ownership = isOwnership();
    }

    public boolean isOwnership() {
        if(ownership == null)
            ownership = likes.containsKey(Tools.getUser().get_id());

        return ownership;
    }

    public String getId() {
        return _id;
    }

    public User getAuthor() {
        return author;
    }

    public String getCaption() {
        return caption;
    }

    public String getContent() {
        return content;
    }

    public HashMap<String, Boolean>  getLikes() {
        return likes;
    }

    public List<Reply> getReplies() {
        return replies;
    }

    public JSONObject encodeJSONObject() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(ContentContract.ThreadEntry._ID, _id);
        } catch(JSONException e) {
            Log.v(TAG, e.toString());
        }

        return jsonObject;
    }

    @Override
    public int getViewType() {
        return ViewType.VIEW_THREAD;
    }
}