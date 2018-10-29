package com.example.cchiv.jiggles.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.cchiv.jiggles.data.ContentContract.TrackEntry;

import java.util.ArrayList;
import java.util.List;

public class Track {

    private static final String TAG = "Track";

    private String id;
    private String name;
    private String type = "track";
    private String uri;
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

    public Track(String id, String name, String type, String uri,
                 boolean local, boolean favourite) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.uri = uri;
        this.local = local;
        this.favourite = favourite;
    }

    public Track(String name, String uri) {
        this.name = name;
        this.uri = uri;
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

    public void setUri(String uri) {
        this.uri = uri;
    }

    public static boolean isUnique(Track track, Cursor cursor) {
        if(track == null)
            return false;

        int indexTrackId = cursor.getColumnIndex(TrackEntry._ID);
        int id = cursor.getInt(indexTrackId);

        return track.getId().equals(String.valueOf(id));
    }

    public static Track parseCursor(Cursor cursor) {
        if(cursor == null)
            return null;

        int indexTrackId = cursor.getColumnIndex(TrackEntry._ID);
        int indexTrackAlbum = cursor.getColumnIndex(TrackEntry.COL_TRACK_ALBUM);
        int indexTrackName = cursor.getColumnIndex(TrackEntry.COL_TRACK_NAME);
        int indexTrackUri = cursor.getColumnIndex(TrackEntry.COL_TRACK_URI);
        int indexTrackLocal = cursor.getColumnIndex(TrackEntry.COL_TRACK_LOCAL);
        int indexTrackType = cursor.getColumnIndex(TrackEntry.COL_TRACK_TYPE);
        int indexTrackFavourite = cursor.getColumnIndex(TrackEntry.COL_TRACK_FAVOURITE);

        int id = cursor.getInt(indexTrackId);
        int trackAlbum = cursor.getInt(indexTrackAlbum);
        String trackName = cursor.getString(indexTrackName);
        String trackUri = cursor.getString(indexTrackUri);
        boolean trackLocal = cursor.getInt(indexTrackLocal) == 1;
        String trackType = cursor.getString(indexTrackType);
        boolean trackFavourite = cursor.getInt(indexTrackFavourite) == 1;

        Album album = Album.parseCursor(cursor);
        Image image = Image.parseCursor(cursor);
        album.setArt(image);

        Artist artist = Artist.parseCursor(cursor);
        album.setArtist(artist);

        Track track = new Track(String.valueOf(id), trackName, trackType, trackUri, trackLocal, trackFavourite);
        track.setArtist(artist);
        track.setAlbum(album);

        return track;
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