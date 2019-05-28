package com.example.cchiv.jiggles.adapters;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.interfaces.RemoteMediaCallback;
import com.example.cchiv.jiggles.model.Release;
import com.example.cchiv.jiggles.model.Review;
import com.example.cchiv.jiggles.utilities.Tools;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FeatureAlbumAdapter extends FeatureAdapter<Release> {

    private static final String TAG = "FeatureAlbumAdapter";

    private RemoteMediaCallback remoteMediaCallback;

    public FeatureAlbumAdapter(Context context, List<Release> releases, String title, View rootView) {
        super(context, rootView, R.layout.feature_other_layout);

        remoteMediaCallback = (RemoteMediaCallback) context;

        Feature feature = onCreateFeature(title, releases);
        onAttachFeature(feature);
    }

    public Feature onCreateFeature(String title, List<Release> releases) {
        return new Feature(title, releases, true);
    }

    @Override
    public void inflateHighlightView(View rootView, Release highlight) {
        if(highlight != null) {
            RelativeLayout relativeLayout = rootView.findViewById(R.id.feature_highlight);

            View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.feature_highlight_layout, relativeLayout, true);
            view.setOnClickListener(item -> onClickItemView(highlight));

            fillCaptionView(view, rootView, highlight.getUrl(), 325, 325);

            ((TextView) view.findViewById(R.id.release_artist)).setText(highlight.getArtist());
            ((TextView) view.findViewById(R.id.release_title)).setText(highlight.getTitle());

            Tools.ScoreView scoreView = new Tools.ScoreView(
                    view.findViewById(R.id.release_score),
                    view.findViewById(R.id.release_impact)
            );

            Tools.onComputeScore(getContext(), highlight.getReviews(), scoreView, view);

            onSetFlipHighlight(rootView);
        }
    }

    private void onSetFlipHighlight(View rootView) {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            private int position = 0;

            private int animators[] = new int[] {
                    R.animator.card_flip_left_out,
                    R.animator.card_flip_left_in,
                    R.animator.card_flip_right_in,
                    R.animator.card_flip_right_out
            };

            @Override
            public void run() {
                AnimatorSet animatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), animators[position]);
                animatorSet.setTarget(rootView.findViewById(R.id.highlight_layout));
                animatorSet.start();

                AnimatorSet animatorSet1 = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(),  animators[position + 1]);
                animatorSet1.setTarget(rootView.findViewById(R.id.highlight_layout_complimentary));
                animatorSet1.start();

                position = (position + 2) % 4;

                handler.postDelayed(this, 5000);
            }
        };

        handler.postDelayed(runnable, 5000);
    }

    @Override
    public void inflateFreshView(View rootView, List<Release> content) {
        LinearLayout linearLayout = rootView.findViewById(R.id.feature_fresh);

        for(Release release : content) {
            View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.feature_fresh_layout, linearLayout, false);
            view.setOnClickListener(item -> onClickItemView(release));

            fillCaptionView(view, null, release.getUrl(), 250, 250);

            ((TextView) view.findViewById(R.id.release_artist)).setText(release.getArtist());
            ((TextView) view.findViewById(R.id.release_title)).setText(release.getTitle());

            Tools.ScoreView scoreView = new Tools.ScoreView(
                    view.findViewById(R.id.release_score),
                    view.findViewById(R.id.release_impact)
            );

            Tools.onComputeScore(getContext(), release.getReviews(), scoreView);

            linearLayout.addView(view);
        }
    }

    @Override
    public void onClickItemView(Release item) {
        super.onClickItemView(item);

        remoteMediaCallback.onRemoteMediaClick(item.getUri());
    }

    @Override
    public void onBindViewHolder(@NonNull FeatureAdapter.FeatureViewHolder holder, int position) {
        Release release = data.get(position);

        Picasso.get()
                .load(release.getUrl())
                .resize(200, 200)
                .into(holder.caption);

        holder.itemView.setOnClickListener(view -> onClickItemView(release));
        holder.title.setText(release.getTitle());
        holder.artist.setText(getContext().getString(R.string.app_component_author, release.getArtist()));

        List<Review> reviews = release.getReviews();
        String score = Tools.onComputeScore(getContext(), reviews, null);
        holder.score.setText(score);
    }
}
