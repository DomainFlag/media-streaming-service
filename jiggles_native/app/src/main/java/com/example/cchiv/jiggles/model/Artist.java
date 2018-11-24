package com.example.cchiv.jiggles.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.example.cchiv.jiggles.data.ContentContract.ArtistEntry;

import java.util.ArrayList;
import java.util.List;

public class Artist {

    private static final String TAG = "Artist";

    private String id;
    private String name;
    private String type = "artist";
    private String uri;
    public boolean favourite = true;
    public boolean local = true;
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

    public Artist(String id, String name, String uri, boolean local, String type, boolean favourite) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.uri = uri;
        this.local = local;
        this.favourite = favourite;
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

    public Album addItem(Store store, Track track, String albumName) {
        Album album = new Album(albumName);
        album.addItem(store, track);

        store.getAlbums().add(album);
        albums.add(album);

        return album;
    }

    public Album updateItem(Store store, String albumName, Track track) {
        int it = 0;
        while(it < albums.size()) {
            Album album = albums.get(it);
            if(album.getName() != null && album.getName().equals(albumName)) {
                album.addItem(store, track);
                album.setArtist(this);

                return null;
            }

            it++;
        }

        Album album = new Album(albumName, track);
        album.setArtist(this);
        store.getAlbums().add(album);

        albums.add(album);

        return album;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        if(name != null)
            return name;
        else return "";
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

    public static boolean isUnique(Artist artist, Cursor cursor) {
        if(artist == null)
            return false;

        int indexArtistId = cursor.getColumnIndex(ArtistEntry._ID);
        int id = cursor.getInt(indexArtistId);

        return artist.getId().equals(String.valueOf(id));
    }

    public static Artist parseCursor(Cursor cursor) {
        int indexArtistId = cursor.getColumnIndex(ArtistEntry._ID);
        int indexArtistName = cursor.getColumnIndex(ArtistEntry.COL_ARTIST_NAME);
        int indexArtistUri = cursor.getColumnIndex(ArtistEntry.COL_ARTIST_URI);
        int indexArtistType = cursor.getColumnIndex(ArtistEntry.COL_ARTIST_TYPE);
        int indexArtistLocal = cursor.getColumnIndex(ArtistEntry.COL_ARTIST_LOCAL);
        int indexArtistFavourite = cursor.getColumnIndex(ArtistEntry.COL_ARTIST_FAVOURITE);

        int id = cursor.getInt(indexArtistId);
        String artistName = cursor.getString(indexArtistName);
        String artistUri = cursor.getString(indexArtistUri);
        boolean artistLocal = cursor.getInt(indexArtistLocal) == 1;
        String artistType = cursor.getString(indexArtistType);
        boolean artistFavourite = cursor.getInt(indexArtistFavourite) == 1;

        return new Artist(String.valueOf(id), artistName, artistUri, artistLocal, artistType, artistFavourite);
    }

    public static ContentValues parseValues(Artist artist) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ArtistEntry.COL_ARTIST_NAME, artist.getName());
        contentValues.put(ArtistEntry.COL_ARTIST_URI, artist.getUri());
        contentValues.put(ArtistEntry.COL_ARTIST_TYPE, artist.getType());
        contentValues.put(ArtistEntry.COL_ARTIST_LOCAL, artist.local);
        contentValues.put(ArtistEntry.COL_ARTIST_FAVOURITE, artist.favourite);

        return contentValues;
    }
}