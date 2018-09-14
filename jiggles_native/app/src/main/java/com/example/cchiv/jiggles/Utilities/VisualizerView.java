package com.example.cchiv.jiggles.Utilities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.example.cchiv.jiggles.R;

public class VisualizerView extends View {

    public static final String TAG = "VisualizerView";

    private Context context;

    private static final Float THRESHOLD = 8.0f;

    private byte[] data = null;
    private int width, height;
    private Paint paint = new Paint();

    private int clearColor;

    public VisualizerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.context = context;

        clearColor = ContextCompat.getColor(context, R.color.primaryTextColor);

        paint.setColor(ContextCompat.getColor(context, R.color.iconsTextColor));
        paint.setStrokeWidth(1.0f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(clearColor);

        if(data != null) {
            float length = data.length / THRESHOLD;

            float partitionSymX = (float) (width / 2.0f) / length;
            float partitionY = (float) (height - 128.0f) / 256.0f;

            float meanHeight = height / 2.0f;

            for(int it = 0; it < length; it += 2) {
                float freq1 = (float) Math.hypot(data[it], data[it+1]);
                float freq2 = (float) Math.hypot(data[it+2], data[it+3]);

                float height1 = meanHeight - freq1 * partitionY;
                float height2 = meanHeight - freq2 * partitionY;

                canvas.drawLine(partitionSymX * (it + length),
                        height1,
                        partitionSymX * (it + 3 + length),
                        height2,
                        paint);

                canvas.drawLine(partitionSymX * (-it + length),
                        height1,
                        partitionSymX * (-it - 2 + length),
                        height2,
                        paint);
            }

            invalidate();
        }
    }

    public void onUpdateFftData(byte[] fft) {
        data = fft;

        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        width = getWidth();
        height = getHeight();
    }
}
