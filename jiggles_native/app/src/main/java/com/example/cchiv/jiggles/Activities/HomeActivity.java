package com.example.cchiv.jiggles.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.cchiv.jiggles.Adapters.ReleaseAdapter;
import com.example.cchiv.jiggles.Constants;
import com.example.cchiv.jiggles.Model.News;
import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.Utilities.NetworkUtilities;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    private NetworkUtilities networkUtilities;

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

        networkUtilities = new NetworkUtilities();

        fetchNews();
        fetchReleases();
    }

    public void fetchNews() {
        networkUtilities.fetchNews(new NetworkUtilities.FetchNews.OnPostNetworkCallback() {
            @Override
            public void onPostNetworkCallback(ArrayList<News> news) {
                if(news != null) {
                    // Do something with News
                }
            }
        });
    }

    public void fetchReleases() {
        networkUtilities.fetchReleases(releases -> {
            // Do something with releases
            if(releases != null) {
                // Do something with Releases
                RecyclerView recyclerView = findViewById(R.id.home_list);
                recyclerView.setHasFixedSize(true);

                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,
                        LinearLayoutManager.HORIZONTAL,
                        false);
                recyclerView.setLayoutManager(layoutManager);

                ReleaseAdapter releaseAdapter = new ReleaseAdapter(this, releases);
                recyclerView.setAdapter(releaseAdapter);
            }
        });
    }
}
