package com.example.cchiv.jiggles.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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

import java.util.ArrayList;
import java.util.Locale;

public class ReleaseAdapter extends ModelAdapter<ReleaseAdapter.ReleaseViewHolder, Release> {

    private static final String TAG = "ReleaseAdapter";

    private static final Float IMPACT_THRESHOLD = 8.0f;

    private Context context;

    public ReleaseAdapter(Context context, ArrayList<Release> releases) {
        this.context = context;
        this.data = releases;
    }

    @NonNull
    @Override
    public ReleaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReleaseViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.release_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReleaseViewHolder holder, int position) {
        Release release = data.get(position);
        ArrayList<Review> reviews = release.getReviews();
        onBindDataViewHolder(holder, release.getUrl(), release.getArtist(), release.getTitle(), reviews);
    }


    private void onBindDataViewHolder(@NonNull ReleaseViewHolder holder, String url, String artist, String title, ArrayList<Review> reviews) {
        Picasso.get()
                .load(url)
                .resize(175, 175)
                .into(holder.caption);

        holder.artist.setText(artist);
        holder.title.setText(title);

        float total = 0;
        for(int it = 0; it < reviews.size(); it++) {
            Review review = reviews.get(it);

            View view = LayoutInflater.from(context)
                    .inflate(R.layout.review_layout, holder.reviews, false);

            int score = review.getScore();
            int color;
            if(score < 60)
                color = ContextCompat.getColor(context, R.color.colorScoreOther);
            else if(score < 80)
                color = ContextCompat.getColor(context, R.color.colorScorePossitive);
            else color = ContextCompat.getColor(context, R.color.colorScoreAcclaim);

            view.findViewById(R.id.review_score_indicator).setBackgroundColor(color);

            ((TextView) view.findViewById(R.id.review_score)).setText(String.valueOf(score));
            ((TextView) view.findViewById(R.id.review_author)).setText(review.getAuthor());
            ((TextView) view.findViewById(R.id.review_content)).setText(review.getContent());

            holder.reviews.addView(view);

            /* Getting the mean score of all reviews */
            total += review.getScore();
        }

        total /= reviews.size() * 10.0f;

        if(total < IMPACT_THRESHOLD)
            holder.impact.setVisibility(View.GONE);
        else holder.score.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));

        holder.score.setText(String.format(Locale.US, "%.1f", total));
    }

    public class ReleaseViewHolder extends RecyclerView.ViewHolder {

        private ImageView caption;
        private TextView artist;
        private TextView title;
        private ImageView impact;
        private TextView score;
        private LinearLayout reviews;

        private ReleaseViewHolder(View itemView) {
            super(itemView);

            caption = itemView.findViewById(R.id.release_caption);
            artist = itemView.findViewById(R.id.release_artist);
            title = itemView.findViewById(R.id.release_title);
            impact = itemView.findViewById(R.id.release_impact);
            score = itemView.findViewById(R.id.release_score);
            reviews = itemView.findViewById(R.id.release_reviews);
        }
    }
}
