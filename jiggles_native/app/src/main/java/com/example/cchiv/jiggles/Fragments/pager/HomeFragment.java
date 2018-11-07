package com.example.cchiv.jiggles.fragments.pager;

import android.content.Context;
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
import com.example.cchiv.jiggles.adapters.ThreadAdapter;
import com.example.cchiv.jiggles.fragments.ThreadFragment;
import com.example.cchiv.jiggles.utilities.NetworkUtilities;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_layout, container, false);

        rootView.findViewById(R.id.forum_thread_creator).setOnClickListener((view) -> {
            Fragment fragment = new ThreadFragment();

            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.forum_thread, fragment)
                    .commit();
        });

        RecyclerView recyclerView = rootView.findViewById(R.id.forum_list);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        ThreadAdapter threadAdapter = new ThreadAdapter(context, null);
        recyclerView.setAdapter(threadAdapter);

        NetworkUtilities networkUtilities = new NetworkUtilities();
        networkUtilities.fetchThreads((threads) -> {
            threadAdapter.swapContent(threads);
            threadAdapter.notifyDataSetChanged();
        });

        return rootView;
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//
//        inflater.inflate(R.menu.forum_menu, menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch(item.getItemId()) {
//            case R.menu.forum_menu : {
//                // Handle the forum menu event
//                Log.v(TAG, String.valueOf(R.menu.forum_menu));
//            }
//            default: return false;
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
    }
}