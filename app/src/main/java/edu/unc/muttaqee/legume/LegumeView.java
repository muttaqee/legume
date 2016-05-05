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
    public static int SIZE = 10;

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
        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            percent = (double) Math.abs(p.getCz()) / MainActivity.getLegumeViewWidth();
            Log.v("LOG-PERCENT", p.getCz() + ", " + percent);
            int vala = (int) (255 * percent);
            int valb = (int) ((i/SIZE)*255);
            paint.setColor(Color.rgb(valb, 255, valb));
            canvas.drawCircle(p.getCx(), p.getCy(), i * 5 + 50 /* p.cz / 4 */, paint);
        }
    }
}
