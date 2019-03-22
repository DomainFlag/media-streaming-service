package com.example.cchiv.jiggles.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.provider.MediaStore;
import android.util.Log;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.data.ContentContract.TrackEntry;
import com.example.cchiv.jiggles.utilities.Tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Track {

    private static final String TAG = "Track";

    private String id;
    private String name;
    private String artistName = null;
    private String albumName = null;
    private String imageUri = null;
    private String type = "track";
    private String uri;
    private Bitmap bitmap = null;
    public boolean favourite = false;
    public boolean local = false;
    private transient Album album = null;
    private List<Image> images = null;
    private transient List<Artist> artists = new ArrayList<>();

    public Track(String id, String name, String type, String uri,
                 List<Image> images, List<Artist> artists, boolean favourite) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.uri = uri;
        this.images = images;
        this.artists = artists;
        this.favourite = favourite;


        if(this.artists.size() > 0)
            this.artistName = this.artists.get(0).getName();
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
        this.local = true;
    }

    public Artist getArtist() {
        if(artists.size() > 0)
            return artists.get(0);
        else return null;
    }

    public String getArtistName() {
        if(artists != null && artists.size() > 0) {
            return artists.get(0).getName();
        } else {
            return artistName;
        }
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Image getArt() {
        if(album != null) {
            return album.getArt();
        }

        return null;
    }

    public int getColor(Context context) {
        Image image = getArt();

        if(image == null) {
            if(bitmap != null) {
                return Tools.getPaletteColor(context, bitmap);
            } else if(imageUri != null) {
                return Tools.getPaletteColor(context, imageUri);
            }
        } else {
            if(image.getColor() == Color.WHITE || image.color == -1 || image.color == 0)
                return Color.BLACK;
            else return image.getColor();
        }

        return Color.BLACK;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setArtist(Artist artist) {
        artistName = artist.getName();

        artists.add(artist);
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public void setId(String id) {
        this.id = id;
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

    public Bitmap getBitmap(Context context) {
        if(bitmap != null)
            return bitmap;

        Image art = null;
        if(album != null)
            art = album.getArt();

        Bitmap bitmap = null;
        if(art == null) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_artwork_placeholder);
        } else {
            try {
                if(art.getUrl() != null)
                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), art.getUrl());
            } catch(IOException e) {
                Log.v(TAG, e.toString());
            }
        }

        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
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

        return new Track(String.valueOf(id), trackName, trackType, trackUri, trackLocal, trackFavourite);
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