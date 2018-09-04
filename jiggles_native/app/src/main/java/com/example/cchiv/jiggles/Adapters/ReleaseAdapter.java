package com.example.cchiv.jiggles.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cchiv.jiggles.Model.Release;
import com.example.cchiv.jiggles.Model.Review;
import com.example.cchiv.jiggles.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

public class ReleaseAdapter extends RecyclerView.Adapter<ReleaseAdapter.ReleaseViewHolder> {

    private static final Float IMPACT_THRESHOLD = 8.0f;

    private Context context;
    private ArrayList<Release> releases;

    public ReleaseAdapter(Context context, ArrayList<Release> releases) {
        this.context = context;
        this.releases = releases;
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
        Release release = releases.get(position);

        Picasso.get()
                .load(release.getUrl())
                .into(holder.caption);

        holder.artist.setText(release.getArtist());
        holder.title.setText(release.getTitle());

        ArrayList<Review> reviews = release.getReviews();
        float score = 0;
        for(int it = 0; it < reviews.size(); it++) {
            Review review = reviews.get(it);

            View view = LayoutInflater.from(context)
                    .inflate(R.layout.review_layout, holder.reviews, false);

            ((TextView) view.findViewById(R.id.review_score)).setText(String.valueOf(review.getScore()));
            ((TextView) view.findViewById(R.id.review_author)).setText(review.getAuthor());
            ((TextView) view.findViewById(R.id.review_content)).setText(review.getContent());

            holder.reviews.addView(view);

            /* Getting the mean score of all reviews */
            score += review.getScore();
        }

        score /= reviews.size() * 10.0f;

        if(score < IMPACT_THRESHOLD)
            holder.impact.setVisibility(View.GONE);

        holder.score.setText(String.format(Locale.US, "%.1f", score));
    }

    @Override
    public int getItemCount() {
        return releases.size();
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
