package com.example.cchiv.jiggles.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cchiv.jiggles.Model.Album;
import com.example.cchiv.jiggles.Model.Artist;
import com.example.cchiv.jiggles.Model.Content;
import com.example.cchiv.jiggles.Model.Image;
import com.example.cchiv.jiggles.Model.Track;
import com.example.cchiv.jiggles.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private class ViewType {
        private final static int VIEW_ARTIST = 11;
        private final static int VIEW_ARTIST_HIGHLIGHT = 12;
        private final static int VIEW_ALBUM = 14;
        private final static int VIEW_ALBUM_HIGHLIGHT = 15;
        private final static int VIEW_TRACK = 17;
        private final static int VIEW_TRACK_HIGHLIGHT = 18;

        private final static int VIEW_DIVIDER = 20;
    }

    private static final String TAG = "ContentAdapter";

    private Context context;

    private Content content;

    public ContentAdapter(Context context, Content content) {
        this.context = context;

        this.content = content;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        switch(viewType) {
            case ViewType.VIEW_ARTIST : {
                return new ArtistViewHolder(
                        layoutInflater
                                .inflate(R.layout.content_artist_layout, parent, false));
            }
            case ViewType.VIEW_ALBUM : {
                return new AlbumViewHolder(
                        layoutInflater
                                .inflate(R.layout.content_album_layout, parent, false));
            }
            case ViewType.VIEW_TRACK : {
                return new TrackViewHolder(
                        layoutInflater
                                .inflate(R.layout.content_track_layout, parent, false));
            }
            case ViewType.VIEW_DIVIDER : {
                return new DividerViewHolder(
                        layoutInflater
                                .inflate(R.layout.content_divider_layout, parent, false));
            }
        }

        return null;
    }

    private void onBindArtistViewHolder(ArtistViewHolder holder, int position) {
        Artist artist = content.getArtists().get(position);

        ArrayList<Image> images = artist.getImages();
        if(images.size() > 0)
            Picasso.get()
                    .load(images.get(0).getUrl())
                    .into(holder.thumbnail);

        holder.name.setText(artist.getName());
    }

    private void onBindAlbumViewHolder(AlbumViewHolder holder, int position) {
        Album album = content.getAlbums().get(position);

        ArrayList<Image> images = album.getImages();
        if(images.size() > 0)
            Picasso.get()
                    .load(images.get(0).getUrl())
                    .into(holder.thumbnail);

        holder.name.setText(album.getName());
        holder.artist.setText("By none");
    }

    private void onBindTrackViewHolder(TrackViewHolder holder, int position) {
        Track track = content.getTracks().get(position);

        holder.name.setText(track.getName());
        holder.artist.setText("By none");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int currentPosition = getArtistPosition(position);
        if(currentPosition != -1)
            onBindArtistViewHolder((ArtistViewHolder) holder, position);

        position -= content.getArtists().size();
        currentPosition = getAlbumPosition(position);
        if(currentPosition != -1 && holder instanceof AlbumViewHolder)
            onBindAlbumViewHolder((AlbumViewHolder) holder, position);

        position -= content.getAlbums().size();
        currentPosition = getTrackPosition(position);
        if(currentPosition != -1 && holder instanceof TrackViewHolder)
            onBindTrackViewHolder((TrackViewHolder) holder, position);
    }

    private int getArtistPosition(int position) {
        ArrayList<Artist> artists = content.getArtists();

        if(position < artists.size()) {
            if(position == 0)
                return ViewType.VIEW_ARTIST;
            else return ViewType.VIEW_ARTIST;
        } else return -1;
    }

    private int getAlbumPosition(int position) {
        ArrayList<Album> albums = content.getAlbums();

        if(position < albums.size()) {
            if(position == 0)
                return ViewType.VIEW_ALBUM;
            else return ViewType.VIEW_ALBUM;
        } else return -1;
    }

    private int getTrackPosition(int position) {
        ArrayList<Track> tracks = content.getTracks();

        if(position < tracks.size()) {
            if(position == 0)
                return ViewType.VIEW_TRACK;
            else return ViewType.VIEW_TRACK;
        } else return -1;
    }

    @Override
    public int getItemViewType(int position) {
        int currentPosition = getArtistPosition(position);
        if(currentPosition != -1)
            return currentPosition;

        position -= content.getArtists().size();
        currentPosition = getAlbumPosition(position);
        if(currentPosition != -1)
            return currentPosition;

        position -= content.getAlbums().size();
        currentPosition = getTrackPosition(position);
        if(currentPosition != -1)
            return currentPosition;

        return -1;
    }

    public void swapCollection(Content content) {
        this.content = content;
    }

    @Override
    public int getItemCount() {
        if(content != null)
            return content.getCount();
        else return 0;
    }

    public class TrackViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView artist;

        public TrackViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.track_name);
            artist = itemView.findViewById(R.id.track_artist);
        }
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder {

        private ImageView thumbnail;
        private TextView name;
        private TextView artist;

        public AlbumViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.album_name);
            thumbnail = itemView.findViewById(R.id.album_thumbnail);
            artist = itemView.findViewById(R.id.album_artist);
        }
    }

    public class ArtistViewHolder extends RecyclerView.ViewHolder {

        private ImageView thumbnail;
        private TextView name;

        public ArtistViewHolder(View itemView) {
            super(itemView);

            thumbnail = itemView.findViewById(R.id.artist_thumbnail);
            name = itemView.findViewById(R.id.artist_name);
        }
    }

    public class DividerViewHolder extends RecyclerView.ViewHolder {

        public DividerViewHolder(View itemView) {
            super(itemView);
        }
    }
}
