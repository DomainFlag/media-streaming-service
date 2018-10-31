package com.example.cchiv.jiggles.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.activities.PlayerActivity;
import com.example.cchiv.jiggles.adapters.ContentAdapter;
import com.example.cchiv.jiggles.model.Album;
import com.example.cchiv.jiggles.model.Artist;
import com.example.cchiv.jiggles.model.Image;
import com.example.cchiv.jiggles.utilities.Tools;
import com.squareup.picasso.Picasso;

public class AlbumFragment extends Fragment {

    private static final String TAG = "AlbumFragment";

    private Context context;

    private Album album = null;
    private int position = -1;

    private View rootView = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_layout_album, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.album_list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        ContentAdapter contentAdapter = new ContentAdapter(context, (trackId) -> {
            Intent intent = new Intent(context, PlayerActivity.class);

            intent.putExtra("trackId", trackId);

            startActivity(intent);
        }, album, position);
        recyclerView.setAdapter(contentAdapter);

        updateLayout();

        return rootView;
    }

    private void updateLayout() {
        ImageView thumbnail = rootView.findViewById(R.id.album_thumbnail);
        Image image = album.getArt();
        Picasso
                .get()
                .load(image.getUri())
                .into(thumbnail);

        LinearLayout linearLayout = rootView.findViewById(R.id.album_background);
        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[] {
                        image.getColor(),
                        ContextCompat.getColor(context, R.color.primaryTextColor)
                }
        );

        Tools.setStatusBarColor(context, image.getColor());

        ViewCompat.setBackground(linearLayout, gradientDrawable);

        Artist artist = album.getArtist();
        if(artist != null) {
            ((TextView) rootView.findViewById(R.id.album_genres))
                    .setText(artist.getGenres());

            ((TextView) rootView.findViewById(R.id.album_artist))
                    .setText(artist.getName());
        }

        ((TextView) rootView.findViewById(R.id.album_name))
                .setText(album.getName());
    }

    public void onAttachAlbum(Album album, int position) {
        this.album = album;
        this.position = position;
    }
}
