package com.example.cchiv.jiggles.fragments;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.adapters.FeedAdapter;
import com.example.cchiv.jiggles.adapters.ReplyAdapter;
import com.example.cchiv.jiggles.model.FeedItem;
import com.example.cchiv.jiggles.model.Reply;
import com.example.cchiv.jiggles.utilities.NetworkUtilities;
import com.example.cchiv.jiggles.utilities.Tools;
import com.example.cchiv.jiggles.utilities.TreeParser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment implements FeedAdapter.OnClickReplies {

    private static final String TAG = "HomeFragment";

    private Context context;

    private FeedAdapter feedAdapter;
    private ReplyAdapter replyAdapter;
    private FeedItem feedItem = null;

    @BindView(R.id.home_list) RecyclerView recyclerView;

    @BindView(R.id.home_replies_layout) CardView cardRepliesLayoutView;
    @BindView(R.id.home_replies_item) TextView textRepliesItemView;
    @BindView(R.id.home_replies_close) ImageView imageRepliesCloseView;
    @BindView(R.id.home_feed_item_replies) RecyclerView recyclerRepliesView;

    @BindView(R.id.reply_author_caption) ImageView imageAuthorCaptionView;
    @BindView(R.id.reply_author_name) TextView textAuthorNameView;
    @BindView(R.id.reply_value) EditText editReplyValueText;
    @BindView(R.id.reply_action) ImageView imageReplyActionView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_layout, container, false);

        ButterKnife.bind(this, rootView);

//        textAuthorNameView.setText(Tools.getUser().getName());

        cardRepliesLayoutView.setVisibility(View.GONE);
        imageRepliesCloseView.setOnClickListener(view -> {
            cardRepliesLayoutView.setVisibility(View.GONE);

            feedItem = null;
        });

        float density = getResources().getDisplayMetrics().density;

        recyclerView.addItemDecoration(new FeedItemDecoration((int) density * 8));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        feedAdapter = new FeedAdapter(context, this, new ArrayList<>());
        recyclerView.setAdapter(feedAdapter);

//        rootView.findViewById(R.id.home_thread_creator).setOnClickListener((view) -> {
//            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
//
//            ThreadFragment fragment = new ThreadFragment();
//            fragment.onAttachThreadCreationCallback(thread -> {
//                feedAdapter.onAddItem(thread);
//                feedAdapter.notifyItemChanged(feedAdapter.getItemCount() - 1);
//
//                fragmentManager.beginTransaction()
//                        .remove(fragment)
//                        .commit();
//            });
//
//            fragmentManager.beginTransaction()
//                    .add(R.id.home_thread, fragment)
//                    .commit();
//        });

        NetworkUtilities.FetchThreads fetchThreads = new NetworkUtilities.FetchThreads(threads -> {
            feedAdapter.getItems().addAll(threads);
            feedAdapter.notifyDataSetChanged();
        });

        NetworkUtilities.FetchPosts fetchPosts = new NetworkUtilities.FetchPosts(posts -> {
            feedAdapter.getItems().addAll(posts);
            feedAdapter.notifyDataSetChanged();
        }, Tools.getToken(context));

        return rootView;
    }

    public void setClickReplyCallback(int position) {
        imageReplyActionView.setOnClickListener(view -> {
            if(feedItem != null) {
                String content = editReplyValueText.getText().toString();

                if(content.isEmpty())
                    return;

                imageReplyActionView.setColorFilter(ContextCompat.getColor(context, R.color.unexpectedColor));
                NetworkUtilities.ResolveReplyAction resolveReplyAction = new NetworkUtilities.ResolveReplyAction(thread -> {
                    feedAdapter.getItems().set(position, thread);
                    feedAdapter.notifyItemChanged(position);

                    replyAdapter.getReplies().add(thread.getReplies().get(thread.getReplies().size() - 1));
                    replyAdapter.notifyDataSetChanged();

                    imageReplyActionView.setColorFilter(ContextCompat.getColor(context, R.color.primaryTextColor));
                }, feedItem.encodeJSONObject(null, content), NetworkUtilities.RequestAdaptBuilder.getType(false), Tools.getToken(context));
            }
        });
    }

    @Override
    public void onClickRepliesCallback(FeedItem feedItem) {
        this.feedItem = feedItem;

        setClickReplyCallback(0);

        TreeParser treeParser = new TreeParser();
        List<Reply> replies = treeParser.queryTree(feedItem.getReplies());

        replyAdapter = new ReplyAdapter(context, replies);
        recyclerRepliesView.setAdapter(replyAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerRepliesView.setLayoutManager(linearLayoutManager);

        cardRepliesLayoutView.setVisibility(View.VISIBLE);
    }

    public class FeedItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        private FeedItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView
                .State state) {
            int position = parent.getChildAdapterPosition(view);

            if(position == 0)
                outRect.bottom = space;
            else if(position == parent.getChildCount() - 1)
                outRect.top = space;
            else {
                outRect.top = space;
                outRect.bottom = space;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
    }
}
