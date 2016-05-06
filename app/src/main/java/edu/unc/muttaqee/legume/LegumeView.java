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
    public static int SIZE = 25;
    int red = 255;
    int blue = 0;
    int green = 0;

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
        paint.setColor(Color.rgb(red, blue, green)); // FIXME - ADJUST TO ACCEL DATA

        double percent;
        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);

            //percent = (double) Math.abs(p.cz) / MainActivity.getLegumeViewWidth();
            //Log.v("LOG-PERCENT", p.cz + ", " + percent);
            // int vala = (int) (255 * percent);
            // int valb = (int) (1 - ((double)i/SIZE)*255);
            //paint.setColor(Color.rgb((red * 255)/100 * valb, (blue * 255)/100 * valb, (green * 255)/100 *valb));
            int r = (red * 255)/100, g = (green * 255)/100, b = (blue * 255)/100;
            double percentage = (i/SIZE);
            paint.setColor(Color.rgb(r + (int)percentage*(255-r), g + (int)percentage*(255-g), b + (int)percentage*(255-b)));
            canvas.drawCircle(p.getCx(), p.getCy(), i * 5 + 50 /* p.cz / 4 */, paint);
        }
    }

    public void setRed(int red) {
        this.red = red;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }

    public void setGreen(int green) {
        this.green = green;
    }
}
