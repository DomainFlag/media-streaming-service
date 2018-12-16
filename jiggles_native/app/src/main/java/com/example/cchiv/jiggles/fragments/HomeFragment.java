package com.example.cchiv.jiggles.fragments;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.adapters.FeedAdapter;
import com.example.cchiv.jiggles.utilities.NetworkUtilities;
import com.example.cchiv.jiggles.utilities.Tools;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_layout, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.home_list);

        float density = getResources().getDisplayMetrics().density;

        recyclerView.addItemDecoration(new FeedItemDecoration((int) density * 8));
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        FeedAdapter feedAdapter = new FeedAdapter(context, new ArrayList<>());
        recyclerView.setAdapter(feedAdapter);

        rootView.findViewById(R.id.home_thread_creator).setOnClickListener((view) -> {
            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();

            ThreadFragment fragment = new ThreadFragment();
            fragment.onAttachThreadCreationCallback(thread -> {
                feedAdapter.onAddItem(thread);
                feedAdapter.notifyItemChanged(feedAdapter.getItemCount() - 1);

                fragmentManager.beginTransaction()
                        .remove(fragment)
                        .commit();
            });

            fragmentManager.beginTransaction()
                    .add(R.id.home_thread, fragment)
                    .commit();
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
