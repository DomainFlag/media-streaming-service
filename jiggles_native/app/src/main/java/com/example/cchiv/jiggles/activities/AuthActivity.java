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

    private UserAuth userAuth = new UserAuth();
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
        callbackManager.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        ButterKnife.bind(this);

        typeface = Typeface.createFromAsset(getAssets(), "fonts/Brandon_bld.otf");

        if(savedInstanceState != null) {
            userAuth.resolveBundle(savedInstanceState);
        }

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.OTHER_PREFERENCES, MODE_PRIVATE);
        String email = sharedPreferences.getString(Constants.PREFERENCE_EMAIL, null);
        if(email != null) {
            userAuth.email = email;

            editEmailText.setText(email);
        }

        imageFacebookView.setOnClickListener(view -> {
            setFacebookAuth();
        });

        imageSpotifyView.setOnClickListener(view -> {
//            SpotifyConnection spotifyConnection = new SpotifyConnection(this);
//            spotifyConnection.connect();
        });

        changeAuthState();
    }

    public void onClickAction(View view) {
        String name = getEditTextValue(R.id.auth_name_value);
        String email = getEditTextValue(R.id.auth_email_value);
        String password = getEditTextValue(R.id.auth_password_value);

        if(email != null && !email.isEmpty()) {
            CheckBox rememberMe = findViewById(R.id.auth_remember_me);
            if(rememberMe.isChecked()) {
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.OTHER_PREFERENCES, MODE_PRIVATE);
                sharedPreferences.edit()
                        .putString(Constants.PREFERENCE_EMAIL, email)
                        .apply();
            }
        }

        if(email != null && password != null) {
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
                    }, authType, email, password, name);
        } else {
            // Error message to show
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
                        userAuth.social = true;

                        userAuth.email = response.getJSONObject().getString("email");
                        userAuth.firstName = response.getJSONObject().getString("first_name");
                        userAuth.lastName = response.getJSONObject().getString("last_name");

                        Profile profile = Profile.getCurrentProfile();
                        if(profile != null) {
                            userAuth.caption = Profile.getCurrentProfile().getProfilePictureUri(200, 200).toString();
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
        if(userAuth.social) {
            relativeSocialLayout.setVisibility(View.VISIBLE);

            Picasso.get()
                    .load(userAuth.caption)
                    .error(R.drawable.ic_account)
                    .placeholder(R.drawable.ic_account)
                    .into(imageSocialProfileView);

            textSocialGreetingsView.setText(getString(R.string.auth_social_greetings, userAuth.firstName, userAuth.lastName));
            textSocialProfileView.setText(getString(R.string.auth_social_email, userAuth.email));

            linearEmailLayout.setVisibility(View.GONE);
            linearNameLayout.setVisibility(View.GONE);
        } else {
            relativeSocialLayout.setVisibility(View.GONE);

            linearEmailLayout.setVisibility(View.VISIBLE);
            linearNameLayout.setVisibility(View.VISIBLE);
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

        outState.putString(UserAuth.PREF_EMAIL_KEY, userAuth.email);
        outState.putString(UserAuth.PREF_FIRST_NAME_KEY, userAuth.firstName);
        outState.putString(UserAuth.PREF_LAST_NAME_KEY, userAuth.lastName);
        outState.putString(UserAuth.PREF_PASSWORD_KEY, userAuth.password);
        outState.putString(UserAuth.PREF_CAPTION_KEY, userAuth.caption);
        outState.putBoolean(UserAuth.PREF_SOCIAL_KEY, userAuth.social);
    }

    private static class UserAuth {
        private static final String PREF_EMAIL_KEY = "PREF_EMAIL_KEY";
        private static final String PREF_FIRST_NAME_KEY = "PREF_FIRST_NAME_KEY";
        private static final String PREF_LAST_NAME_KEY = "PREF_LAST_NAME_KEY";
        private static final String PREF_PASSWORD_KEY = "PREF_PASSWORD_KEY";
        private static final String PREF_CAPTION_KEY = "PREF_CAPTION_KEY";
        private static final String PREF_SOCIAL_KEY = "PREF_SOCIAL_KEY";

        private String lastName;
        private String firstName;
        private String email;
        private String caption;
        private String password;
        private boolean social;

        private void resolveBundle(Bundle bundle) {
            firstName = bundle.getString(PREF_FIRST_NAME_KEY, null);
            lastName = bundle.getString(PREF_LAST_NAME_KEY, null);
            email = bundle.getString(PREF_EMAIL_KEY, null);
            caption = bundle.getString(PREF_CAPTION_KEY, null);
            password = bundle.getString(PREF_PASSWORD_KEY, null);
            social = bundle.getBoolean(PREF_SOCIAL_KEY, false);
        }
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
