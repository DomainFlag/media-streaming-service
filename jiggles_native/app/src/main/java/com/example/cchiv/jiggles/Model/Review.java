package com.example.cchiv.jiggles.Model;

public class Review {

    private String _id;
    private String author;
    private String content;
    private String url;
    private int score;

    public Review(String _id, String author, String content, String url, int score) {
        this._id = _id;
        this.author = author;
        this.content = content;
        this.url = url;
        this.score = score;
    }

    public String get_id() {
        return _id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

    public int getScore() {
        return score;
    }
}
