package com.example.cchiv.jiggles.model;

public class Like {

    private static final String TAG = "Like";

    private String _id;
    private User author;

    public Like(String _id, User author, int like) {
        this._id = _id;
        this.author = author;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String get_id() {
        return _id;
    }

    public User getAuthor() {
        return author;
    }
}
