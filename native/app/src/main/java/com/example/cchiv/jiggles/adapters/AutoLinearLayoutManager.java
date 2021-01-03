package com.example.cchiv.jiggles.adapters;

import android.content.Context;
import android.graphics.PointF;
import android.os.Handler;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

public class AutoLinearLayoutManager extends LinearLayoutManager {

    private static final String TAG = "AutoLinearLayoutManager";

    private static final int SCROLL_SPEED = 800;
    private static final int SCROLL_PAUSE = 6000;

    private Handler handlerScroll = new Handler();
    private Runnable runnableScroll = null;

    private Handler handlerPause = new Handler();
    private Runnable runnablePause = null;

    public AutoLinearLayoutManager(Context context) {
        super(context);
    }

    public AutoLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public AutoLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        LinearSmoothScroller linearSmoothScroller =
                new LinearSmoothScroller(recyclerView.getContext()) {
                    private static final float MILLISECONDS_PER_INCH = 100f;

                    @Override
                    public PointF computeScrollVectorForPosition(int targetPosition) {
                        return AutoLinearLayoutManager.this
                                .computeScrollVectorForPosition(targetPosition);
                    }

                    @Override
                    protected float calculateSpeedPerPixel
                            (DisplayMetrics displayMetrics) {
                        return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
                    }
                };

        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }

    public void setOnTouchListener(RecyclerView recyclerView) {
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                if(e.getAction() == MotionEvent.ACTION_MOVE) {
                    if(runnablePause != null)
                        handlerPause.removeCallbacks(runnablePause);
                    else runnablePause = () -> setAutoScroll(recyclerView);

                    handlerPause.postDelayed(runnablePause, SCROLL_PAUSE);

                    if(runnableScroll != null)
                        handlerScroll.removeCallbacks(runnableScroll);
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {}

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
        });
    }

    public void removeAutoScroll() {
        if(runnableScroll != null)
            handlerScroll.removeCallbacks(runnableScroll);

        if(handlerPause != null)
            handlerPause.removeCallbacks(runnablePause);
    }

    public void setAutoScroll(RecyclerView recyclerView) {
        if(recyclerView == null)
            return;

        if(runnableScroll == null) {
            runnableScroll = new Runnable() {
                boolean flag = true;
                int count = 0;

                @Override
                public void run() {
                    if(count < getItemCount()) {
                        if(count == getItemCount() - 1) {
                            flag = false;
                        } else if(count == 0) {
                            flag = true;
                        }

                        if(flag)
                            count++;
                        else
                            count--;

                        recyclerView.smoothScrollToPosition(count);
                    } else {
                        count = 0;
                    }

                    handlerScroll.postDelayed(this, SCROLL_SPEED);
                }
            };
        }

        handlerScroll.postDelayed(runnableScroll, SCROLL_SPEED);
    }
}
