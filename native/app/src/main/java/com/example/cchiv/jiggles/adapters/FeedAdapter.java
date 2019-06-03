package com.example.cchiv.jiggles.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
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
import com.example.cchiv.jiggles.interfaces.RemoteMediaCallback;
import com.example.cchiv.jiggles.model.player.content.Album;
import com.example.cchiv.jiggles.model.FeedItem;
import com.example.cchiv.jiggles.model.Post;
import com.example.cchiv.jiggles.model.Thread;
import com.example.cchiv.jiggles.utilities.NetworkUtilities;
import com.example.cchiv.jiggles.utilities.Tools;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "FeedAdapter";

    public interface OnClickReplies {
        void onClickRepliesCallback(FeedItem feedItem);
    }

    private OnClickReplies onClickReplies;
    private RemoteMediaCallback remoteMediaCallback;
    private List<FeedItem> items;
    private Context context;

    public FeedAdapter(Context context, OnClickReplies onClickReplies, List<FeedItem> items) {
        this.context = context;
        this.onClickReplies = onClickReplies;
        this.remoteMediaCallback = (RemoteMediaCallback) context;
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        switch(viewType) {
            case FeedItem.ViewType.VIEW_POST : {
                return new PostViewHolder(
                        layoutInflater
                                .inflate(R.layout.home_post_layout, parent, false)
                );
            }
            case FeedItem.ViewType.VIEW_THREAD : {
                return new ThreadViewHolder(
                        layoutInflater
                                .inflate(R.layout.home_thread_layout, parent, false)
                );
            }
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        switch(viewType) {
            case FeedItem.ViewType.VIEW_POST : {
                onBindPostViewHolder((PostViewHolder) holder, position);
                break;
            }
            case FeedItem.ViewType.VIEW_THREAD : {
                onBindThreadViewHolder((ThreadViewHolder) holder, position);
                break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getViewType();
    }

    public void onBindPostViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = (Post) items.get(position);
        Album album = post.getAlbum();

        Uri uri = album.getArt().getUrl();
        Picasso.get()
                .load(uri)
                .into(holder.caption);

        holder.timestamp.setText(Tools.parseSocialDate(post.get_id()));

        Picasso.get()
                .load(post.getAuthor().getCaption())
                .error(R.drawable.ic_account)
                .placeholder(R.drawable.ic_account)
                .into(holder.account);

        holder.likeContent.setText(String.valueOf(post.getLikes().size()));
        Tools.resolveCallbackUser(user -> {
            if(post.getLikes().containsKey(user.get_id())) {
                int color = context.getResources().getColor(R.color.unexpectedColor);

                holder.likeIcon.setColorFilter(color);
                holder.likeContent.setTextColor(color);
            }
        });

        holder.action.setOnClickListener(view -> {
            if(remoteMediaCallback != null) {
                remoteMediaCallback.onRemoteMediaClick(album.getUri());
            }
        });

        holder.likeLayout.setOnClickListener(view -> {
            // TODO(1) like for post
        });

        holder.reply.setOnClickListener(view -> {
            // TODO(2) replies
        });
        holder.author.setText(post.getAuthor().getName());


        holder.repliesView.setText(context.getString(R.string.feed_item_comments_label, post.getReplies().size()));
        holder.repliesView.setOnClickListener(view -> {
            onClickReplies.onClickRepliesCallback(post);
        });

        holder.title.setText(album.getName());
        holder.subtitle.setText(album.getArtist().getName());
        holder.type.setText(context.getString(R.string.post_like));

        holder.menuButton.setOnClickListener((view) -> {
            PopupMenu popupMenu = new ThreadPopupMenu(context, holder.menuButton);
            popupMenu.show();
        });
    }

    public void onBindThreadViewHolder(@NonNull ThreadViewHolder holder, int position) {
        Thread thread = (Thread) items.get(position);

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
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

        Picasso.get()
                .load(thread.getAuthor().getCaption())
                .error(R.drawable.ic_account)
                .placeholder(R.drawable.ic_account)
                .into(holder.account);

        holder.timestamp.setText(Tools.parseSocialDate(thread.get_id()));

        holder.likeContent.setText(String.valueOf(thread.getLikes().size()));

        Tools.resolveCallbackUser(user -> {
            if(thread.getLikes().containsKey(user.get_id())) {
                int color = context.getResources().getColor(R.color.unexpectedColor);

                holder.likeIcon.setColorFilter(color);
                holder.likeContent.setTextColor(color);
            }
        });

        holder.likeLayout.setOnClickListener(view -> {
            String token = Tools.getToken(context);

            Tools.resolveCallbackUser(user -> {
                NetworkUtilities.ResolveThreadLike resolveThreadLike = new NetworkUtilities.ResolveThreadLike(result -> {
                    items.set(position, result);

                    notifyItemChanged(position);
                }, thread.encodeJSONObject(null, null),
                        NetworkUtilities.RequestAdaptBuilder.getType(user.resolveOwnership(thread)), token);
            });
        });

        holder.reply.setOnClickListener(view -> onClickReplies.onClickRepliesCallback(thread));
        holder.author.setText(thread.getAuthor().getName());

        holder.repliesView.setText(context.getString(R.string.feed_item_comments_label, thread.getReplies().size()));
        holder.repliesView.setOnClickListener(view -> onClickReplies.onClickRepliesCallback(thread));

        holder.followersView.setText(context.getString(R.string.thread_following_label, "Jiggles"));
        holder.content.setText(thread.getContent());

        holder.menuButton.setOnClickListener((view) -> {
            PopupMenu popupMenu = new ThreadPopupMenu(context, holder.menuButton);
            popupMenu.show();
        });
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
        if(items != null)
            return items.size();
        else return 0;
    }

    public void swapContent(List<FeedItem> items) {
        this.items = items;
    }

    public void onAddItem(FeedItem item) {
        this.items.add(item);
    }

    public List<FeedItem> getItems() {
        return items;
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        private ImageView caption;
        private ImageView account;
        private LinearLayout likeLayout;
        private ImageView likeIcon;
        private TextView likeContent;
        private TextView author;
        private LinearLayout reply;
        private TextView timestamp;
        private TextView title;
        private TextView subtitle;
        private TextView type;
        private TextView action;

        private TextView repliesView;
        private ImageView menuButton;

        public PostViewHolder(View itemView) {
            super(itemView);

            caption = itemView.findViewById(R.id.post_caption);
            account = itemView.findViewById(R.id.post_account);
            timestamp = itemView.findViewById(R.id.post_timestamp);
            reply = itemView.findViewById(R.id.feed_item_reply);
            author = itemView.findViewById(R.id.post_author);
            likeLayout = itemView.findViewById(R.id.feed_item_like_layout);
            likeIcon = itemView.findViewById(R.id.feed_item_like_icon);
            likeContent = itemView.findViewById(R.id.feed_item_like_content);
            title = itemView.findViewById(R.id.post_content_title);
            subtitle = itemView.findViewById(R.id.post_content_subtitle);
            action = itemView.findViewById(R.id.post_action);
            type = itemView.findViewById(R.id.post_type);

            repliesView = itemView.findViewById(R.id.post_replies_view);

            menuButton = itemView.findViewById(R.id.post_menu_button);
        }
    }

    public class ThreadViewHolder extends RecyclerView.ViewHolder {

        private ImageView caption;
        private ImageView account;
        private LinearLayout likeLayout;
        private ImageView likeIcon;
        private TextView likeContent;
        private TextView author;
        private LinearLayout reply;
        private TextView timestamp;
        private TextView content;

        private TextView repliesView;
        private TextView followersView;

        private ImageView menuButton;

        public ThreadViewHolder(View itemView) {
            super(itemView);

            caption = itemView.findViewById(R.id.thread_caption);
            account = itemView.findViewById(R.id.thread_account);
            timestamp = itemView.findViewById(R.id.thread_timestamp);
            reply = itemView.findViewById(R.id.feed_item_reply);
            author = itemView.findViewById(R.id.thread_author);
            likeLayout = itemView.findViewById(R.id.feed_item_like_layout);
            likeIcon = itemView.findViewById(R.id.feed_item_like_icon);
            likeContent = itemView.findViewById(R.id.feed_item_like_content);
            content = itemView.findViewById(R.id.thread_content);
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

            setOnMenuItemClickListener(menuItem -> {
                return false;
            });

            menuPopupHelper.setForceShowIcon(true);
        }
    }
}
