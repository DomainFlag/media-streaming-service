package com.example.cchiv.jiggles.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.data.ContentContract.NewsEntry;
import com.example.cchiv.jiggles.model.News;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewsAdapter extends ModelAdapter<NewsAdapter.NewsViewHolder, News> {

    private Context context;

    public NewsAdapter(Context context, ArrayList<News> news) {
        this.context = context;
        this.data = news;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsViewHolder(LayoutInflater
                .from(context)
                .inflate(R.layout.news_layout, parent, false));
    }

    private News getItem(int position) {
        return data.get(position);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        if(getCursor() != null) {
            Cursor cursor = getCursor();

            cursor.moveToPosition(position);
            int indexCursorNewsAuthor = cursor.getColumnIndexOrThrow(NewsEntry.COL_NEWS_AUTHOR);
            int indexCursorNewsCaption = cursor.getColumnIndexOrThrow(NewsEntry.COL_NEWS_CAPTION);
            int indexCursorNewsHeader = cursor.getColumnIndexOrThrow(NewsEntry.COL_NEWS_HEADER);

            onBindDataViewHolder(holder,
                    cursor.getString(indexCursorNewsCaption),
                    cursor.getString(indexCursorNewsAuthor),
                    cursor.getString(indexCursorNewsHeader));
        } else {
            News news = getItem(position);
            onBindDataViewHolder(holder, news.getCaption(), news.getAuthor(), news.getHeader());
        }
    }

    private void onBindDataViewHolder(@NonNull NewsViewHolder holder, String caption, String author, String header) {
        Picasso.get()
                .load(caption)
                .resize(320, 180)
                .centerCrop()
                .into(holder.caption);

        holder.author.setText(String.format(context.getString(R.string.home_news_author), author));
        holder.header.setText(header);
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
