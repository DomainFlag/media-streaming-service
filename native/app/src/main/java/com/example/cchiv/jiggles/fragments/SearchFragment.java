package com.example.cchiv.jiggles.fragments;

import android.app.Activity;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cchiv.jiggles.R;
import com.example.cchiv.jiggles.adapters.ContentAdapter;
import com.example.cchiv.jiggles.interfaces.RemoteMediaCallback;
import com.example.cchiv.jiggles.utilities.NetworkUtilities;
import com.example.cchiv.jiggles.utilities.Tools;

public class SearchFragment extends Fragment implements TextView.OnEditorActionListener {

    private final static String TAG = "SearchFragment";

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
        rootView = inflater.inflate(R.layout.fragment_search_layout, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.search_result);
        recyclerView.setNestedScrollingEnabled(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);

        contentAdapter = new ContentAdapter(context, null);
        contentAdapter.onAttachItemClickListener(store -> {
            if(store != null && store.getAlbums().size() > 0) {
                ((RemoteMediaCallback) context).onRemoteMediaClick(store.getAlbum(0).getUri());
            }
        });

        recyclerView.setAdapter(contentAdapter);

        ImageView imageView = rootView.findViewById(R.id.home_bar_switch);
        imageView.setOnClickListener(view -> ((Activity) context).onBackPressed());

        EditText editText = rootView.findViewById(R.id.home_bar_edit);
        editText.setOnEditorActionListener(this);
        editText.requestFocus();

        editTextBorder = rootView.findViewById(R.id.home_bar_border);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                fetchQueryResults(charSequence.toString() + "*");

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
        String token = Tools.getToken(context);

        if(token != null) {
            NetworkUtilities.FetchSearchResults fetchSearchResults = new NetworkUtilities
                    .FetchSearchResults(collection -> {
                contentAdapter.swapLiveCollection(collection);
            }, query, token);
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionType, KeyEvent keyEvent) {
        if(actionType == EditorInfo.IME_ACTION_SEARCH) {
            EditText editText = rootView.findViewById(R.id.home_bar_edit);
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
