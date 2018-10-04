package com.example.cchiv.jiggles.model;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class Artist {

    private String id;
    private String name = null;
    private String type = "artist";
    private String uri;
    public boolean favourite = true;
    private List<String> genres = null;
    private List<Image> images = new ArrayList<>();
    private List<Album> albums = new ArrayList<>();

    public Artist(String id, String name, String type, String uri, boolean favourite, List<String> genres, List<Image> images) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.uri = uri;
        this.favourite = favourite;
        this.genres = genres;
        this.images = images;
    }

    public Artist(String name, List<String> genres) {
        this.name = name;
        this.genres = genres;
    }

    public Artist(String name, List<String> genres, List<Album> albums) {
        this.name = name;
        this.genres = genres;
        this.albums = albums;
    }

    public String getGenres() {
        if(genres != null && genres.size() > 0)
            return TextUtils.join(" | ", genres);
        else return null;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public Album addItem(Collection collection, Track track, String albumName) {
        Album album = new Album(albumName);
        album.addItem(collection, track);

        collection.getAlbums().add(album);
        albums.add(album);

        return album;
    }

    public Album updateItem(Collection collection, String albumName, Track track) {
        int it = 0;
        while(it < albums.size()) {
            Album album = albums.get(it);
            if(album.getName() != null && album.getName().equals(albumName)) {
                album.addItem(collection, track);
                album.setArtist(this);

                return null;
            }

            it++;
        }

        Album album = new Album(albumName, track);
        album.setArtist(this);
        collection.getAlbums().add(album);

        albums.add(album);

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
}