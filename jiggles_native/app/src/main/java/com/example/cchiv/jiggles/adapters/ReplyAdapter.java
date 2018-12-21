package com.example.cchiv.jiggles.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.model.Reply;
import com.example.cchiv.jiggles.utilities.Tools;

import java.util.List;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.CommentViewHolder> {

    private static final String TAG = "ReplyAdapter";

    private Context context;
    private List<Reply> replies;

    public ReplyAdapter(Context context, List<Reply> replies) {
        this.context = context;
        this.replies = replies;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentViewHolder(
                LayoutInflater.from(context)
                        .inflate(R.layout.home_comment_layout, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Reply reply = replies.get(position);

//        int dimen = (int) context.getResources().getDimension(R.dimen.activity_margin_component);
//
//        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.reply.getLayoutParams();
//        params.setMargins(dimen * reply.getDepth(), 0, 0, 0);

        holder.author.setText(reply.getAuthor().getName());
        holder.date.setText(Tools.parseSocialDate(reply.get_id()));
        holder.content.setText(reply.getContent());

        holder.likeContent.setText(String.valueOf(reply.getLikes().size()));
        Tools.resolveCallbackUser(user -> {
            if(reply.getLikes().containsKey(user.get_id())) {
                int color = context.getResources().getColor(R.color.unexpectedColor);

                holder.likeIcon.setColorFilter(color);
                holder.likeContent.setTextColor(color);
            }
        });

        holder.likeLayout.setOnClickListener(view -> {
            String token = Tools.getToken(context);

//            NetworkUtilities.ResolveThreadLike resolveThreadLike = new NetworkUtilities.ResolveThreadLike(result -> {
//                items.set(position, result);
//                notifyItemChanged(position);
//            }, thread.encodeJSONObject(), NetworkUtilities.RequestAdaptBuilder.getType(thread.isOwnership()), token);
        });
    }

    public List<Reply> getReplies() {
        return replies;
    }

    @Override
    public int getItemCount() {
        return replies.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        private ImageView caption;
        private TextView author;
        private TextView date;
        private TextView content;
        private LinearLayout likeLayout;
        private ImageView likeIcon;
        private TextView likeContent;
        private LinearLayout reply;

        public CommentViewHolder(View itemView) {
            super(itemView);

            caption = itemView.findViewById(R.id.reply_author_caption);
            author = itemView.findViewById(R.id.reply_author);
            date = itemView.findViewById(R.id.reply_timestamp);
            content = itemView.findViewById(R.id.reply_content);
            likeLayout = itemView.findViewById(R.id.feed_item_like_layout);
            likeIcon = itemView.findViewById(R.id.feed_item_like_icon);
            likeContent = itemView.findViewById(R.id.feed_item_like_content);
            reply = itemView.findViewById(R.id.feed_item_reply);
        }
    }
}
