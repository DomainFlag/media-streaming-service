package com.example.cchiv.jiggles.spotify;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.cchiv.jiggles.Constants;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

public class SpotifyConnection {

    private static final String TAG = "SpotifyConnection";

    private static SpotifyAppRemote spotifyAppRemote;

    private Context context;

    public SpotifyConnection(Context context) {
        this.context = context;
    }

    public void connect() {
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(Constants.CLIENT_ID)
                        .setRedirectUri(Constants.REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(context, connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        SpotifyConnection.spotifyAppRemote = spotifyAppRemote;
                        Log.d(TAG, "Connected");

                        // Now you can start interacting with App Remote
                        connected();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e(TAG, throwable.getMessage(), throwable);
                    }
                });
    }


    private void connected() {
        spotifyAppRemote.getPlayerApi().play("spotify:user:spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");

        // Subscribe to PlayerState
        spotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(new Subscription.EventCallback<PlayerState>() {
                    public void onEvent(PlayerState playerState) {
                        Track track = playerState.track;
                        if(track != null) {
                            Log.v(TAG, track.name + " by " + track.artist.name);
                        }
                    }
                });
    }

    public static class RemoteSpotify {

        private static final String TAG = "RemoteSpotify";

        private Context context;

        private static final int REQUEST_CODE = 1337;
        private static final String REDIRECT_URI = "yourcustomprotocol://callback";

        private AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(Constants.CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

        public RemoteSpotify(Context context) {
            this.context = context;

            builder.setScopes(new String[] { "streaming" });
            AuthenticationRequest request = builder.build();
            AuthenticationClient.openLoginActivity((Activity) context, REQUEST_CODE, request);
        }

        public void onActivityResult(int requestCode, int resultCode, Intent intent) {
            // Check if result comes from the correct activity
            if(requestCode == REQUEST_CODE) {
                AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

                switch(response.getType()) {
                    // Response was successful and contains auth token
                    case TOKEN:
                        response.getAccessToken();
                        // Handle successful response

                        break;

                    // Auth flow returned an error
                    case ERROR:
                        // Handle error response
                        break;

                    // Most likely auth flow was cancelled
                    default:
                        // Handle other cases
                }
            }
        }
    }
}
