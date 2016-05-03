package edu.unc.muttaqee.legume;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.punchthrough.bean.sdk.Bean;
import com.punchthrough.bean.sdk.BeanDiscoveryListener;
import com.punchthrough.bean.sdk.BeanListener;
import com.punchthrough.bean.sdk.BeanManager;
import com.punchthrough.bean.sdk.message.Acceleration;
import com.punchthrough.bean.sdk.message.BeanError;
import com.punchthrough.bean.sdk.message.Callback;
import com.punchthrough.bean.sdk.message.ScratchBank;

public class MainActivity extends AppCompatActivity {

    Bitmap bitmap;
    Canvas canvas;
    static LegumeView lv;

    Bean bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // FIXME - remove
        bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);

        lv = (LegumeView) findViewById(R.id.canvas);

        BeanManager.getInstance().startDiscovery(bdl);

        //lv.invalidate(); // FIXME - put in another thread / asynctask
    }

    public static int getLegumeViewWidth() {
        return lv.getViewWidth();
    }

    BeanDiscoveryListener bdl = new BeanDiscoveryListener() {
        @Override
        public void onBeanDiscovered(Bean bean, int rssi) {
            Log.v("LOG-NOTE", "" + bean.getDevice() + ", " + rssi);
            MainActivity.this.bean = bean;
        }

        @Override
        public void onDiscoveryComplete() {
            if (bean != null) {
                bean.connect(getApplicationContext(), blsnr);
            } else {
                Log.v("LOG-ALERT", "bean is NULL");
            }
        }
    };

    BeanListener blsnr = new BeanListener() {
        @Override
        public void onConnected() {

            Log.v("LOG-NOTE", "We are connected to: " + bean.getDevice().getName());

            Toast toast = Toast.makeText(getApplicationContext(), "Connected!", Toast.LENGTH_SHORT);
            toast.show();

            new AsyncPlotPoints().execute();

//            // TODO - DON'T NEED THIS FIRST READING; JUST FOR SHOW
//            bean.readAcceleration(new Callback<Acceleration>() {
//                @Override
//                public void onResult(Acceleration result) {
//                    long t = System.currentTimeMillis();
//                    double x, y, z;
//                    x = result.x();
//                    y = result.y();
//                    z = result.z();
//                    Log.v("LOG-NOTE", t + "ms - " + x + ", " + y + ", " + z);
//                    Point p = new Point(t, x, y, z, null);
//                    lv.points.add(p);
//                    lv.invalidate();
//
//                    Log.v("LOG-NOTE", p.toString());
//                }
//            });
        }

        @Override
        public void onConnectionFailed() {

        }

        @Override
        public void onDisconnected() {

        }

        @Override
        public void onSerialMessageReceived(byte[] data) {

        }

        @Override
        public void onScratchValueChanged(ScratchBank bank, byte[] value) {

        }

        @Override
        public void onError(BeanError error) {

        }
    };

    private class AsyncPlotPoints extends AsyncTask<String, Void, String> {
        boolean loop = true;
        Point prev = null;

        @Override
        protected String doInBackground(String... params) {
            int i = 0;
            while (i < 300) {
                bean.readAcceleration(new Callback<Acceleration>() {
                    @Override
                    public void onResult(Acceleration result) {
                        long t = System.currentTimeMillis();
                        double x, y, z;
                        x = result.x();
                        y = result.y();
                        z = result.z();
                        Log.v("LOG-NOTE", t + "ms - " + x + ", " + y + ", " + z);
                        Point p = new Point(t, x, y, z, prev);
                        lv.points.add(p);
                        prev = p;

                        // TODO THINGY
                        if (prev.px > 0.5 || prev.px < -0.5) {
                            prev.px = 0.0;
                        }
                        if (prev.py > 0.5 || prev.py < -0.5) {
                            prev.py = 0.0;
                        }
                        if (prev.pz > 0.5 || prev.pz < 0.2) {
                            prev.pz = 0.0;
                        }

                        if (lv.points.size() > 5) {
                            lv.points.remove(0);
                        }

                        lv.invalidate();

                        Log.v("LOG-NOTE", p.toString() + " time: " + p.timeStamp + " position: " + p.px + ", " + p.py + ", " + p.pz);
                    }
                });
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                i++;
            }
            return "Executed";
        };
    }

    // TODO - Click event listener to handle button clicks


}
