package com.example.cchiv.jiggles.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cchiv.jiggles.Model.News;
import com.example.cchiv.jiggles.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private Context context;
    private ArrayList<News> news;

    public NewsAdapter(Context context, ArrayList<News> news) {
        this.context = context;
        this.news = news;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsViewHolder(LayoutInflater
                .from(context)
                .inflate(R.layout.news_layout, parent, false));
    }

    private News getItem(int position) {
        return news.get(position);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News news = getItem(position);

        Picasso.get()
                .load(news.getCaption())
                .resize(320, 180)
                .centerCrop()
                .into(holder.caption);

        holder.author.setText(String.format(context.getString(R.string.home_news_author), news.getAuthor()));
        holder.header.setText(news.getHeader());
    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder {

        private ImageView caption;
        private TextView header;
        private TextView author;

        private NewsViewHolder(View itemView) {
            super(itemView);

            caption = itemView.findViewById(R.id.news_caption);
            header = itemView.findViewById(R.id.news_header);
            author = itemView.findViewById(R.id.news_author);
        }
    }
}
