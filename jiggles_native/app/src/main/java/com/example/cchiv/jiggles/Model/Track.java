package com.example.cchiv.jiggles.model;

import java.util.ArrayList;

public class Track {

    private String id;
    private String name;
    private String type = "track";
    private String uri;
    private ArrayList<Image> images;
    private ArrayList<Artist> artists;
    public boolean favourite;

    public Track(String id, String name, String type, String uri,
                 ArrayList<Image> images, ArrayList<Artist> artists, boolean favourite) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.uri = uri;
        this.images = images;
        this.artists = artists;
        this.favourite = favourite;
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

    public ArrayList<Image> getImages() {
        return images;
    }

    public ArrayList<Artist> getArtists() {
        return artists;
    }
}