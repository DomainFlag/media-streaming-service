package com.example.cchiv.jiggles.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.model.Album;
import com.example.cchiv.jiggles.model.Artist;
import com.example.cchiv.jiggles.model.Store;
import com.example.cchiv.jiggles.model.Track;
import com.squareup.picasso.Picasso;

public class FlowAdapter extends RecyclerView.Adapter<FlowAdapter.FlowItemViewHolder> {

    private Context context;
    private Store store;

    public FlowAdapter(Context context, Store store) {
        this.context = context;
        this.store = store;
    }

    public void onSwapStore(Store store) {
        this.store = store;
    }

    @NonNull
    @Override
    public FlowItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FlowItemViewHolder(
                LayoutInflater
                        .from(context)
                        .inflate(R.layout.flow_item_layout, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull FlowItemViewHolder holder, int position) {
        Artist artist = store.getArtist(position);
        Album album = artist.getAlbums().get(0);
        Track track = album.getTracks().get(0);

        Picasso.get()
                .load(album.getArt().getUrl())
                .into(holder.thumbnail);

        holder.artist.setText(artist.getName());
        holder.album.setText(album.getName());
        holder.track.setText(track.getName());

        holder.jamming.setText(context.getString(R.string.flow_nearby, 1054));
    }

    @Override
    public int getItemCount() {
        if(store != null)
            return store.getArtists().size();
        else return 0;
    }

    public class FlowItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView thumbnail;
        private TextView artist;
        private TextView album;
        private TextView track;
        private TextView jamming;

        public FlowItemViewHolder(View itemView) {
            super(itemView);

            thumbnail = itemView.findViewById(R.id.flow_thumbnail);
            artist = itemView.findViewById(R.id.flow_item_artist);
            album = itemView.findViewById(R.id.flow_item_album);
            track = itemView.findViewById(R.id.flow_item_track);
            jamming = itemView.findViewById(R.id.flow_item_jamming);
        }
    }
}
