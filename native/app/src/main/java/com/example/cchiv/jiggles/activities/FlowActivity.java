package com.example.cchiv.jiggles.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.adapters.FlowAdapter;
import com.example.cchiv.jiggles.data.ContentContract;
import com.example.cchiv.jiggles.model.player.Store;
import com.example.cchiv.jiggles.utilities.JigglesLoader;

public class FlowActivity extends PlayerAppCompatActivity {

    private FlowAdapter flowAdapter;

    @Override
    protected void onCreateActivity(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_flow);

        RecyclerView recyclerView = findViewById(R.id.flow_list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        flowAdapter = new FlowAdapter(this, null);
        recyclerView.setAdapter(flowAdapter);

        LoaderManager loaderManager = getSupportLoaderManager();

        Bundle loaderBundle = new Bundle();
        loaderBundle.putString(JigglesLoader.BUNDLE_URI_KEY, ContentContract.CONTENT_COLLECTION_URI.toString());

        JigglesLoader jigglesLoader = new JigglesLoader<>(this,
                (JigglesLoader.OnPostLoaderCallback<Store>) this::updateRecyclerView,
                Store::parseCursor
        );

        loaderManager.initLoader(154124, loaderBundle, jigglesLoader).forceLoad();

        createAnimatedDrawable();
    }

    public void updateRecyclerView(Store store) {
        flowAdapter.onSwapStore(store);
        flowAdapter.notifyDataSetChanged();
    }

    private void createAnimatedDrawable() {
        ImageView imageView = findViewById(R.id.flow_image);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageView, View.ROTATION, 0.0f, 360.0f);

        objectAnimator.setDuration(4000);
        objectAnimator.setRepeatCount(Animation.INFINITE);
        objectAnimator.setInterpolator(new LinearInterpolator());

        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                imageView.animate()
                        .cancel();
            }
        });

        objectAnimator.start();

        imageView.setOnClickListener((v) -> {
            if(objectAnimator.isPaused())
                objectAnimator.resume();
            else objectAnimator.pause();
        });
    }
}
