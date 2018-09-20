package com.example.cchiv.jiggles.model;

import java.util.ArrayList;
import java.util.Date;

public class Album {

    private String id;
    private String name;
    private Date releaseDate;
    private String uri;
    public String type = "album";
    public boolean favourite = true;
    private ArrayList<Image> images;
    private ArrayList<Artist> artists;

    public Album(String id, String name, Date releaseDate, String uri, String type, boolean favourite, ArrayList<Image> images, ArrayList<Artist> artists) {
        this.id = id;
        this.name = name;
        this.releaseDate = releaseDate;
        this.uri = uri;
        this.type = type;
        this.favourite = favourite;
        this.images = images;
        this.artists = artists;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public String getUri() {
        return uri;
    }

    public ArrayList<Artist> getArtists() {
        return artists;
    }

    public ArrayList<Image> getImages() {
        return images;
    }
}