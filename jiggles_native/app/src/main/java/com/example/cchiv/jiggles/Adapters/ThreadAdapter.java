package com.example.cchiv.jiggles.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cchiv.jiggles.Constants;
import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.model.Comm;
import com.example.cchiv.jiggles.model.Thread;
import com.example.cchiv.jiggles.utilities.Tools;
import com.example.cchiv.jiggles.utilities.TreeParser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ThreadViewHolder> {

    private static final String TAG = "ThreadAdapter";

    private ArrayList<Thread> threads;
    private Context context;

    private Typeface typeface;

    private boolean menuToggle = false;

    public ThreadAdapter(Context context, ArrayList<Thread> threads) {
        this.context = context;
        this.threads = threads;

        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Brandon_bld.otf");
    }

    @NonNull
    @Override
    public ThreadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        return new ThreadViewHolder(
                layoutInflater
                        .inflate(R.layout.thread_layout, parent, false)
        );
    }

    private void viewContrastizer(View view, int color) {
        if(view instanceof LinearLayout) {
            LinearLayout linearLayout = (LinearLayout) view;

            int len = linearLayout.getChildCount();
            for(int it = 0; it < len; it++) {
                View child = linearLayout.getChildAt(it);

                if(child instanceof TextView) {
                    ((TextView) child).setTextColor(color);
                } else if(child instanceof LinearLayout) {
                    viewContrastizer(child, color);
                } else if(child instanceof ImageView) {
                    ImageView imageView = (ImageView) child;
                    ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(color));
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ThreadViewHolder holder, int position) {
        Thread thread = threads.get(position);

        Uri uri = buildUriResource(thread.getCaption());
        Picasso.get()
                .load(uri)
                .into(holder.thumbnail, new Callback() {
                    @Override
                    public void onSuccess() {
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) holder.thumbnail.getDrawable();

                        if(bitmapDrawable != null) {
                            Palette palette = Palette.from(bitmapDrawable.getBitmap()).generate();
                            int lightMutedColor = palette.getLightMutedColor(ContextCompat.getColor(context, R.color.primaryTextColor));
                            int darkMutedColor = palette.getDarkMutedColor(ContextCompat.getColor(context, R.color.primaryTextColor));

                            viewContrastizer(holder.utilities, darkMutedColor);
                            holder.itemView.setBackgroundColor(lightMutedColor);
                        }
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

        holder.author.setText(thread.getAuthor().getName());
        holder.date.setText(Tools.parseDate(thread.getId()));
        holder.like.setText(String.valueOf(thread.getVotes()));
        holder.content.setText(thread.getContent());

        TreeParser treeParser = new TreeParser();
        List<Comm> comments = treeParser.queryTree(thread.getComments());

        CommentAdapter commentAdapter = new CommentAdapter(context, comments);
        holder.comments.setAdapter(commentAdapter);
        holder.comments.setNestedScrollingEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        holder.comments.setLayoutManager(linearLayoutManager);

        holder.comment.setOnClickListener((view) -> {
            // Do the comment

            Log.v(TAG, thread.getId());
        });

        holder.menu_button.setOnClickListener((view) -> {
            if(menuToggle)
                holder.menu.setVisibility(View.GONE);
            else holder.menu.setVisibility(View.VISIBLE);

            menuToggle = !menuToggle;
        });
    }

    public Uri buildUriResource(String path) {
        return new Uri.Builder()
                .scheme(Constants.SCHEME)
                .authority(Constants.AUTHORITY)
                .appendEncodedPath(path)
                .build();
    }

    @Override
    public int getItemCount() {
        if(threads != null)
            return threads.size();
        else return 0;
    }

    public void swapContent(ArrayList<Thread> threads) {
        this.threads = threads;
    }

    public class ThreadViewHolder extends RecyclerView.ViewHolder {

        private ImageView thumbnail;
        private TextView like;
        private TextView author;
        private TextView date;
        private TextView comment;
        private TextView content;
        private ConstraintLayout utilities;
        private RecyclerView comments;

        private ImageView menu_button;

        private LinearLayout menu;
        private TextView edit;
        private TextView delete;

        public ThreadViewHolder(View itemView) {
            super(itemView);

            thumbnail = itemView.findViewById(R.id.thumbnail);
            comment = itemView.findViewById(R.id.comment);
            author = itemView.findViewById(R.id.author);
            date = itemView.findViewById(R.id.date);
            like = itemView.findViewById(R.id.like);
            utilities = itemView.findViewById(R.id.utilities);
            content = itemView.findViewById(R.id.content);
            comments = itemView.findViewById(R.id.comments);

            menu = itemView.findViewById(R.id.menu);
            menu_button = itemView.findViewById(R.id.menu_button);

            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);

            like.setTypeface(typeface);
            author.setTypeface(typeface);
            date.setTypeface(typeface);
            comment.setTypeface(typeface);
        }
    }
}
