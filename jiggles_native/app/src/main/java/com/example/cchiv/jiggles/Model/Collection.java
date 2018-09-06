package com.example.cchiv.jiggles.Model;

import java.util.ArrayList;

public class Collection {

    private ArrayList<Artist> artists;
    private ArrayList<Track> tracks;
    private ArrayList<Album> albums;

    public Collection(ArrayList<Artist> artists, ArrayList<Track> tracks, ArrayList<Album> albums) {
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
}
