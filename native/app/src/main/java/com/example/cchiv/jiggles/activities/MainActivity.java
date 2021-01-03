package com.example.cchiv.jiggles.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.cchiv.jiggles.Constants;
import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.utilities.Tools;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView welcomeBackground = findViewById(R.id.welcome_background);
        Picasso.get()
                .load(R.drawable.background)
                .resize(600, 600)
                .into(welcomeBackground);
    }

    public void onClickGetStarted(View view) {
        String authType = null;
        switch(view.getId()) {
            case R.id.get_started : {
                authType = Constants.AUTH_SIGN_UP;
                break;
            }
            case R.id.welcome_sign_in : {
                authType = Constants.AUTH_SIGN_IN;
                break;
            }
            case R.id.welcome_sign_up : {
                authType = Constants.AUTH_SIGN_UP;
                break;
            }
            default : {
                Log.v("MainActivity", "Unknown command");
            }
        }

        if(authType != null) {
            Intent intent;

            if(view.getId() == R.id.get_started) {
                String token = Tools.getToken(this);
                if(token != null) {
                    intent = new Intent(this, HomeActivity.class);
                } else {
                    intent = new Intent(this, AuthActivity.class);
                    intent.putExtra(Constants.AUTH_TYPE_KEY, Constants.AUTH_SIGN_IN);
                }
            } else {
                intent = new Intent(this, AuthActivity.class);
                intent.putExtra(Constants.AUTH_TYPE_KEY, authType);
            }

            startActivity(intent);
        }
    }
}
