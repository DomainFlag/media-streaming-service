package com.example.cchiv.jiggles.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cchiv.jiggles.Constants;
import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.model.Album;
import com.example.cchiv.jiggles.model.Artist;
import com.example.cchiv.jiggles.model.Collection;
import com.example.cchiv.jiggles.model.Image;
import com.example.cchiv.jiggles.model.Track;
import com.squareup.picasso.Picasso;

import java.util.List;

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

    public interface OnClickCallbackListener {
        void onClickCallbackListener(String trackId);
    }

    public interface OnClickAlbumListener {
        void onClickAlbumListener(Album album, int position);
    }

    private static final String TAG = "ContentAdapter";

    private OnClickCallbackListener onClickCallbackListener;
    private OnClickAlbumListener onClickAlbumListener;

    private Context context = null;

    private Collection collection;

    private Album album = null;
    private int albumIndex = 0;

    public ContentAdapter(Context context, Collection collection) {
        this.context = context;
        this.collection = collection;
    }

    public ContentAdapter(Context context, OnClickCallbackListener onClickCallbackListener, Album album, int albumIndex) {
        this.context = context;
        this.album = album;
        this.albumIndex = albumIndex;
        this.onClickCallbackListener = onClickCallbackListener;
    }

    public ContentAdapter(Context context, OnClickAlbumListener onClickAlbumListener, Collection collection) {
        this.context = context;
        this.collection = collection;
        this.onClickAlbumListener = onClickAlbumListener;
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
        Artist artist = collection.getArtists().get(position);

        List<Image> images = artist.getImages();
        if(images.size() > 0)
            Picasso.get()
                    .load(images.get(0).getUri())
                    .into(holder.thumbnail);

        holder.name.setText(artist.getName());
    }

    private void onBindAlbumViewHolder(AlbumViewHolder holder, int position) {
        Album album = collection.getAlbums().get(position);

        holder.name.setText(album.getName());

        int leftColor = ContextCompat.getColor(context, R.color.primaryTextColor);
        int rightColor = onLoadAlbumArt(album, holder);

        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[] { leftColor, rightColor });

        ViewCompat.setBackground(holder.itemView, gradientDrawable);

        Artist artist = album.getArtist();
        if(artist != null) {
            String name = artist.getName();
            if(name != null)
                holder.artist.setText(name);

            String genre = artist.getGenres();
            if(genre != null)
                holder.genres.setText(genre);
        }

        holder.itemView.setOnClickListener((view) -> {
            this.onClickAlbumListener.onClickAlbumListener(album, position);
        });
    }

    private int onLoadAlbumArt(Album album, AlbumViewHolder holder) {
        List<Image> images = album.getImages();
        if(images != null && images.size() > 0) {
            if(images.size() > 1) {
                loadArtAlbum(images.get(1), holder.artSecondary);

                holder.artSecondary.setVisibility(View.VISIBLE);
            } else holder.artSecondary.setVisibility(View.INVISIBLE);

            loadArtAlbum(images.get(0), holder.art);

            return images.get(0).getColor();
        } else {
            holder.art.setVisibility(View.GONE);

            return ContextCompat.getColor(context, R.color.iconsTextColor);
        }
    }

    private void loadArtAlbum(Image image, ImageView imageView) {
        Bitmap bitmap = image.getBitmap();
        if(bitmap == null) {
            Picasso.get()
                    .load(image.getUri())
                    .into(imageView);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

    private void onBindTrackViewHolder(TrackViewHolder holder, int position) {
        Track track;
        if(this.album != null)
            track = this.album.getTracks().get(position);
        else track = collection.getTracks().get(position);

        holder.name.setText(track.getName());

        Album album = track.getAlbum();
        if(album != null) {
            Artist artist = album.getArtist();
            if(artist != null) {
                holder.artist.setText(artist.getName());
            } else holder.artist.setText(track.getName());
        } else holder.artist.setText(track.getName());

        holder.itemView.setOnClickListener((view) -> {
            onClickCallbackListener.onClickCallbackListener(track.getId());
        });
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(album != null) {
            onBindTrackViewHolder((TrackViewHolder) holder, position);
            return;
        }

        int currentPosition;

        currentPosition = getArtistPosition(position);
        if(currentPosition != -1)
            onBindArtistViewHolder((ArtistViewHolder) holder, position);

        currentPosition = getAlbumPosition(position);
        if(currentPosition != -1 && holder instanceof AlbumViewHolder)
            onBindAlbumViewHolder((AlbumViewHolder) holder, position);

        currentPosition = getTrackPosition(position);
        if(currentPosition != -1 && holder instanceof TrackViewHolder)
            onBindTrackViewHolder((TrackViewHolder) holder, position);
    }

    private int getArtistPosition(int position) {
        if(!collection.getFilterBy().equals(Constants.ARTIST))
            return -1;

        List<Artist> artists = collection.getArtists();

        if(position < artists.size()) {
            if(position == 0)
                return ViewType.VIEW_ARTIST;
            else return ViewType.VIEW_ARTIST;
        } else return -1;
    }

    private int getAlbumPosition(int position) {
        if(!collection.getFilterBy().equals(Constants.ALBUM))
            return -1;

        List<Album> albums = collection.getAlbums();

        if(position < albums.size()) {
            if(position == 0)
                return ViewType.VIEW_ALBUM;
            else return ViewType.VIEW_ALBUM;
        } else return -1;
    }

    private int getTrackPosition(int position) {
        if(!collection.getFilterBy().equals(Constants.TRACK))
            return -1;

        List<Track> tracks = collection.getTracks();

        if(position < tracks.size()) {
            if(position == 0)
                return ViewType.VIEW_TRACK;
            else return ViewType.VIEW_TRACK;
        } else return -1;
    }

    @Override
    public int getItemViewType(int position) {
        if(album != null)
            return ViewType.VIEW_TRACK;

        int currentPosition = getArtistPosition(position);
        if(currentPosition != -1)
            return currentPosition;

        position -= collection.getArtists().size();
        currentPosition = getAlbumPosition(position);
        if(currentPosition != -1)
            return currentPosition;

        position -= collection.getAlbums().size();
        currentPosition = getTrackPosition(position);
        if(currentPosition != -1)
            return currentPosition;

        return -1;
    }

    public void swapCollection(Collection collection) {
        this.collection = collection;
    }

    @Override
    public int getItemCount() {
        if(album != null)
            return album.getTracks().size();

        if(collection != null)
            return collection.getCount();
        else return 0;
    }

    public class TrackViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView artist;

        public TrackViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.album_name);
            artist = itemView.findViewById(R.id.album_artist);
        }
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView artist;
        private TextView genres;
        private ImageView art;
        private ImageView artSecondary;

        public AlbumViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.album_name);
            artist = itemView.findViewById(R.id.album_artist);
            genres = itemView.findViewById(R.id.album_genres);
            art = itemView.findViewById(R.id.album_art);
            artSecondary = itemView.findViewById(R.id.album_art_secondary);
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
