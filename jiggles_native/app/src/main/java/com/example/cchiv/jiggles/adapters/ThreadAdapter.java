package com.example.cchiv.jiggles.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.cchiv.jiggles.Constants;
import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.model.Reply;
import com.example.cchiv.jiggles.model.Thread;
import com.example.cchiv.jiggles.utilities.NetworkUtilities;
import com.example.cchiv.jiggles.utilities.Tools;
import com.example.cchiv.jiggles.utilities.TreeParser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ThreadViewHolder> {

    private static final String TAG = "ThreadAdapter";

    private List<Thread> threads;
    private Context context;

    public ThreadAdapter(Context context, List<Thread> threads) {
        this.context = context;
        this.threads = threads;
    }

    @NonNull
    @Override
    public ThreadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        return new ThreadViewHolder(
                layoutInflater
                        .inflate(R.layout.home_thread, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ThreadViewHolder holder, int position) {
        Thread thread = threads.get(position);

        Uri uri = buildUriResource(thread.getCaption());
        Picasso.get()
                .load(uri)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        holder.caption.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        holder.caption.setVisibility(View.GONE);
                        holder.divider.setVisibility(View.GONE);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

        holder.timestamp.setText(Tools.parseSocialDate(thread.getId()));

        holder.likeContent.setText(String.valueOf(thread.getLikes().size()));
        if(thread.isOwnership()) {
            int color = context.getResources().getColor(R.color.unexpectedColor);

            holder.likeIcon.setColorFilter(color);
            holder.likeContent.setTextColor(color);
        }

        holder.likeLayout.setOnClickListener(view -> {
            String token = Tools.getToken(context);

            NetworkUtilities.ResolveThreadLike resolveThreadLike = new NetworkUtilities.ResolveThreadLike(result -> {
                threads.set(position, result);
                notifyItemChanged(position);
            }, thread.encodeJSONObject(), NetworkUtilities.RequestAdaptBuilder.getType(thread.isOwnership()), token);
        });

        holder.reply.setOnClickListener(view -> {
            holder.replies.setVisibility(View.VISIBLE);
        });
        holder.author.setText(thread.getAuthor().getName());


        holder.repliesView.setText(context.getString(R.string.thread_comments_label, thread.getComments().size()));
        holder.repliesView.setOnClickListener(view -> {
            holder.replies.setVisibility(View.VISIBLE);
        });

        holder.followersView.setText(context.getString(R.string.thread_following_label, "Jiggles"));

        holder.content.setText(thread.getContent());
        buildThreadRepliesTree(holder, thread);

        holder.menuButton.setOnClickListener((view) -> {
            PopupMenu popupMenu = new ThreadPopupMenu(context, holder.menuButton);
            popupMenu.show();
        });
    }

    private void buildThreadRepliesTree(@NonNull ThreadViewHolder holder, Thread thread) {
        TreeParser treeParser = new TreeParser();
        List<Reply> comments = treeParser.queryTree(thread.getComments());

        CommentAdapter commentAdapter = new CommentAdapter(context, comments);
        holder.replies.setAdapter(commentAdapter);
        holder.replies.setNestedScrollingEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        holder.replies.setLayoutManager(linearLayoutManager);
    }

    private Uri buildUriResource(String path) {
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

    public void swapContent(List<Thread> threads) {
        this.threads = threads;
    }

    public class ThreadViewHolder extends RecyclerView.ViewHolder {

        private ImageView caption;
        private View divider;
        private LinearLayout likeLayout;
        private ImageView likeIcon;
        private TextView likeContent;
        private TextView author;
        private LinearLayout reply;
        private TextView timestamp;
        private TextView content;

        private RecyclerView replies;
        private TextView repliesView;
        private TextView followersView;

        private ImageView menuButton;

        public ThreadViewHolder(View itemView) {
            super(itemView);

            caption = itemView.findViewById(R.id.thread_caption);
            divider = itemView.findViewById(R.id.thread_divider);
            timestamp = itemView.findViewById(R.id.thread_timestamp);
            reply = itemView.findViewById(R.id.thread_reply);
            author = itemView.findViewById(R.id.thread_author);
            likeLayout = itemView.findViewById(R.id.thread_like_layout);
            likeIcon = itemView.findViewById(R.id.thread_like_icon);
            likeContent = itemView.findViewById(R.id.thread_like_content);
            content = itemView.findViewById(R.id.thread_content);
            replies = itemView.findViewById(R.id.thread_replies);
            repliesView = itemView.findViewById(R.id.thread_replies_view);
            followersView = itemView.findViewById(R.id.thread_followers_view);

            menuButton = itemView.findViewById(R.id.thread_menu_button);
        }
    }

    public class ThreadPopupMenu extends PopupMenu {

        @SuppressLint("RestrictedApi")
        public ThreadPopupMenu(Context context, View anchor) {
            super(context, anchor);

            MenuBuilder menuBuilder = new MenuBuilder(context);
            MenuPopupHelper menuPopupHelper = new MenuPopupHelper(context, menuBuilder, anchor);

            Menu menu = getMenu();
            getMenuInflater().inflate(R.menu.thread_menu, menu);

            menuPopupHelper.setForceShowIcon(true);
        }
    }
}
