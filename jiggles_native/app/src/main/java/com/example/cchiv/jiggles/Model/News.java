package com.example.cchiv.jiggles.model;

public class News {

    private int _id;
    private String author;
    private String header;
    private String caption;

    public News(int _id, String author, String header, String caption) {
        this._id = _id;
        this.author = author;
        this.header = header;
        this.caption = caption;
    }

    public int get_id() {
        return _id;
    }

    public String getAuthor() {
        return author;
    }

    public String getHeader() {
        return header;
    }

    public String getCaption() {
        return caption;
    }
}
