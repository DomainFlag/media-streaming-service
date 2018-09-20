package com.example.cchiv.jiggles.model;

import java.util.ArrayList;

public class Release {

    private String _id;
    private String title;
    private String artist;
    private String url;

    private ArrayList<Review> reviews;

    public Release(String _id, String title, String artist, String url, ArrayList<Review> reviews) {
        this._id = _id;
        this.title = title;
        this.artist = artist;
        this.url = url;
        this.reviews = reviews;
    }

    public String get_id() {
        return _id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getUrl() {
        return url;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }
}
