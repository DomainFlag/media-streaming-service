package com.example.cchiv.jiggles.model;

import android.util.Log;

import com.example.cchiv.jiggles.Constants;
import com.example.cchiv.jiggles.data.ContentContract;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class FeedItem {

    private static final String TAG = "FeedItem";

    public class ViewType {
        public final static int VIEW_POST = 0;
        public final static int VIEW_THREAD = 1;
        public final static int VIEW_REPLY = 2;
    }

    private String _id;
    private User author;
    private String authorName;
    private String content;
    private HashMap<String, Boolean> likes;
    private List<Reply> replies;

    public FeedItem(String _id, User author, String content, HashMap<String, Boolean> likes, List<Reply> replies) {
        this._id = _id;
        this.author = author;
        this.content = content;
        this.likes = likes;
        this.replies = replies;
    }

    public String get_id() {
        return _id;
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

    public User getAuthor() {
        return author;
    }

    public String getAuthorName() {
        return authorName;
    }

    public JSONObject encodeJSONObject(Reply reply, String content) {
        JSONObject jsonObject = new JSONObject();

        try {
            switch(getViewType()) {
                case ViewType.VIEW_POST : {
                    jsonObject.put(ContentContract.PostEntry._ID, get_id());
                    break;
                }
                case ViewType.VIEW_THREAD : {
                    jsonObject.put(ContentContract.ThreadEntry._ID, get_id());
                    break;
                }
                default : {
                    return null;
                }
            }

            if(content != null) {
                String parent = null;
                int depth = 0;

                if(reply != null) {
                    parent = reply.getParent();
                    depth = reply.getDepth();
                }

                jsonObject.put(getSimpleColumnName(ContentContract.ReplyEntry.COL_REPLY_PARENT), parent);
                jsonObject.put(getSimpleColumnName(ContentContract.ReplyEntry.COL_REPLY_CONTENT), content);
                jsonObject.put(getSimpleColumnName(ContentContract.ReplyEntry.COL_REPLY_DEPTH), depth);
            }
        } catch(JSONException e) {
            Log.v(TAG, e.toString());
        }

        return jsonObject;
    }

    private String getSimpleColumnName(String columnName) {
        String[] name = columnName.split("_");
        if(name.length == 2)
            return name[1];
        else return null;
    }

    public abstract int getViewType();

    public static FeedItem decodeMessage(RemoteMessage remoteMessage, Gson gson) {
        Map<String, String> data = remoteMessage.getData();

        JSONObject jsonObject = new JSONObject(data);

        JsonParser jsonParser = new JsonParser();
        JsonObject outerRoot = jsonParser.parse(jsonObject.toString()).getAsJsonObject();

        String type = data.get(Constants.FEED_ITEM_TYPE);
        if(type != null) {
            switch(type) {
                case Constants.THREAD : {
                    return gson.fromJson(outerRoot, Thread.class);
                }
                case Constants.POST : {
                    return gson.fromJson(outerRoot, Post.class);
                }
            }
        }

        return null;
    }
}
