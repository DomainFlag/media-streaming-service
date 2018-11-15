package com.example.cchiv.jiggles.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.model.Release;
import com.example.cchiv.jiggles.model.Review;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FeatureAdapter {

    private static final String TAG = "FeatureAdapter";

    private static final Float IMPACT_THRESHOLD = 8.0f;

    private Context context;

    private View rootView;

    private OtherAdapter otherAdapter;

    public FeatureAdapter(Context context, Feature feature, View view) {
        this.context = context;
        this.rootView = view;
        this.otherAdapter = new OtherAdapter(context, feature.others);

        RecyclerView recyclerView = view.findViewById(R.id.feature_other);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(context,
                        LinearLayoutManager.HORIZONTAL,
                        false);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(otherAdapter);

        ((TextView) view.findViewById(R.id.feature_title)).setText(feature.title);

        inflateHighlightView(view, feature.highlight);
        inflateNotableView(view, feature.notable);
    }

    private void fillCaptionView(View rootView, Release release, int width, int height) {
        ImageView caption = rootView.findViewById(R.id.release_caption);
        Picasso.get()
                .load(release.getUrl())
                .resize(width, height)
                .into(caption);

        ((TextView) rootView.findViewById(R.id.release_artist)).setText(release.getArtist());
        ((TextView) rootView.findViewById(R.id.release_title)).setText(release.getTitle());
    }

    private void inflateHighlightView(View rootView, Release release) {
        if(release != null) {
            LinearLayout linearLayout = rootView.findViewById(R.id.feature_highlight);

            View view = LayoutInflater.from(context)
                    .inflate(R.layout.feature_highlight_layout, null, false);

            fillCaptionView(view, release, 200, 200);
            onComputeScore(release.getReviews(), view, true);

            linearLayout.addView(view);
        }
    }

    private void inflateNotableView(View rootView, List<Release> releases) {
        LinearLayout linearLayout = rootView.findViewById(R.id.feature_notable);

        for(Release release : releases) {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.feature_notable_layout, null, false);

            fillCaptionView(view, release, 65, 65);
            onComputeScore(release.getReviews(), view, false);

            linearLayout.addView(view);
        }
    }

    private String onComputeScore(List<Review> reviews, View rootView, boolean detailedLayout) {
        float total = 0;

        for(int it = 0; it < reviews.size(); it++) {
            Review review = reviews.get(it);

            int color, score = review.getScore();
            if(score < 60)
                color = ContextCompat.getColor(context, R.color.colorScoreOther);
            else if(score < 80)
                color = ContextCompat.getColor(context, R.color.colorScorePossitive);
            else color = ContextCompat.getColor(context, R.color.colorScoreAcclaim);

            if(detailedLayout) {
                LinearLayout root = rootView.findViewById(R.id.release_reviews);

                View view = LayoutInflater.from(context)
                        .inflate(R.layout.review_layout, root, false);

                view.findViewById(R.id.review_score_indicator).setBackgroundColor(color);

                ((TextView) view.findViewById(R.id.review_score)).setText(String.valueOf(score));
                ((TextView) view.findViewById(R.id.review_author)).setText(review.getAuthor());
                ((TextView) view.findViewById(R.id.review_content)).setText(review.getContent());

                root.addView(view);
            }

            total += review.getScore();
        }

        total /= reviews.size() * 10.0f;

        String totalScore = String.format(Locale.US, "%.1f", total);

        if(rootView != null || detailedLayout) {
            TextView viewScore = rootView.findViewById(R.id.release_score);

            if(total < IMPACT_THRESHOLD)
                rootView.findViewById(R.id.release_impact).setVisibility(View.GONE);
            else viewScore.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));

            viewScore.setText(totalScore);
        }

        return totalScore;
    }

    public void onSwapData(Feature feature) {
        inflateHighlightView(rootView, feature.highlight);
        inflateNotableView(rootView, feature.notable);

        Log.v(TAG, String.valueOf(feature.others.size()));
        otherAdapter.onSwapData(feature.others);
        otherAdapter.notifyDataSetChanged();
    }

    public static class Feature {
        private String title = null;
        private Release highlight = null;
        private List<Release> notable = new ArrayList<>();
        private List<Release> others = new ArrayList<>();

        public Feature(String title) {
            this.title = title;
        }

        public Feature(String title, Release highlight, List<Release> notable, List<Release> others) {
            this.title = title;
            this.highlight = highlight;
            this.notable = notable;
            this.others = others;
        }

        public static Feature parse(String title, List<Release> releases) {
            Feature feature = new Feature(title);
            feature.title = title;

            if(releases.size() > 0) {
                feature.highlight = releases.get(0);

                if(releases.size() > 1) {
                    feature.notable = releases.subList(1, 3);

                    if(releases.size() > 2)
                        feature.others = releases.subList(3, releases.size());
                }
            }

            return feature;
        }
    }

    public class OtherAdapter extends ModelAdapter<OtherAdapter.ReleaseViewHolder, Release> {

        private static final String TAG = "OtherAdapter";

        private Context context;

        private OtherAdapter(Context context, List<Release> releases) {
            this.context = context;
            this.data = releases;
        }

        @NonNull
        @Override
        public OtherAdapter.ReleaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new OtherAdapter.ReleaseViewHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.feature_other_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull OtherAdapter.ReleaseViewHolder holder, int position) {
            Release release = data.get(position);

            Picasso.get()
                    .load(release.getUrl())
                    .resize(100, 100)
                    .into(holder.caption);

            holder.artist.setText(release.getArtist());
            holder.title.setText(release.getTitle());

            List<Review> reviews = release.getReviews();
            String score = onComputeScore(reviews, null, false);
            holder.score.setText(score);
        }

        public class ReleaseViewHolder extends RecyclerView.ViewHolder {

            private ImageView caption;
            private TextView artist;
            private TextView title;
            private TextView score;

            private ReleaseViewHolder(View itemView) {
                super(itemView);

                caption = itemView.findViewById(R.id.release_caption);
                artist = itemView.findViewById(R.id.release_artist);
                title = itemView.findViewById(R.id.release_title);
                score = itemView.findViewById(R.id.release_score);
            }
        }
    }
}
