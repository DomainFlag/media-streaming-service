package com.example.cchiv.jiggles.activities;

import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.utilities.JigglesConnection;
import com.example.cchiv.jiggles.utilities.PlayerUtilities;
import com.example.cchiv.jiggles.utilities.VisualizerView;
import com.google.android.exoplayer2.ui.PlayerView;

import java.io.IOException;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    private final static String TAG = "PlayerActivity";

    private boolean utilitiesToggle = false;

    private JigglesConnection jigglesConnection = null;
    private PlayerUtilities playerUtilities = null;

    private PlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        playerView = findViewById(R.id.player);

        VisualizerView visualizerView = findViewById(R.id.player_visualizer);
        playerUtilities = new PlayerUtilities(this, visualizerView);

        updateUserInterface();
        scan();
    }

    public void updateUserInterface(){
        jigglesConnection = new JigglesConnection(this, (message, type, size, data) -> {
            // Do something with the data regardless the reading or writing state
            switch(type) {
                case JigglesConnection.MessageConstants.MESSAGE_READ : {
                    break;
                }
                case JigglesConnection.MessageConstants.MESSAGE_WRITE : {
                    break;
                }
                case JigglesConnection.MessageConstants.MESSAGE_TOAST : {
                    Log.v(TAG, message);
                    break;
                }
            }
        });

        findViewById(R.id.player_share).setOnClickListener((view) -> {
            jigglesConnection = new JigglesConnection(this, (message, type, size, data) -> {
                // Do something with the data regardless the reading or writing state
                switch(type) {
                    case JigglesConnection.MessageConstants.MESSAGE_READ : {
                        break;
                    }
                    case JigglesConnection.MessageConstants.MESSAGE_WRITE : {
                        break;
                    }
                    case JigglesConnection.MessageConstants.MESSAGE_TOAST : {
                        Log.v(TAG, message);
                        break;
                    }
                }
            });

            jigglesConnection.discover();
            jigglesConnection.syncAudioData();
        });

        findViewById(R.id.player_lyrics).setOnClickListener((view) -> {
            // Do something later with lyrics
        });

        findViewById(R.id.player_back).setOnClickListener((view) -> {
            finish();
        });

        View utilities = findViewById(R.id.player_utilities);
        ImageView menu = findViewById(R.id.player_menu);
        menu.setOnClickListener((view) -> {
            if(utilitiesToggle) {
                menu.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.pleasureColor)));
                utilities.setVisibility(View.GONE);
            } else {
                menu.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.iconsTextColor)));
                utilities.setVisibility(View.VISIBLE);
            }

            utilitiesToggle = !utilitiesToggle;
        });

        BitmapDrawable bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.background);
        Palette palette = Palette.from(bitmapDrawable.getBitmap())
                .generate();

        TextView textArtistView = (TextView) findViewById(R.id.player_artist);
        textArtistView.setText("by The xx");

        TextView textTrackView = (TextView) findViewById(R.id.player_track);
        textTrackView.setText("Intro");

        int defaultColor = ContextCompat.getColor(this, R.color.iconsTextColor);
        int lightVibrantColor = palette.getDarkVibrantColor(defaultColor);
        int color = ContextCompat.getColor(this, R.color.primaryTextColor);

        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] {
                        lightVibrantColor,
                        color
                }
        );

        View view = findViewById(R.id.player_background);
        ViewCompat.setBackground(view, gradientDrawable);

        playerView.setShutterBackgroundColor(ContextCompat.getColor(this, R.color.visualizerClearColor));
    }

    public void setPlayer(String fileName) {
        playerView.setControllerShowTimeoutMs(-1);
        playerUtilities.setUpPlayer(playerView, fileName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(TAG, String.valueOf(resultCode));
        if(jigglesConnection != null)
            jigglesConnection.onSearchPairedDevices(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(jigglesConnection != null)
            jigglesConnection.unregisterReceiver();

        if(playerUtilities != null)
            playerUtilities.release();
    }

    @Override
    protected void onPause() {
        super.onPause();

        togglePlayer(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        togglePlayer(true);
    }

    public void togglePlayer(boolean playbackState) {
        if(playerUtilities != null)
            playerUtilities.togglePlayback(playbackState);
    }

    public ArrayList<String> scan() {
        AssetManager assetManager = getAssets();
        try {
            String[] files = assetManager.list("samples");
            for(String file : files) {
                setPlayer(file);
            }
        } catch(IOException e) {
            Log.v(TAG, e.toString());
        }

        return null;
    }
}
