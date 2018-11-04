package com.example.cchiv.jiggles.activities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.fragments.pager.HomeFragment;
import com.example.cchiv.jiggles.fragments.pager.LatestFragment;
import com.example.cchiv.jiggles.fragments.pager.StoreFragment;
import com.example.cchiv.jiggles.utilities.Tools;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    private static final int HOME_NUM_PAGES = 3;

    private ViewPager viewPager;

    public static final int[] TAB_ICONS_ARRAY = {
            R.drawable.ic_home,
            R.drawable.ic_home,
            R.drawable.ic_store,
    };

    public static final int ARRAY_TAB_TITLES = R.array.home_pager_fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Tools.resolveAuthToken(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Tab> tabs = Tab.generate(this);
        SliderPageAdapter pageAdapter = new SliderPageAdapter(this, fragmentManager, tabs);

        viewPager = findViewById(R.id.home_pager);
        viewPager.setAdapter(pageAdapter);

        setTabLayout(pageAdapter, viewPager);
    }

    @Override
    public void onBackPressed() {
        if(viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    public void setTabLayout(SliderPageAdapter pageAdapter, ViewPager viewPager) {
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        for(int it = 0; it < tabLayout.getTabCount(); it++) {
            TabLayout.Tab tab = tabLayout.getTabAt(it);
            if(tab == null)
                continue;

            tab.setCustomView(pageAdapter.getTabView(it));
        }
    }

    public class SliderPageAdapter extends FragmentPagerAdapter {

        private Context context;
        private List<Tab> tabs;

        private SliderPageAdapter(Context context, FragmentManager fm, List<Tab> tabs) {
            super(fm);

            this.context = context;
            this.tabs = tabs;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch(position) {
                case 0 : {
                    fragment = new HomeFragment();
                    break;
                }
                case 1 : {
                    fragment = new LatestFragment();
                    break;
                }
                case 2 : {
                    fragment = new StoreFragment();
                    break;
                }
                default : {
                    Log.v(TAG, "Unknown app part requested");
                }
            }

            if(fragment != null)
                fragment.onAttach(context);

            return fragment;
        }

        private View getTabView(int pos) {
            Tab tab = tabs.get(pos);

            View view = LayoutInflater.from(context).inflate(R.layout.pager_tab_layout, null);

            ImageView imageView = view.findViewById(R.id.tab_icon);
            imageView.setImageDrawable(tab.getDrawable());

            TextView textView = view.findViewById(R.id.tab_title);
            textView.setText(tab.getTitle());

            return view;
        }

        @Override
        public int getCount() {
            return HOME_NUM_PAGES;
        }
    }

    private static class Tab {

        private Drawable drawable;
        private String title;

        private Tab(Drawable drawable, String title) {
            this.drawable = drawable;
            this.title = title;
        }

        private Drawable getDrawable() {
            return drawable;
        }

        private String getTitle() {
            return title;
        }

        private static List<Tab> generate(Context context) {
            List<Tab> tabs = new ArrayList<>();

            String[] titles = context.getResources().getStringArray(ARRAY_TAB_TITLES);
            for(int i = 0; i < HOME_NUM_PAGES; i++) {
                Drawable icon = ContextCompat.getDrawable(context, TAB_ICONS_ARRAY[i]);
                String title = titles[i];

                Tab tab = new Tab(icon, title);

                tabs.add(tab);
            }

            return tabs;
        }
    }
}
