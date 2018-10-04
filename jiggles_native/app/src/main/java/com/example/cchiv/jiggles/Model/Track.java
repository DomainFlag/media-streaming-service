package com.example.cchiv.jiggles.model;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class Track {

    private String id;
    private String name;
    private String type = "track";
    private String uri;
    private String path = null;
    private Bitmap art = null;
    private Album album;
    private List<Image> images;
    private List<Artist> artists = new ArrayList<>();
    public boolean favourite;

    public Track(String id, String name, String type, String uri,
                 List<Image> images, List<Artist> artists, boolean favourite) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.uri = uri;
        this.images = images;
        this.artists = artists;
        this.favourite = favourite;
    }

    public Track(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public Artist getArtist() {
        if(artists.size() > 0)
            return artists.get(0);
        else return null;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public void setArtist(Artist artist) {
        artists.add(artist);
    }

    public Track(String name) {
        this.name = name;
    }

    public Album getAlbum() {
        return album;
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

    public List<Image> getImages() {
        return images;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArt(Bitmap bitmap) {
        this.art = bitmap;
    }

    public Bitmap getArt() {
        return art;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}