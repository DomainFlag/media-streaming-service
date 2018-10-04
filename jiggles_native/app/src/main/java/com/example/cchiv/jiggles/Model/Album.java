package com.example.cchiv.jiggles.model;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Album {

    private String id;
    private String name;
    private Date releaseDate;
    private String uri;
    public String type = "album";
    public boolean favourite = true;
    private List<Image> images = new ArrayList<>();
    private List<Artist> artists = new ArrayList<>();
    private List<Track> tracks = new ArrayList<>();

    public Album(String id, String name, Date releaseDate, String uri, String type, boolean favourite, List<Image> images, List<Artist> artists) {
        this.id = id;
        this.name = name;
        this.releaseDate = releaseDate;
        this.uri = uri;
        this.type = type;
        this.favourite = favourite;
        this.images = images;
        this.artists = artists;
    }

    public Album(String name, List<Track> tracks) {
        this.name = name;
        this.tracks = tracks;
    }

    public Album(String name) {
        this.name = name;
    }

    public Album(String name, Track track) {
        this.name = name;
        this.tracks.add(track);
    }

    public void setArtist(Artist artist) {
        artists.add(artist);
    }

    public void setTrack(Track track) {
        this.tracks.add(track);
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setArt(Bitmap art) {
        this.images.add(new Image(art));
    }

    public void setArt(List<Bitmap> images) {
        for(Bitmap bitmap : images) {
            this.images.add(new Image(bitmap));
        }
    }

    public void addItem(Collection collection, Track track) {
        tracks.add(track);
        track.setAlbum(this);
        collection.getTracks().add(track);
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

    public List<Artist> getArtists() {
        return artists;
    }

    public Artist getArtist() {
        if(artists.size() > 0)
            return artists.get(0);
        else return null;
    }

    public List<Image> getImages() {
        return images;
    }

    public Bitmap getArt() {
        if(images.size() > 0)
            return images.get(0).getBitmap();
        else return null;
    }
}