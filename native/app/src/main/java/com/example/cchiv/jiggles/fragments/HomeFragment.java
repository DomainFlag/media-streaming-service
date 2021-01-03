package com.example.cchiv.jiggles.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.adapters.FeedAdapter;
import com.example.cchiv.jiggles.adapters.ReplyAdapter;
import com.example.cchiv.jiggles.databinding.FragmentHomeLayoutBinding;
import com.example.cchiv.jiggles.model.FeedItem;
import com.example.cchiv.jiggles.utilities.NetworkUtilities;
import com.example.cchiv.jiggles.utilities.Tools;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;


public class HomeFragment extends Fragment implements FeedAdapter.OnClickReplies {

    private static final String TAG = "HomeFragment";

    private Context context;

    private FeedAdapter feedAdapter;
    private ReplyAdapter replyAdapter;
    private FeedItem feedItem = null;

    private FragmentHomeLayoutBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeLayoutBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        Tools.resolveCallbackUser(result -> {
            if(result == null)
                return;

            // binding.replyAuthorName.setText(result.getName());

            Picasso
                    .get()
                    .load(result.getCaption())
                    .placeholder(R.drawable.ic_account)
                    .error(R.drawable.ic_account)
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                            imageDrawable.setCircular(true);
                            imageDrawable.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);

                            binding.homeAccountCaption.setImageDrawable(imageDrawable);
                            // binding.homeReplyAuthorCaption.setImageDrawable(imageDrawable);
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            binding.homeAccountCaption.setImageDrawable(errorDrawable);
                            // binding.replyAuthorCaption.setImageDrawable(errorDrawable);
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                            binding.homeAccountCaption.setImageDrawable(placeHolderDrawable);
                            // binding.replyAuthorCaption.setImageDrawable(placeHolderDrawable);
                        }
                    });
        });

//        binding.homeRepliesLayout.setVisibility(View.GONE);
//        binding.homeRepliesClose.setOnClickListener(view -> {
//            binding.homeRepliesLayout.setVisibility(View.GONE);
//
//            feedItem = null;
//        });

        float density = getResources().getDisplayMetrics().density;

        binding.homeList.addItemDecoration(new FeedItemDecoration((int) density * 8));
        binding.homeList.setNestedScrollingEnabled(false);
        binding.homeList.setHasFixedSize(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        binding.homeList.setLayoutManager(linearLayoutManager);

        feedAdapter = new FeedAdapter(context, this, new ArrayList<>());
        binding.homeList.setAdapter(feedAdapter);

        binding.homeAccountLayout.setVisibility(View.VISIBLE);
        binding.homeAccountLayout.setOnClickListener((view) -> {
            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();

            ThreadFragment fragment = new ThreadFragment();
            fragment.onAttachThreadCreationCallback(thread -> {
                feedAdapter.onAddItem(thread);
                feedAdapter.notifyItemChanged(feedAdapter.getItemCount() - 1);

                fragmentManager.beginTransaction()
                        .remove(fragment)
                        .commit();

                binding.homeAccountLayout.setVisibility(View.VISIBLE);
            });

            fragmentManager.beginTransaction()
                    .add(R.id.home_thread, fragment)
                    .commit();

            binding.homeAccountLayout.setVisibility(View.GONE);
        });

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
//        binding.replyAction.setOnClickListener(view -> {
//            if(feedItem != null) {
//                String content = binding.replyValue.getText().toString();
//
//                if(content.isEmpty())
//                    return;
//
//                binding.replyAction.setColorFilter(ContextCompat.getColor(context, R.color.unexpectedColor));
//                NetworkUtilities.ResolveReplyAction resolveReplyAction = new NetworkUtilities.ResolveReplyAction(thread -> {
//                    feedAdapter.getItems().set(position, thread);
//                    feedAdapter.notifyItemChanged(position);
//
//                    replyAdapter.getReplies().add(thread.getReplies().get(thread.getReplies().size() - 1));
//                    replyAdapter.notifyDataSetChanged();
//
//                    binding.replyAction.setColorFilter(ContextCompat.getColor(context, R.color.primaryTextColor));
//                }, feedItem.encodeJSONObject(null, content), NetworkUtilities.RequestAdaptBuilder.getType(false), Tools.getToken(context));
//            }
//        });
    }

    @Override
    public void onClickRepliesCallback(FeedItem feedItem) {
//        this.feedItem = feedItem;
//
//        setClickReplyCallback(0);
//
//        replyAdapter = new ReplyAdapter(context, feedItem.getReplies());
//        binding.homeFeedItemReplies.setAdapter(replyAdapter);
//
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
//        binding.homeFeedItemReplies.setLayoutManager(linearLayoutManager);
//
//        binding.homeRepliesLayout.setVisibility(View.VISIBLE);
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
