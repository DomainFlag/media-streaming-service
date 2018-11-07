package com.example.cchiv.jiggles.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.model.Track;
import com.example.cchiv.jiggles.services.PlayerService;
import com.example.cchiv.jiggles.services.PlayerServiceConnection;

public abstract class PlayerAppCompatActivity extends AppCompatActivity implements
        PlayerService.OnCallbackListener, PlayerServiceConnection.OnCallbackConnectionComplete {

    public PlayerServiceConnection playerServiceConnection = null;

    public View barPlayerLayout;
    public TextView barPlayerTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreateActivity(savedInstanceState);

        barPlayerLayout = findViewById(R.id.home_player);
        barPlayerTitle = findViewById(R.id.home_player_title);

        playerServiceConnection = new PlayerServiceConnection(this);
        playerServiceConnection.onStartService(null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        onChangeCurrentTrack();
    }

    public void onChangeCurrentTrack() {
        Track track = playerServiceConnection.getCurrentTrack();
        if(track != null) {
            updatePlayerBar(track);
        }
    }

    private void updatePlayerBar(Track track) {
        barPlayerLayout.setVisibility(View.VISIBLE);
        barPlayerTitle.setText(getString(R.string.home_bar_title, track.getArtist().getName(), track.getName()));
    }

    protected abstract void onCreateActivity(@Nullable Bundle savedInstanceState);

    protected abstract void onDestroyActivity();

    @Override
    public void onCallbackListener(Track track) {
        updatePlayerBar(track);
    }

    @Override
    public void onCallbackConnectionComplete() {
        onChangeCurrentTrack();
    }
}
