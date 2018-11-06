package com.example.cchiv.jiggles.fragments.pager;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.cchiv.jiggles.Constants;
import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.adapters.ContentAdapter;
import com.example.cchiv.jiggles.model.Collection;
import com.example.cchiv.jiggles.utilities.NetworkUtilities;

public class SearchFragment extends Fragment implements TextView.OnEditorActionListener {

    private final static String TAG = "SearchActivity";

    private static final int SPAN_COLS = 2;

    private Context context;

    private View editTextBorder;
    private ContentAdapter contentAdapter;

    private View rootView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_search, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.search_result);
        recyclerView.setNestedScrollingEnabled(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, SPAN_COLS, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);

        contentAdapter = new ContentAdapter(context, null);
        recyclerView.setAdapter(contentAdapter);

        ImageButton imageButton = rootView.findViewById(R.id.search_close);
//        imageButton.setOnClickListener(view -> finish());

        EditText editText = (EditText) rootView.findViewById(R.id.search_edit);
        editText.setOnEditorActionListener(this);
        editText.requestFocus();

        editTextBorder = rootView.findViewById(R.id.search_edit_border);

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

        return rootView;
    }

    public void fetchQueryResults(String query) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.AUTH_TOKEN, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(Constants.TOKEN, null);

        if(token != null) {
            NetworkUtilities networkUtilities = new NetworkUtilities();
            networkUtilities.fetchSearchResults((Collection collection) -> {
                // Do something with the collection

                contentAdapter.swapCollection(collection);
                contentAdapter.notifyDataSetChanged();
            }, query, token);
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionType, KeyEvent keyEvent) {
        if(actionType == EditorInfo.IME_ACTION_SEARCH) {
            EditText editText = (EditText) rootView.findViewById(R.id.search_edit);
            editText.clearFocus();

            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if(inputMethodManager != null)
                inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);

            fetchQueryResults(editText.getEditableText().toString());
            editText.setText(null);

            return true;
        }

        return false;
    }
}
