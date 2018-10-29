package com.example.cchiv.jiggles.model;

import android.content.ContentProviderOperation;
import android.database.Cursor;

import com.example.cchiv.jiggles.Constants;
import com.example.cchiv.jiggles.data.ContentContract.AlbumEntry;
import com.example.cchiv.jiggles.data.ContentContract.ArtistEntry;
import com.example.cchiv.jiggles.data.ContentContract.ImageEntry;
import com.example.cchiv.jiggles.data.ContentContract.TrackEntry;

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

    public int getCount(String filterBy) {
        switch(filterBy) {
            case Constants.ALBUM : return albums.size();
            case Constants.ARTIST : return artists.size();
            case Constants.TRACK : return tracks.size();
            case Constants.ALL : return artists.size() + albums.size() + tracks.size();
            default : return 0;
        }
    }

    public int getCount() {
        return getCount(filterBy);
    }

    public static Collection parseCursor(Cursor cursor) {
        Collection collection = new Collection();

        Artist artist = null;
        Album album = null;
        Track track = null;
        Image image = null;
        while(cursor.moveToNext()) {
            if(!Artist.isUnique(artist, cursor)) {
                artist = Artist.parseCursor(cursor);

                collection.artists.add(artist);
            }

            if(!Album.isUnique(album, cursor)) {
                album = Album.parseCursor(cursor);
                album.setArtist(artist);

                artist.getAlbums().add(album);
                collection.albums.add(album);
            }

            if(!Image.isUnique(image, cursor)) {
                image = Image.parseCursor(cursor);

                album.setArt(image);
            }

            if(!Track.isUnique(track, cursor)) {
                track = Track.parseCursor(cursor);
                track.setArtist(artist);

                album.getTracks().add(track);
                collection.tracks.add(track);
            }
        }

        return collection;
    }

    public static ArrayList<ContentProviderOperation> parseValues(Collection collection) {
        ArrayList<ContentProviderOperation> contentProviderOperations = new ArrayList<>();
        List<Artist> artists = collection.getArtists();

        int index = 0;
        for(int g = 0; g < artists.size(); g++) {
            Artist artist = artists.get(g);
            int artistIndex = index;

            contentProviderOperations.add(
                    ContentProviderOperation
                            .newInsert(ArtistEntry.CONTENT_URI)
                            .withValues(Artist.parseValues(artist))
                            .build());

            index++;

            List<Album> albums = artist.getAlbums();
            for(int h = 0; h < albums.size(); h++) {
                Album album = albums.get(h);

                int albumIndex = index;

                contentProviderOperations.add(
                        ContentProviderOperation
                                .newInsert(AlbumEntry.CONTENT_URI)
                                .withValues(Album.parseValues(album))
                                .withValueBackReference(AlbumEntry.COL_ALBUM_ARTIST, artistIndex)
                                .build());

                index++;

                List<Image> images = album.getImages();
                for(int i = 0; i < images.size(); i++) {
                    Image image = images.get(i);

                    contentProviderOperations.add(
                            ContentProviderOperation
                                    .newInsert(ImageEntry.CONTENT_URI)
                                    .withValues(Image.parseValues(image))
                                    .withValueBackReference(ImageEntry.COL_IMAGE_ALBUM, albumIndex)
                                    .build());

                    index++;
                }

                List<Track> tracks = album.getTracks();
                for(int i = 0; i < tracks.size(); i++) {
                    Track track = tracks.get(i);

                    contentProviderOperations.add(
                            ContentProviderOperation
                                    .newInsert(TrackEntry.CONTENT_URI)
                                    .withValues(Track.parseValues(track))
                                    .withValueBackReference(TrackEntry.COL_TRACK_ALBUM, albumIndex)
                                    .build());

                    index++;
                }
            }
        }

        return contentProviderOperations;
    }
}
