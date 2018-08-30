package com.example.cchiv.jiggles;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        TextView authSocialTextView = findViewById(R.id.auth_social_header);
        authSocialTextView.setText(getString(R.string.auth_social_header, "log in"));
    }
}
