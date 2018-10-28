package com.example.cchiv.jiggles.model;

import android.content.ContentValues;

import com.example.cchiv.jiggles.data.ContentContract.TrackEntry;

import java.util.ArrayList;
import java.util.List;

public class Track {

    private String id;
    private String name;
    private String type = "track";
    private String uri;
    private String path = null;
    public boolean favourite = false;
    public boolean local = true;
    private Album album;
    private List<Image> images;
    private List<Artist> artists = new ArrayList<>();

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

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public static ContentValues parseValues(Track track) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(TrackEntry.COL_TRACK_NAME, track.getName());
        contentValues.put(TrackEntry.COL_TRACK_URI, track.getUri());
        contentValues.put(TrackEntry.COL_TRACK_LOCAL, track.local);
        contentValues.put(TrackEntry.COL_TRACK_TYPE, track.type);
        contentValues.put(TrackEntry.COL_TRACK_FAVOURITE, track.favourite);

        return contentValues;
    }
}