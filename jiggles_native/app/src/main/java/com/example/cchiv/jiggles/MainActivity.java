package com.example.cchiv.jiggles;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

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

    public void onGetStartedClick(View view) {
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
    }
}
