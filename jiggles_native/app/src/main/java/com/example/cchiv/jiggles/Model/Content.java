package com.example.cchiv.jiggles.model;

import java.util.ArrayList;

public class Content {

    private ArrayList<Artist> artists;
    private ArrayList<Track> tracks;
    private ArrayList<Album> albums;

    public Content() {}

    public Content(ArrayList<Artist> artists, ArrayList<Track> tracks, ArrayList<Album> albums) {
        this.artists = artists;
        this.tracks = tracks;
        this.albums = albums;
    }

    public ArrayList<Artist> getArtists() {
        return artists;
    }

    public ArrayList<Album> getAlbums() {
        return albums;
    }

    public ArrayList<Track> getTracks() {
        return tracks;
    }

    public void setArtists(ArrayList<Artist> artists) {
        this.artists = artists;
    }

    public void setAlbums(ArrayList<Album> albums) {
        this.albums = albums;
    }

    public void setTracks(ArrayList<Track> tracks) {
        this.tracks = tracks;
    }

    public int getCount() {
        return artists.size() + tracks.size() + albums.size();
    }
}
