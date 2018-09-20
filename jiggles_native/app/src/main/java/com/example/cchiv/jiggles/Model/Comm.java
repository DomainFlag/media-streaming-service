package com.example.cchiv.jiggles.model;

public class Comm {

    private String _id;
    private User author;
    private String parent;
    private int depth;
    private String content;
    private int likes;

    public Comm() {}

    public Comm(String _id, User author, String parent, int depth, String content, int likes) {
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

    public int getLikes() {
        return likes;
    }
}
