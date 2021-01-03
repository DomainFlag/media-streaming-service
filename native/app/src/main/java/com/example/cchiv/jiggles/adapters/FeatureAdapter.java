package com.example.cchiv.jiggles.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.utilities.Tools;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

public abstract class FeatureAdapter<T> extends ModelAdapter<FeatureAdapter.FeatureViewHolder, T> {

    private static final String TAG = "FeatureAdapter";

    private Context context;

    private View rootView;
    private int resource;

    private RecyclerView recyclerView;
    private AutoLinearLayoutManager layoutManager = null;

    public FeatureAdapter(Context context, View view, int resource) {
        this.context = context;
        this.rootView = view;
        this.resource = resource;
    }

    public void onAttachFeature(Feature feature) {
        recyclerView = rootView.findViewById(R.id.feature_other);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        FeatureItemDecoration featureItemDecoration = new FeatureItemDecoration(16);
        recyclerView.addItemDecoration(featureItemDecoration);

        layoutManager =
                new AutoLinearLayoutManager(context,
                        LinearLayoutManager.HORIZONTAL,
                        false);
        layoutManager.setAutoScroll(recyclerView);
        layoutManager.setOnTouchListener(recyclerView);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(this);

        ((TextView) rootView.findViewById(R.id.feature_title)).setText(feature.title);

        onSwapData(feature);
    }

    public void pause() {
        if(layoutManager != null)
            layoutManager.removeAutoScroll();
    }

    public void resume() {
        if(layoutManager != null)
            layoutManager.setAutoScroll(recyclerView);
    }

    public void release() {
        if(layoutManager != null)
            layoutManager.removeAutoScroll();
    }

    public void disableHighlightFeature() {
        rootView.findViewById(R.id.feature_highlight_layout).setVisibility(View.GONE);
    }

    public void disableFreshFeature() {
        rootView.findViewById(R.id.feature_fresh_layout).setVisibility(View.GONE);
    }

    public void disableOtherFeature() {
        rootView.findViewById(R.id.feature_other_layout).setVisibility(View.GONE);
    }

    public Context getContext() {
        return context;
    }

    public void fillCaptionView(View rootView, View parent, String url, int width, int height) {
        ImageView caption = rootView.findViewById(R.id.release_caption);
        Picasso.get()
                .load(url)
                .resize(width, height)
                .placeholder(R.drawable.ic_artwork_placeholder)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        caption.setImageBitmap(bitmap);

                        if(parent != null) {
                            int color = Tools.getPaletteColor(context, bitmap);

                            GradientDrawable gradientDrawable = new GradientDrawable(
                                    GradientDrawable.Orientation.LEFT_RIGHT,
                                    new int[] {
                                            color,
                                            ContextCompat.getColor(context, R.color.iconsTextColor)
                                    }
                            );

                            View view = parent.findViewById(R.id.feature_highlight);
                            ViewCompat.setBackground(view, gradientDrawable);
                        } else {
                            View view = rootView.findViewById(R.id.release_divider);

                            if(view != null) {
                                int color = Tools.getPaletteColor(context, bitmap);

                                GradientDrawable drawable = (GradientDrawable) view.getBackground();
                                drawable.setColor(color);
                            }
                        }
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) { }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {}
                });
    }

    public abstract void inflateHighlightView(View rootView, T highlight);

    public abstract void inflateFreshView(View rootView, List<T> content);

    public void onClickItemView(T item) {}

    public void onSwapData(Feature feature) {
        if(feature.highlight != null) {
            rootView.setVisibility(View.VISIBLE);
        } else {
            rootView.setVisibility(View.GONE);
        }

        inflateHighlightView(rootView, feature.highlight);
        inflateFreshView(rootView, feature.fresh);

        onSwapData(feature.others);
        notifyDataSetChanged();
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

    public class Feature {

        private String title = null;

        private T highlight = null;
        private List<T> fresh = new ArrayList<>();
        private List<T> others = new ArrayList<>();

        private boolean complete;

        public Feature(String title, boolean complete) {
            this.title = title;
            this.complete = complete;
        }

        public Feature(String title, List<T> content, boolean complete) {
            this(title, complete);

            if(content == null)
                return;

            if(content.size() > 0) {
                highlight = content.get(0);

                if(content.size() > 2) {
                    if(complete) {
                        fresh = content.subList(1, 3);

                        if(content.size() > 3)
                            others = content.subList(3, content.size());
                    } else {
                        others = content.subList(1, content.size());
                    }
                }
            }
        }

        public Feature(String title, T highlight, List<T> fresh, List<T> others, boolean complete) {
            this(title, complete);

            this.highlight = highlight;
            this.fresh = fresh;
            this.others = others;
        }

        public String getTitle() {
            return title;
        }

        public T getHighlight() {
            return highlight;
        }

        public List<T> getFresh() {
            return fresh;
        }

        public List<T> getOthers() {
            return others;
        }
    }

    @NonNull
    @Override
    public FeatureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FeatureViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(resource, parent, false)) {
        };
    }

    public class FeatureViewHolder extends RecyclerView.ViewHolder {
        public ImageView caption;
        public TextView artist;
        public TextView title;
        public TextView score;

        public FeatureViewHolder(View itemView) {
            super(itemView);

            caption = itemView.findViewById(R.id.release_caption);
            artist = itemView.findViewById(R.id.release_artist);
            title = itemView.findViewById(R.id.release_title);
            score = itemView.findViewById(R.id.release_score);
        }
    }
}
