package com.example.cchiv.jiggles.Activities;

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
import android.widget.EditText;
import android.widget.TextView;

import com.example.cchiv.jiggles.Constants;
import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.Utilities.NetworkUtilities;

import okhttp3.Response;

public class AuthActivity extends AppCompatActivity implements NetworkUtilities.OnPostNetworkCallback {

    private Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        typeface = Typeface.createFromAsset(getAssets(), "fonts/Brandon_bld.otf");

        changeAuthState();
    }

    @Override
    public void onPostNetworkCallback(Response response) {
        String token = response.header("X-Auth");
        if(token != null && !token.isEmpty()) {
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.AUTH_TOKEN, MODE_PRIVATE);
            sharedPreferences.edit()
                    .putString(Constants.TOKEN, token)
                    .apply();

            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        } else {
            // Error message to show
        }
    }

    public void onClickAction(View view) {
        String email = getEditTextValue(R.id.auth_email_value);
        String password = getEditTextValue(R.id.auth_password_value);

        if(email != null && password != null) {
            NetworkUtilities networkUtilities = new NetworkUtilities(this);
            networkUtilities.userAuthLogging(email, password);
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

    private void changeAuthState() {
        Intent intent = getIntent();
        String authType = intent.getStringExtra(Constants.AUTH_TYPE_KEY);

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

                    findViewById(R.id.auth_name_container).setVisibility(View.GONE);
                    findViewById(R.id.auth_password_repeat_container).setVisibility(View.GONE);
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
                TextView authSocialTextView = findViewById(R.id.auth_social_header);
                authSocialTextView.setText(getString(R.string.auth_social_header, authTypeText));

                Button button = findViewById(R.id.auth_action);
                button.setText(authTypeText);
            }

            if(authRedirectionText != null) {
                TextView authRedirectionTextView = findViewById(R.id.auth_redirection);

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

                authRedirectionTextView.setText(spannableString);
                authRedirectionTextView.setTypeface(typeface);
            }
        }
    }

    public void onClickRedirect(View view) {
        Intent intent = getIntent();

        String authType = intent.getStringExtra(Constants.AUTH_TYPE_KEY);
        if(authType.equals(Constants.AUTH_SIGN_IN))
            intent.putExtra(Constants.AUTH_TYPE_KEY, Constants.AUTH_SIGN_UP);
        else intent.putExtra(Constants.AUTH_TYPE_KEY, Constants.AUTH_SIGN_IN);

        recreate();
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
