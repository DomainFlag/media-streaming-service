package com.example.cchiv.jiggles.model;

import com.example.cchiv.jiggles.Constants;

import java.util.ArrayList;
import java.util.List;

public class Collection {

    private static final String TAG = "Collection";

    private String filterBy = Constants.ALBUM;

    private List<Artist> artists = new ArrayList<>();
    private List<Track> tracks = new ArrayList<>();
    private List<Album> albums = new ArrayList<>();

    public Collection() {}

    public Collection(List<Artist> artists, List<Track> tracks, List<Album> albums) {
        if(artists != null)
            this.artists = artists;

        if(tracks != null)
            this.tracks = tracks;

        if(albums != null)
            this.albums = albums;
    }

    public void setFilterBy() {
        this.filterBy = filterBy;
    }

    public String getFilterBy() {
        return filterBy;
    }

    private String getOptProperty(String s, String def) {
        if(s != null)
            return s;
        return def;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public void setArtist(Artist artist) {
        this.artists.add(artist);
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    public void setAlbum(Album album) {
        this.albums.add(album);
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    public Album addItem(Track track, String artistName, String albumName, List<String> genres) {
        int it = 0;
        while(it < artists.size()) {
            Artist artist = artists.get(it);
            if(artist.getName() != null && artist.getName().equals(artistName)) {
                return artist.updateItem(this, albumName, track);
            }

            it++;
        }

        Artist artist = new Artist(artistName, genres);
        artists.add(artist);

        return artist.addItem(this, track, albumName);
    }

    public int getCount() {
        switch(filterBy) {
            case Constants.ALBUM : return albums.size();
            case Constants.ARTIST : return artists.size();
            case Constants.TRACK : return tracks.size();
            case Constants.ALL : return artists.size() + albums.size() + tracks.size();
            default : return 0;
        }
    }
}
