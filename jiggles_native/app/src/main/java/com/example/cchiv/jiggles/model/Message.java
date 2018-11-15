package com.example.cchiv.jiggles.model;

import android.content.ContentValues;

import com.example.cchiv.jiggles.data.ContentContract.NotificationEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Message {

    private String _id;
    private User author;
    private String type;
    private String resource;
    private String identifier;
    private int votes;
    private List<Comm> comments = new ArrayList<>();

    public Message(String _id, User author, String type, String resource, String identifier, int votes) {
        this._id = _id;
        this.author = author;
        this.type = type;
        this.resource = resource;
        this.identifier = identifier;
        this.votes = votes;
    }

    public Message(String _id, User author, String type, String resource, String identifier, int votes, List<Comm> comments) {
        this(_id, author, type, resource, identifier, votes);

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

    public String getIdentifier() {
        return identifier;
    }

    public int getVotes() {
        return votes;
    }

    public List<Comm> getComments() {
        return comments;
    }

    public static Message decodeMessage(Map<String, String> message) {
        String _id = message.get(NotificationEntry._ID);
        String author = message.get(NotificationEntry.COL_NOTIFICATION_AUTHOR);
        String type = message.get(NotificationEntry.COL_NOTIFICATION_TYPE);
        String resource = message.get(NotificationEntry.COL_NOTIFICATION_RESOURCE);
        String identifier = message.get(NotificationEntry.COL_NOTIFICATION_IDENTIFIER);
        int votes = Integer.valueOf(message.get(NotificationEntry.COL_NOTIFICATION_VOTES));

        return new Message(_id, null, type, resource, identifier, votes);
    }

    public static ContentValues parseValues(Message message) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(NotificationEntry.COL_NOTIFICATION_AUTHOR, message.getAuthor().getName());
        contentValues.put(NotificationEntry.COL_NOTIFICATION_IDENTIFIER, message.getIdentifier());
        contentValues.put(NotificationEntry.COL_NOTIFICATION_TYPE, message.getType());
        contentValues.put(NotificationEntry.COL_NOTIFICATION_RESOURCE, message.getResource());

        return contentValues;
    }
}
