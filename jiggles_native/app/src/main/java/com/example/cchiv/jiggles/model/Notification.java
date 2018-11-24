package com.example.cchiv.jiggles.model;

import android.content.ContentValues;

import com.example.cchiv.jiggles.data.ContentContract.NotificationEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Notification {

    private static final String TAG = "Notification";

    private String _id;
    private User author;
    private String type;
    private String resource;
    private int votes;
    private List<Reply> comments = new ArrayList<>();

    public Notification(String _id, User author, String type, String resource, int votes) {
        this._id = _id;
        this.author = author;
        this.type = type;
        this.resource = resource;
        this.votes = votes;
    }

    public Notification(String _id, User author, String type, String resource, int votes, List<Reply> comments) {
        this(_id, author, type, resource, votes);

        this.comments = comments;
    }

    public String getId() {
        return _id;
    }

    public User getAuthor() {
        return author;
    }

    public String getType() {
        return type;
    }

    public String getResource() {
        return resource;
    }

    public int getVotes() {
        return votes;
    }

    public List<Reply> getComments() {
        return comments;
    }

    public static Notification decodeMessage(Map<String, String> message) {
        String _id = message.get(NotificationEntry._ID);
        String author = message.get(NotificationEntry.COL_NOTIFICATION_AUTHOR);
        String type = message.get(NotificationEntry.COL_NOTIFICATION_TYPE);
        String resource = message.get(NotificationEntry.COL_NOTIFICATION_RESOURCE);
        int votes = Integer.valueOf(message.get(NotificationEntry.COL_NOTIFICATION_VOTES));

        return new Notification(_id, null, type, resource, votes);
    }

    public static ContentValues parseValues(Notification notification) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(NotificationEntry.COL_NOTIFICATION_AUTHOR, notification.getAuthor().getName());
        contentValues.put(NotificationEntry.COL_NOTIFICATION_TYPE, notification.getType());
        contentValues.put(NotificationEntry.COL_NOTIFICATION_RESOURCE, notification.getResource());

        return contentValues;
    }
}
