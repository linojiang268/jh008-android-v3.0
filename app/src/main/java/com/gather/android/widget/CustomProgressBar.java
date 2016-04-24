package com.gather.android.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;


/**
 * Created by Levi on 2015/8/4.
 */
public class CustomProgressBar extends Drawable {
    private static final int MAX = 1000;
    private int progress = 0;
    private Canvas mCanvas;
    @Override
    protected boolean onLevelChange(int level) {
        progress = level;
        draw(mCanvas);
        return super.onLevelChange(level);
    }

    @Override
    public void draw(Canvas canvas) {
        if (canvas == null)return;
        mCanvas = canvas;
        int width = getMinimumWidth();
        int height = getMinimumHeight();
        if (width > 0 && height > 0){
            int barWidth = width / 2;
            int barheight = 6;

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.argb(255, 50, 151, 92));
            paint.setStrokeWidth(1);

            Rect rectBg = new Rect();
            rectBg.left = (width - barWidth) / 2;
            rectBg.right = (width + barWidth) / 2;
            rectBg.top = (height - barheight) / 2;
            rectBg.bottom = (height + barheight) / 2;

            canvas.drawRect(rectBg, paint);

            Rect rectP = new Rect();
            rectP.left = rectBg.left;
            rectP.right = rectBg.left + barWidth * progress / MAX;
            rectP.bottom = rectBg.bottom;
            rectP.top = rectBg.top;

            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(rectP, paint);
        }
    }

    @Override
    public void setAlpha(int i) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }
}
