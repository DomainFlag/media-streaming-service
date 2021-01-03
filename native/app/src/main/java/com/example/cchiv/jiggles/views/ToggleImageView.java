package com.example.cchiv.jiggles.views;

import android.content.Context;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;

public class ToggleImageView extends AppCompatImageView implements View.OnClickListener {

    public interface OnActiveCallback {
        void onActiveCallback(View view, boolean isActive);
    }

    private OnActiveCallback onActiveCallback = null;
    private boolean active = false;

    public ToggleImageView(Context context) {
        super(context);

        setOnClickListener(this);
        setAlpha(0.4f);
    }

    public ToggleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOnClickListener(this);
        setAlpha(0.4f);
    }

    public ToggleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOnClickListener(this);
        setAlpha(0.4f);
    }

    public void setOnActiveCallback(OnActiveCallback onActiveCallback) {
        this.onActiveCallback = onActiveCallback;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void onClick(View view) {
        active = !active;

        if(active) {
            setAlpha(1.0f);
        } else {
            setAlpha(0.4f);
        }

        if(onActiveCallback != null)
            onActiveCallback.onActiveCallback(view, active);
    }
}
