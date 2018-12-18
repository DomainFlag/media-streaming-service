package com.example.cchiv.jiggles.model;

import java.util.HashMap;
import java.util.List;

public class Thread extends FeedItem {

    private static final String TAG = "Thread";

    private String caption;

    public Thread(String _id, User author, String caption, String content, HashMap<String, Boolean>  likes, List<Reply> replies) {
        super(_id, author, content, likes, replies);

        this.caption = caption;
    }

    public String getCaption() {
        return caption;
    }

    @Override
    public int getViewType() {
        return ViewType.VIEW_THREAD;
    }
}