package com.example.cchiv.jiggles.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.model.Reply;
import com.example.cchiv.jiggles.utilities.Tools;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private static final String TAG = "CommentAdapter";

    private Context context;
    private List<Reply> comments;

    public CommentAdapter(Context context, List<Reply> comments) {
        this.context = context;
        this.comments = comments;
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
        Reply comment = comments.get(position);

        int dimen = (int) context.getResources().getDimension(R.dimen.activity_margin_component);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.comment.getLayoutParams();
        params.setMargins(dimen * comment.getDepth(), 0, 0, 0);

        holder.author.setText(comment.getAuthor().getName());
        holder.date.setText(Tools.parseDate(comment.getId()));
        holder.content.setText(comment.getContent());
        holder.like.setText(String.valueOf(comment.getLikes()));
//        if(comment.getLikes().containsKey(Tools.getUser().get_id()))
//            holder.like.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.pleasureColor)));

//        holder.like.setOnClickListener(view -> {
//            // Resolve up-vote
//            String token = Tools.getToken(context);
//            NetworkUtilities.ResolveCommentLike resolveCommentLike = new NetworkUtilities.ResolveCommentLike(result -> {
//                comments.set(position, result);
//                notifyItemChanged(position);
//            }, comment.encodeJSONObject(), NetworkUtilities.RequestAdaptBuilder.TYPE_POST, token);
//        });

        holder.reply.setOnClickListener(view -> {
            if(holder.replyContainer.getVisibility() == View.GONE)
                holder.replyContainer.setVisibility(View.VISIBLE);
            else holder.replyContainer.setVisibility(View.GONE);
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout comment;
        private ImageView caption;
        private TextView author;
        private TextView date;
        private TextView content;
        private View replyContainer;
        private TextView like;
        private TextView reply;

        private ImageView reply_author;
        private EditText reply_value;

        public CommentViewHolder(View itemView) {
            super(itemView);

            comment = itemView.findViewById(R.id.comment);

            caption = itemView.findViewById(R.id.caption);
            author = itemView.findViewById(R.id.author);
            date = itemView.findViewById(R.id.date);
            content = itemView.findViewById(R.id.content);
            like = itemView.findViewById(R.id.like);
            reply = itemView.findViewById(R.id.reply);

            reply_author = itemView.findViewById(R.id.reply_author);
            reply_value = itemView.findViewById(R.id.reply_value);

        }
    }
}
