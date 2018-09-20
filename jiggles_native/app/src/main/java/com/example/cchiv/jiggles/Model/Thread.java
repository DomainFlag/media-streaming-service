package com.example.cchiv.jiggles.model;

import java.util.List;

public class Thread {

    private String _id;
    private User author;
    private String caption;
    private String content;
    private int votes;
    private List<Comm> comments;

    public Thread() {}

    public Thread(String _id, User author, String caption, String content, int votes, List<Comm> comments) {
        this._id = _id;
        this.author = author;
        this.caption = caption;
        this.content = content;
        this.votes = votes;
        this.comments = comments;
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

    public int getVotes() {
        return votes;
    }

    public List<Comm> getComments() {
        return comments;
    }
}