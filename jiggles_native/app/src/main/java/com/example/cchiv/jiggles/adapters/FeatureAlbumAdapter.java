package com.example.cchiv.jiggles.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.model.Release;
import com.example.cchiv.jiggles.model.Review;
import com.example.cchiv.jiggles.utilities.Tools;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FeatureAlbumAdapter extends FeatureAdapter<Release> {

    private static final String TAG = "FeatureAlbumAdapter";

    public FeatureAlbumAdapter(Context context, List<Release> releases, String title, View rootView) {
        super(context, rootView, R.layout.feature_other_layout);

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

            fillCaptionView(view, rootView, highlight.getUrl(), 325, 325);

            ((TextView) view.findViewById(R.id.release_artist)).setText(highlight.getArtist());
            ((TextView) view.findViewById(R.id.release_title)).setText(highlight.getTitle());

            Tools.ScoreView scoreView = new Tools.ScoreView(
                    view.findViewById(R.id.release_score),
                    view.findViewById(R.id.release_impact)
            );

            Tools.onComputeScore(getContext(), highlight.getReviews(), scoreView, false);
        }
    }

    @Override
    public void inflateFreshView(View rootView, List<Release> content) {
        LinearLayout linearLayout = rootView.findViewById(R.id.feature_fresh);

        for(Release release : content) {
            View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.feature_fresh_layout, linearLayout, false);

            fillCaptionView(view, null, release.getUrl(), 250, 250);

            ((TextView) view.findViewById(R.id.release_artist)).setText(release.getArtist());
            ((TextView) view.findViewById(R.id.release_title)).setText(release.getTitle());

            Tools.ScoreView scoreView = new Tools.ScoreView(
                    view.findViewById(R.id.release_score),
                    view.findViewById(R.id.release_impact)
            );

            Tools.onComputeScore(getContext(), release.getReviews(), scoreView, false);

            linearLayout.addView(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull FeatureAdapter.FeatureViewHolder holder, int position) {
        Release release = data.get(position);

        Picasso.get()
                .load(release.getUrl())
                .resize(200, 200)
                .into(holder.caption);

        holder.title.setText(release.getTitle());
        holder.artist.setText(getContext().getString(R.string.app_component_author, release.getArtist()));

        List<Review> reviews = release.getReviews();
        String score = Tools.onComputeScore(getContext(), reviews, null, false);
        holder.score.setText(score);
    }
}
