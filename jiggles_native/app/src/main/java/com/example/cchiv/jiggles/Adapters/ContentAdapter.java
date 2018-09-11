package com.example.cchiv.jiggles.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cchiv.jiggles.Model.Album;
import com.example.cchiv.jiggles.Model.Artist;
import com.example.cchiv.jiggles.Model.Track;
import com.example.cchiv.jiggles.R;

import java.util.ArrayList;

public class ContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;

    private ArrayList<Track> tracks = new ArrayList<>();
    private ArrayList<Album> albums = new ArrayList<>();
    private ArrayList<Artist> artists = new ArrayList<>();

    public ContentAdapter(Context context, ArrayList<?> content) {
        this.context = context;

        if(!content.isEmpty()) {
            Object object = content.get(0);

            if(object instanceof Track)
                this.tracks = (ArrayList<Track>) content;
            else if(object instanceof Artist)
                this.artists = (ArrayList<Artist>) content;
            else if(object instanceof Album)
                this.albums = (ArrayList<Album>) content;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TrackViewHolder(
                LayoutInflater
                        .from(context)
                        .inflate(R.layout.news_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return artists.size() + albums.size() + tracks.size();
    }

    public class TrackViewHolder extends RecyclerView.ViewHolder {

        public TrackViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder {

        public AlbumViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class ArtistViewHolder extends RecyclerView.ViewHolder {

        public ArtistViewHolder(View itemView) {
            super(itemView);
        }
    }
}
