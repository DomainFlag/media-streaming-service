package com.example.cchiv.jiggles.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cchiv.jiggles.Model.Thread;
import com.example.cchiv.jiggles.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ThreadViewHolder> {

    private static final String TAG = "ThreadAdapter";

    private ArrayList<Thread> threads;
    private Context context;

    public ThreadAdapter(Context context, ArrayList<Thread> threads) {
        this.context = context;
        this.threads = threads;
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

    private String parseDate(String objectId) {
        Date date = new Date(Integer.parseInt(objectId.substring(0, 8), 16) * 1000);

        return date.toString();
    }

    @Override
    public void onBindViewHolder(@NonNull ThreadViewHolder holder, int position) {
        Thread thread = threads.get(position);

        Picasso.get()
                .load(thread.getCaption())
                .into(holder.thumbnail);

        holder.author.setText(thread.getAuthor().getName());
        holder.date.setText(parseDate(thread.getId()));
//        holder.like.setText(thread.getVotes());

        holder.comment.setOnClickListener((view) -> {
            // Do the comment

            Log.v(TAG, thread.getId());
        });
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

        public ThreadViewHolder(View itemView) {
            super(itemView);

            thumbnail = itemView.findViewById(R.id.thumbnail);
            comment = itemView.findViewById(R.id.comment);
            author = itemView.findViewById(R.id.author);
            date = itemView.findViewById(R.id.date);
            like = itemView.findViewById(R.id.like);
        }
    }
}
