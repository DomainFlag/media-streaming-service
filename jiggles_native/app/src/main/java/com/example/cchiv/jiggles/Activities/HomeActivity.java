package com.example.cchiv.jiggles.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.cchiv.jiggles.Adapters.NewsAdapter;
import com.example.cchiv.jiggles.Adapters.ReleaseAdapter;
import com.example.cchiv.jiggles.Constants;
import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.Utilities.NetworkUtilities;

public class HomeActivity extends AppCompatActivity implements TextView.OnEditorActionListener {

    private static final String TAG = "HomeActivity";

    private static final int GRID_COLS = 3;

    private NetworkUtilities networkUtilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.AUTH_TOKEN, MODE_PRIVATE);
        String token = sharedPreferences.getString(Constants.TOKEN, null);
        if(token != null) {
            TextView textView = findViewById(R.id.home_state);
            textView.setText(token);
        } else {
            finish();
        }

        networkUtilities = new NetworkUtilities();

        fetchReleases();
        fetchNews();
    }

    public void onClickSearch(View view) {
        final View mainSearchContainer = findViewById(R.id.main_search_container);
        mainSearchContainer.setVisibility(View.VISIBLE);

//        final View mainContainer = findViewById(R.id.main_layout);
//        mainContainer.setVisibility(View.GONE);

        final ImageButton imageButton = findViewById(R.id.search_close);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainSearchContainer.setVisibility(View.GONE);
//                mainContainer.setVisibility(View.VISIBLE);
            }
        });

        final EditText editText = (EditText) findViewById(R.id.search_edit);
        editText.setOnEditorActionListener(this);
        editText.requestFocus();

        final View editTextBorder = findViewById(R.id.search_edit_border);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int len = charSequence.length();

                if(len == 0 || charSequence.equals(getResources().getString(R.string.app_filter_search_hint))) {
                    editTextBorder.getLayoutParams().width = 16;
                } else {
                    Paint paint = new Paint();
                    paint.setTextSize(18);
                    paint.setTypeface(Typeface.MONOSPACE);

                    float nowWidth = paint.measureText(charSequence.toString());

                    editTextBorder.getLayoutParams().width = Math.max((int) nowWidth, 16);
                }

                editTextBorder.requestLayout();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        };

        editText.addTextChangedListener(textWatcher);
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionType, KeyEvent keyEvent) {
        if(actionType == EditorInfo.IME_ACTION_SEARCH) {
            EditText editText = (EditText) findViewById(R.id.search_edit);
            editText.clearFocus();

            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if(inputMethodManager != null)
                inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);

            View mainSearchContainer = findViewById(R.id.main_search_container);
            mainSearchContainer.setVisibility(View.GONE);

//            View mainContainer = findViewById(R.id.main_layout);
//            mainContainer.setVisibility(View.VISIBLE);

            return true;
        }

        return false;
    }

    public void fetchReleases() {
        networkUtilities.fetchReleases(releases -> {
            // Do something with releases
            if(releases != null) {
                // Do something with Releases
                RecyclerView recyclerView = findViewById(R.id.home_release_list);
                recyclerView.setHasFixedSize(true);
                recyclerView.setNestedScrollingEnabled(true);

                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,
                        GRID_COLS,
                        LinearLayoutManager.VERTICAL,
                        false);
                recyclerView.setLayoutManager(layoutManager);

                ReleaseAdapter releaseAdapter = new ReleaseAdapter(this, releases);
                recyclerView.setAdapter(releaseAdapter);
            }
        });
    }

    public void fetchNews() {
        networkUtilities.fetchNews(news -> {
            if(news == null) {
                // Do something with News
                RecyclerView recyclerView = findViewById(R.id.home_news_list);
                recyclerView.setHasFixedSize(true);
                recyclerView.setNestedScrollingEnabled(true);

                RecyclerView.LayoutManager layoutManager =
                        new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(layoutManager);

                NewsAdapter newsAdapter = new NewsAdapter(this, news);
                recyclerView.setAdapter(newsAdapter);
            }
        });
    }
}
