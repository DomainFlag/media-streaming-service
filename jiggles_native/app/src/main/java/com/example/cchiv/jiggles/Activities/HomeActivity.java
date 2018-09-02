package com.example.cchiv.jiggles.Activities;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.cchiv.jiggles.Constants;
import com.example.cchiv.jiggles.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.AUTH_TOKEN, MODE_PRIVATE);
        String token = sharedPreferences.getString(Constants.TOKEN, null);
        if(token != null) {
            TextView textView = findViewById(R.id.home_state);
            textView.setText(token);
        } else {
            finish();
        }
    }
}
