package com.example.cchiv.jiggles.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;

import com.example.cchiv.jiggles.data.ContentContract.AlbumEntry;
import com.example.cchiv.jiggles.utilities.Tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Album {

    private static final String TAG = "Album";

    private String id;
    private String name;
    private String uri;
    private Date releaseDate = new Date();
    public String type = "album";
    public boolean local = true;
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

    public Album(String id, String name, Date releaseDate, String uri, boolean local, String type, boolean favourite) {
        this.id = id;
        this.name = name;
        this.releaseDate = releaseDate;
        this.uri = uri;
        this.local = local;
        this.type = type;
        this.favourite = favourite;
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

    public void setArt(Image image) {
        this.images.add(image);
    }

    public void setArt(Uri uriArt) {
        this.images.add(new Image(uriArt));
    }

    public void setArt(List<Image> images) {
        this.images = images;
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

    public Image getArt() {
        if(images.size() > 0)
            if(images.size() == 1)
                return images.get(0);
            else return images.get(0);
        else return null;
    }

    public static boolean isUnique(Album album, Cursor cursor) {
        if(album == null)
            return false;

        int indexAlbumId = cursor.getColumnIndex(AlbumEntry._ID);
        int id = cursor.getInt(indexAlbumId);

        return album.getId().equals(String.valueOf(id));
    }

    public static Album parseCursor(Cursor cursor) {
        int indexAlbumId = cursor.getColumnIndex(AlbumEntry._ID);
        int indexAlbumName = cursor.getColumnIndex(AlbumEntry.COL_ALBUM_NAME);
        int indexAlbumReleaseDate = cursor.getColumnIndex(AlbumEntry.COL_ALBUM_RELEASE_DATE);
        int indexAlbumUri = cursor.getColumnIndex(AlbumEntry.COL_ALBUM_URI);
        int indexAlbumLocal = cursor.getColumnIndex(AlbumEntry.COL_ALBUM_LOCAL);
        int indexAlbumType = cursor.getColumnIndex(AlbumEntry.COL_ALBUM_TYPE);
        int indexAlbumFavourite = cursor.getColumnIndex(AlbumEntry.COL_ALBUM_FAVOURITE);

        int id = cursor.getInt(indexAlbumId);
        String albumName = cursor.getString(indexAlbumName);
        String albumReleaseDate = cursor.getString(indexAlbumReleaseDate);
        String albumUri = cursor.getString(indexAlbumUri);
        boolean albumLocal = cursor.getInt(indexAlbumLocal) == 1;
        String albumType = cursor.getString(indexAlbumType);
        boolean albumFavourite = cursor.getInt(indexAlbumFavourite) == 1;

        return new Album(String.valueOf(id), albumName, Tools.parseStringDate(albumReleaseDate), albumUri, albumLocal, albumType, albumFavourite);
    }

    public static ContentValues parseValues(Album album) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(AlbumEntry.COL_ALBUM_NAME, album.getName());
        contentValues.put(AlbumEntry.COL_ALBUM_RELEASE_DATE, album.getReleaseDate().toString());
        contentValues.put(AlbumEntry.COL_ALBUM_URI, album.getUri());
        contentValues.put(AlbumEntry.COL_ALBUM_LOCAL, album.local);
        contentValues.put(AlbumEntry.COL_ALBUM_TYPE, album.type);
        contentValues.put(AlbumEntry.COL_ALBUM_FAVOURITE, album.favourite);

        return contentValues;
    }
}