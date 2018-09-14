package com.example.cchiv.jiggles.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.cchiv.jiggles.Adapters.ThreadAdapter;
import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.Utilities.NetworkUtilities;

public class ForumActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ThreadAdapter threadAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        recyclerView = findViewById(R.id.social_list);
        recyclerView.setNestedScrollingEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        threadAdapter = new ThreadAdapter(this, null);
        recyclerView.setAdapter(threadAdapter);

        NetworkUtilities networkUtilities = new NetworkUtilities();
        networkUtilities.fetchThreads((threads) -> {
            threadAdapter.swapContent(threads);
            threadAdapter.notifyDataSetChanged();
        });
    }
}
