package com.example.cchiv.jiggles.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.squareup.picasso.Target;

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

        FeatureItemDecoration featureItemDecoration = new FeatureItemDecoration(16);
        recyclerView.addItemDecoration(featureItemDecoration);

        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(context,
                        LinearLayoutManager.HORIZONTAL,
                        false);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(otherAdapter);

        ((TextView) view.findViewById(R.id.feature_title)).setText(feature.title);

        inflateHighlightView(view, feature.highlight);
        inflateFreshView(view, feature.notable);
    }

    private int getPaletteColor(Bitmap bitmap) {
        Palette palette = Palette.from(bitmap).generate();

        int defaultColor = ContextCompat.getColor(context, R.color.iconsTextColor);

        int color = palette.getVibrantColor(defaultColor);
        if(defaultColor == color)
            color = palette.getLightVibrantColor(defaultColor);


        if(defaultColor == color)
            color = palette.getDominantColor(defaultColor);

        return color;
    }

    private void fillCaptionView(View rootView, View parent, Release release, int width, int height) {
        ImageView caption = rootView.findViewById(R.id.release_caption);
        Picasso.get()
                .load(release.getUrl())
                .resize(width, height)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        caption.setImageBitmap(bitmap);

                        if(parent != null) {
                            int color = getPaletteColor(bitmap);

                            GradientDrawable gradientDrawable = new GradientDrawable(
                                    GradientDrawable.Orientation.LEFT_RIGHT,
                                    new int[] {
                                            color,
                                            ContextCompat.getColor(context, R.color.iconsTextColor)
                                    }
                            );

                            View view = parent.findViewById(R.id.feature_highlight_gradient);
                            ViewCompat.setBackground(view, gradientDrawable);
                        }
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

        ((TextView) rootView.findViewById(R.id.release_artist)).setText(release.getArtist());
        ((TextView) rootView.findViewById(R.id.release_title)).setText(release.getTitle());
    }

    private void inflateHighlightView(View rootView, Release release) {
        if(release != null) {
            LinearLayout linearLayout = rootView.findViewById(R.id.feature_highlight);

            View view = LayoutInflater.from(context)
                    .inflate(R.layout.feature_highlight_layout, null, false);

            fillCaptionView(view, rootView, release, 325, 325);
            onComputeScore(release.getReviews(), view, false);

            linearLayout.addView(view);
        }
    }

    private void inflateFreshView(View rootView, List<Release> releases) {
        LinearLayout linearLayout = rootView.findViewById(R.id.feature_fresh);

        for(Release release : releases) {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.feature_fresh_layout, linearLayout, false);

            fillCaptionView(view, null, release, 250, 250);
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
                        .inflate(R.layout.feature_review_layout, root, false);

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
            ImageView viewImpact = rootView.findViewById(R.id.release_impact);

            if(total < IMPACT_THRESHOLD)
                viewImpact.setVisibility(View.GONE);

            viewScore.setText(totalScore);
        }

        return totalScore;
    }

    public void onSwapData(Feature feature) {
        inflateHighlightView(rootView, feature.highlight);
        inflateFreshView(rootView, feature.notable);

        otherAdapter.onSwapData(feature.others);
        otherAdapter.notifyDataSetChanged();
    }

    public class FeatureItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        private FeatureItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView
                .State state) {
            int position = parent.getChildAdapterPosition(view);

            if(position == 0)
                outRect.right = space;
            else if(position == parent.getChildCount() - 1)
                outRect.left = space;
            else {
                outRect.left = space;
                outRect.right = space;
            }
        }
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
                    .resize(200, 200)
                    .into(holder.caption);

            holder.title.setText(release.getTitle());
            holder.artist.setText(context.getString(R.string.app_component_author, release.getArtist()));

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
