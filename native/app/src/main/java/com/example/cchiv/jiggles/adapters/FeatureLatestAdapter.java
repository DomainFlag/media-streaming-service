package com.example.cchiv.jiggles.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.model.News;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FeatureLatestAdapter extends FeatureAdapter<News> {

    public FeatureLatestAdapter(Context context, List<News> news, String title, View rootView) {
        super(context, rootView, R.layout.feature_other_full_layout);

        disableFreshFeature();

        Feature feature = onCreateFeature(title, news);
        onAttachFeature(feature);
    }

    public Feature onCreateFeature(String title, List<News> news) {
        return new Feature(title, news, false);
    }

    @Override
    public void inflateHighlightView(View rootView, News highlight) {
        if(highlight != null) {
            RelativeLayout relativeLayout = rootView.findViewById(R.id.feature_highlight);

            View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.feature_highlight_full_layout, relativeLayout, true);

            fillCaptionView(view, null, highlight.getCaption(), 325, 325);

            ((TextView) view.findViewById(R.id.release_artist)).setText(highlight.getAuthor());
            ((TextView) view.findViewById(R.id.release_title)).setText(highlight.getHeader());
        }
    }

    @Override
    public void inflateFreshView(View rootView, List<News> content) {}

    @Override
    public void onBindViewHolder(@NonNull FeatureAdapter.FeatureViewHolder holder, int position) {
        News news = data.get(position);

        Picasso.get()
                .load(news.getCaption())
                .resize(200, 200)
                .into(holder.caption);

        holder.title.setText(news.getHeader());
        holder.artist.setText(getContext().getString(R.string.app_component_author, news.getAuthor()));

        if(holder.score != null)
            holder.score.setVisibility(View.GONE);;
    }
}
