package edu.unc.muttaqee.legume;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class LegumeView extends View {
    ArrayList<Point> points;
    public static int SIZE = 20;

    public LegumeView(Context context) {
        super(context);
        points = new ArrayList<>(SIZE);
    }

    public LegumeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        points = new ArrayList<>(SIZE);
    }

    public LegumeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        points = new ArrayList<>(SIZE);
    }

    public LegumeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        points = new ArrayList<>(SIZE);
    }

    public int getViewWidth() {
        return this.getWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.rgb(0, 255, 0)); // FIXME - ADJUST TO ACCEL DATA

        double percent;
        for (Point p : points) {
            percent = (double) Math.abs(p.cz) / MainActivity.getLegumeViewWidth();
            Log.v("LOG-PERCENT", p.cz + ", " + percent);
            int val = (int) (255 * percent);
            paint.setColor(Color.rgb(val, 255, val));
            canvas.drawCircle(p.cx, p.cy, 50 /* p.cz / 4 */, paint);
        }
    }
}
