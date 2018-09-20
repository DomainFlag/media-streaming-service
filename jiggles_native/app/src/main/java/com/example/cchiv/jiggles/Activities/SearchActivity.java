package com.example.cchiv.jiggles.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
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

import com.example.cchiv.jiggles.adapters.ContentAdapter;
import com.example.cchiv.jiggles.Constants;
import com.example.cchiv.jiggles.model.Content;
import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.utilities.NetworkUtilities;

public class SearchActivity extends AppCompatActivity implements TextView.OnEditorActionListener {

    private final static String TAG = "SearchActivity";

    private static final int SPAN_COLS = 2;

    private View editTextBorder;
    private RecyclerView recyclerView;
    private ContentAdapter contentAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recyclerView = findViewById(R.id.search_result);
        recyclerView.setNestedScrollingEnabled(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, SPAN_COLS, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);

        contentAdapter = new ContentAdapter(this, null);
        recyclerView.setAdapter(contentAdapter);

        ImageButton imageButton = findViewById(R.id.search_close);
        imageButton.setOnClickListener(view -> finish());

        EditText editText = (EditText) findViewById(R.id.search_edit);
        editText.setOnEditorActionListener(this);
        editText.requestFocus();

        editTextBorder = findViewById(R.id.search_edit_border);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int len = charSequence.length();

                if(len == 0 || charSequence.equals(getResources().getString(R.string.search_hint))) {
                    editTextBorder.getLayoutParams().width = 16;
                } else {
                    Paint paint = new Paint();
                    paint.setTextSize(editText.getTextSize());
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

    public void fetchQueryResults(String query) {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.AUTH_TOKEN, MODE_PRIVATE);
        String token = sharedPreferences.getString(Constants.TOKEN, null);

        if(token != null) {
            NetworkUtilities networkUtilities = new NetworkUtilities();
            networkUtilities.fetchSearchResults((Content content) -> {
                // Do something with the collection

                contentAdapter.swapCollection(content);
                contentAdapter.notifyDataSetChanged();
            }, query, token);
        } else finish();
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionType, KeyEvent keyEvent) {
        if(actionType == EditorInfo.IME_ACTION_SEARCH) {
            EditText editText = (EditText) findViewById(R.id.search_edit);
            editText.clearFocus();

            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if(inputMethodManager != null)
                inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);

            fetchQueryResults(editText.getEditableText().toString());
            editText.setText(null);

            return true;
        }

        return false;
    }
}
