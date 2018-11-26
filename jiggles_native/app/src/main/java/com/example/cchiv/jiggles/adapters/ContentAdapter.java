package com.example.cchiv.jiggles.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.model.Album;
import com.example.cchiv.jiggles.model.Artist;
import com.example.cchiv.jiggles.model.Image;
import com.example.cchiv.jiggles.model.Store;
import com.example.cchiv.jiggles.model.Track;
import com.example.cchiv.jiggles.utilities.Tools;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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

    public static final int MODE_ARTIST = 0;
    public static final int MODE_ALBUM = 1;
    public static final int MODE_TRACK = 2;

    public interface OnItemClickListener {
        void onItemClickListener(String id);
    }

    private static final String TAG = "ContentAdapter";

    private Context context = null;

    private OnItemClickListener onItemClickListener;
    private Store store;
    private int layoutMode = MODE_ALBUM;

    public ContentAdapter(Context context, Store store) {
        this.context = context;
        this.store = store;
    }

    public ContentAdapter(Context context, Store store, int layoutMode, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.store = store;
        this.onItemClickListener = onItemClickListener;
        this.layoutMode = layoutMode;
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
        Artist artist = store.getArtists().get(position);

        List<Image> images = artist.getImages();
        if(images.size() > 0)
            Picasso.get()
                    .load(images.get(0).getUrl())
                    .into(holder.thumbnail);

        holder.name.setText(artist.getName());
    }

    private void onBindAlbumViewHolder(AlbumViewHolder holder, int position) {
        Album album = store.getAlbums().get(position);

        loadAlbumArt(album, holder);

        holder.name.setText(album.getName());

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
            this.onItemClickListener.onItemClickListener(album.getId());
        });
    }

    private void loadAlbumArt(Album album, AlbumViewHolder holder) {
        List<Image> images = album.getImages();

        int color;
        if(images != null && images.size() > 0) {
            color = images.get(0).getColor();

            loadArtAlbum(images.get(0), holder, holder.art, color);
            if(images.size() != 1) {
                loadArtAlbum(images.get(1), holder, holder.artSecondary, null);

                holder.artSecondary.setVisibility(View.VISIBLE);
            } else {
                holder.artSecondary.setVisibility(View.INVISIBLE);
            }
        } else {
            holder.art.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_artwork_placeholder));

            color = ContextCompat.getColor(context, R.color.motionPrimaryDarkColor);
        }

        if(color != 0)
            Tools.setGradientBackground(context, holder.itemView, color, 255);
    }

    private void loadArtAlbum(Image image, AlbumViewHolder holder, ImageView imageView, Integer color) {
        Bitmap bitmap = image.getBitmap();
        if(bitmap == null) {
            Picasso.get()
                    .load(image.getUrl())
                    .placeholder(R.drawable.ic_artwork_placeholder)
                    .error(R.drawable.ic_artwork_placeholder)
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            imageView.setImageBitmap(bitmap);

                            if(color != null && color == 0) {
                                int paletteColor = Tools.getPaletteColor(context, bitmap);

                                Tools.setGradientBackground(context, holder.itemView, paletteColor, 255);
                            }
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            imageView.setImageDrawable(errorDrawable);
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                            imageView.setImageDrawable(placeHolderDrawable);
                        }
                    });
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

    private void onBindTrackViewHolder(TrackViewHolder holder, int position) {
        Track track = store.getTrack(position);

        holder.name.setText(track.getName());

        Album album = track.getAlbum();
        if(album != null) {
            Artist artist = album.getArtist();
            if(artist != null) {
                holder.artist.setText(artist.getName());
            } else holder.artist.setText(track.getName());
        } else holder.artist.setText(track.getName());

        holder.itemView.setOnClickListener((view) -> {
            onItemClickListener.onItemClickListener(track.getId());
        });
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch(layoutMode) {
            case MODE_ARTIST : {
                ArtistViewHolder artistViewHolder = (ArtistViewHolder) holder;

                onBindArtistViewHolder(artistViewHolder, position);
                break;
            }
            case MODE_ALBUM : {
                AlbumViewHolder albumViewHolder = (AlbumViewHolder) holder;

                onBindAlbumViewHolder(albumViewHolder, position);
                break;
            }
            case MODE_TRACK : {
                TrackViewHolder trackViewHolder = (TrackViewHolder) holder;

                onBindTrackViewHolder(trackViewHolder, position);
                break;
            }
        }
    }


    @Override
    public int getItemViewType(int position) {
        switch(layoutMode) {
            case MODE_ARTIST : return ViewType.VIEW_ARTIST;
            case MODE_ALBUM : return ViewType.VIEW_ALBUM;
            case MODE_TRACK : return ViewType.VIEW_TRACK;
        }

        return -1;
    }

    public void swapCollection(Store store) {
        this.store = store;
    }

    public void swapLayoutMode(int layoutMode) {
        this.layoutMode = layoutMode;
    }

    @Override
    public int getItemCount() {
        if(store == null)
            return 0;

        switch(layoutMode) {
            case MODE_ARTIST : return store.getArtists().size();
            case MODE_ALBUM : return store.getAlbums().size();
            case MODE_TRACK : return store.getTracks().size();
        }

        return 0;
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
