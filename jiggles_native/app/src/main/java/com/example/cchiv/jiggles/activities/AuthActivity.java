package com.example.cchiv.jiggles.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.cchiv.jiggles.Constants;
import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.model.User;
import com.example.cchiv.jiggles.utilities.NetworkUtilities;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AuthActivity extends AppCompatActivity {

    private static final String TAG = "AuthActivity";

    private User user = new User();

    private CallbackManager callbackManager;
    private Typeface typeface;

    private String authType = null;

    @BindView(R.id.auth_facebook) ImageView imageFacebookView;
    @BindView(R.id.auth_spotify) ImageView imageSpotifyView;
    @BindView(R.id.auth_social_layout) RelativeLayout relativeSocialLayout;
    @BindView(R.id.auth_social_profile_picture) ImageView imageSocialProfileView;
    @BindView(R.id.auth_social_greetings) TextView textSocialGreetingsView;
    @BindView(R.id.auth_social_profile_email) TextView textSocialProfileView;

    @BindView(R.id.auth_social_header) TextView textSocialHeaderView;
    @BindView(R.id.auth_error) TextView textErrorView;

    @BindView(R.id.auth_email_container) LinearLayout linearEmailLayout;
    @BindView(R.id.auth_email_value) EditText editEmailText;

    @BindView(R.id.auth_name_container) LinearLayout linearNameLayout;
    @BindView(R.id.auth_name_value) EditText editNameText;

    @BindView(R.id.auth_password_container) LinearLayout linearPasswordLayout;
    @BindView(R.id.auth_password_value) EditText editPasswordText;

    @BindView(R.id.auth_password_repeat_container) LinearLayout linearPasswordRepeatLayout;
    @BindView(R.id.auth_password_repeat_value) EditText editPasswordRepeatText;

    @BindView(R.id.auth_remember_me) CheckBox checkRememberMeBox;
    @BindView(R.id.auth_action) Button buttonActionView;
    @BindView(R.id.auth_redirection) TextView textRedirectionView;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        ButterKnife.bind(this);

        typeface = Typeface.createFromAsset(getAssets(), "fonts/Brandon_bld.otf");

        if(savedInstanceState != null) {
            user.resolveDecodeBundle(savedInstanceState);
        }

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.OTHER_PREFERENCES, MODE_PRIVATE);
        String email = sharedPreferences.getString(Constants.PREFERENCE_EMAIL, null);
        if(email != null) {
            user.setName(email);

            editEmailText.setText(email);
        }

        imageFacebookView.setOnClickListener(view -> {
            setFacebookAuth();
        });

        imageSpotifyView.setOnClickListener(view -> {
//            SpotifyPlayer spotifyPlayer = new SpotifyPlayer(this);
//            spotifyPlayer.connect();
        });

        changeAuthState();
    }

    public void onClickAction(View view) {
        if(user == null) {
            user = new User();
        }

        if(!user.social) {
            if(user.getEmail() == null || user.getEmail().isEmpty())
                user.setEmail(editEmailText.getText().toString());

            if(user.getName() == null || user.getName().isEmpty()) {
                user.setName(editNameText.getText().toString());
            }
        }

        user.setPassword(getEditTextValue(R.id.auth_password_value));

        if(user.getEmail() != null && !user.getEmail().isEmpty()) {
            CheckBox rememberMe = findViewById(R.id.auth_remember_me);
            if(rememberMe.isChecked()) {
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.OTHER_PREFERENCES, MODE_PRIVATE);
                sharedPreferences.edit()
                        .putString(Constants.PREFERENCE_EMAIL, user.getEmail())
                        .apply();
            }
        }

        if(user.getEmail() != null && user.getPassword() != null) {
            NetworkUtilities.ResolveAuth resolveAuth = new NetworkUtilities.ResolveAuth(token -> {
                        if(token != null && !token.isEmpty()) {
                            SharedPreferences sharedPreferences = getSharedPreferences(Constants.AUTH_TOKEN, MODE_PRIVATE);
                            sharedPreferences.edit()
                                    .putString(Constants.TOKEN, token)
                                    .apply();

                            Intent intent = new Intent(this, HomeActivity.class);
                            startActivity(intent);
                        } else {
                            textErrorView.setText(getString(R.string.auth_error_auth_resolved));
                        }
                    }, authType, user);
        } else {
            // Error message to show
            textErrorView.setText(getString(R.string.auth_error, "Please enter a valid email or password"));
        }
    }

    public String getEditTextValue(int identifier) {
        View view = findViewById(identifier);
        if(view instanceof EditText) {
            EditText editText = (EditText) view;

            return editText.getEditableText().toString();
        }

        return null;
    }

    private void setFacebookAuth() {
        callbackManager = CallbackManager.Factory.create();

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken != null) {
            getFacebookCredentials(accessToken);
        } else {
            LoginManager loginManager = LoginManager.getInstance();

            loginManager.logInWithReadPermissions(this,
                    Collections.singletonList("public_profile, email"));

            loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    AccessToken accessToken = loginResult.getAccessToken();

                    if(accessToken != null)
                        getFacebookCredentials(accessToken);
                }

                @Override
                public void onCancel() {
                    textErrorView.setVisibility(View.VISIBLE);
                    textErrorView.setText(getString(R.string.auth_error_cancel));
                }

                @Override
                public void onError(FacebookException exception) {
                    textErrorView.setVisibility(View.VISIBLE);
                    textErrorView.setText(getString(R.string.auth_error, exception.toString()));
                }
            });
        }
    }

    private void getFacebookCredentials(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken, (object, response) -> {
                    try {
                        user.social = true;

                        user.setEmail(response.getJSONObject().getString("email"));
                        user.setName(
                                response.getJSONObject().getString("first_name") + " "
                                        + response.getJSONObject().getString("last_name")
                        );

                        Profile profile = Profile.getCurrentProfile();
                        if(profile != null) {
                            user.setCaption(Profile.getCurrentProfile().getProfilePictureUri(200, 200).toString());
                        }

                        changeSocialState();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, email, first_name, last_name");

        request.setParameters(parameters);
        request.executeAsync();
    }

    private void changeSocialState() {
        if(user.social) {
            relativeSocialLayout.setVisibility(View.VISIBLE);

            Picasso.get()
                    .load(user.getCaption())
                    .error(R.drawable.ic_account)
                    .placeholder(R.drawable.ic_account)
                    .into(imageSocialProfileView);

            String[] names = user.getNames();
            textSocialGreetingsView.setText(getString(R.string.auth_social_greetings, names[0], names[1]));
            textSocialProfileView.setText(getString(R.string.auth_social_email, user.getEmail()));

            linearEmailLayout.setVisibility(View.GONE);
            linearNameLayout.setVisibility(View.GONE);
        } else {
            relativeSocialLayout.setVisibility(View.GONE);

            linearEmailLayout.setVisibility(View.VISIBLE);

            Intent intent = getIntent();
            authType = intent.getStringExtra(Constants.AUTH_TYPE_KEY);
            if(authType != null && authType.equals(Constants.AUTH_SIGN_UP)) {
                linearNameLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void changeAuthState() {
        Intent intent = getIntent();
        authType = intent.getStringExtra(Constants.AUTH_TYPE_KEY);

        if(authType != null) {
            String authTypeText = null;

            RedirectionText authRedirectionText = null;
            switch(authType) {
                case Constants.AUTH_SIGN_IN : {
                    authTypeText = getString(R.string.auth_social_header_log_in);

                    authRedirectionText = new RedirectionText(
                            getString(R.string.auth_redirection_new_user),
                            getString(R.string.auth_redirection_sign_up)
                    );

                    linearNameLayout.setVisibility(View.GONE);
                    linearPasswordRepeatLayout.setVisibility(View.GONE);
                    break;
                }
                case Constants.AUTH_SIGN_UP : {
                    authTypeText = getString(R.string.auth_social_header_sing_up);

                    authRedirectionText = new RedirectionText(
                            getString(R.string.auth_redirection_old_user),
                            getString(R.string.auth_redirection_log_in)
                    );

                    break;
                }
                default : {
                    Log.v("AuthActivity", "Undefined Command");

                    finish();
                }
            }

            if(authTypeText != null) {
                textSocialHeaderView.setText(getString(R.string.auth_social_header, authTypeText));
                buttonActionView.setText(authTypeText);
            }

            if(authRedirectionText != null) {
                SpannableString spannableString = new SpannableString(
                        String.format(getString(R.string.auth_redirection_text),
                                authRedirectionText.getPrimaryText(),
                                authRedirectionText.getSecondaryText())
                );

                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                spannableString.setSpan(foregroundColorSpan,
                        spannableString.length() - authRedirectionText.getSecondaryText().length(),
                        spannableString.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                textRedirectionView.setText(spannableString);
                textRedirectionView.setTypeface(typeface);
            }
        }

        changeSocialState();
    }

    public void onClickRedirect(View view) {
        Intent intent = getIntent();

        authType = intent.getStringExtra(Constants.AUTH_TYPE_KEY);
        if(authType.equals(Constants.AUTH_SIGN_IN))
            intent.putExtra(Constants.AUTH_TYPE_KEY, Constants.AUTH_SIGN_UP);
        else intent.putExtra(Constants.AUTH_TYPE_KEY, Constants.AUTH_SIGN_IN);

        recreate();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        user.resolveEncodeBundle(outState);
    }

    public class RedirectionText {
        private String primaryText;
        private String secondaryText;

        RedirectionText(String primaryText, String secondaryText) {
            this.primaryText = primaryText;
            this.secondaryText = secondaryText;
        }

        private String getPrimaryText() {
            return primaryText;
        }

        private String getSecondaryText() {
            return secondaryText;
        }
    }
}
