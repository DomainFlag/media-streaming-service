package com.example.cchiv.jiggles.Model;

import java.util.ArrayList;

public class Artist {

    private String id;
    private String name;
    private String type = "artist";
    private String uri;
    public boolean favourite = true;
    private ArrayList<String> genres;
    private ArrayList<Image> images;

    public Artist(String id, String name, String type, String uri, boolean favourite, ArrayList<String> genres, ArrayList<Image> images) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.uri = uri;
        this.favourite = favourite;
        this.genres = genres;
        this.images = images;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getUri() {
        return uri;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public ArrayList<Image> getImages() {
        return images;
    }
}