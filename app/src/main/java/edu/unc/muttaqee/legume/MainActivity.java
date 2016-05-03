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

    // Determine how to offset accelerometer readings
    private class AsyncCalibrate extends AsyncTask<String, Void, String> {
        double avgx = 0, avgy = 0, avgz = 0;
        @Override
        protected String doInBackground(String... params) {
            final int TIMES_TO_SAMPLE = 30;
            // Put bean on a flat surface // TODO NOTIFICATION
            // Sum accelerometer values
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int i = 0;
            while (i < TIMES_TO_SAMPLE) {
                bean.readAcceleration(new Callback<Acceleration>() {
                    @Override
                    public void onResult(Acceleration result) {
                        avgx += result.x();
                        avgy += result.y();
                        avgz += result.z();
                    }
                });
                i++;
            }
            // Compute average of accelerometer values
            avgx /= TIMES_TO_SAMPLE;
            avgy /= TIMES_TO_SAMPLE;
            avgz /= TIMES_TO_SAMPLE;
            return null;
        }
    }

//    private class AsyncPlotPointPairs extends AsyncTask<String, Void, String> {
//        final int NUM_POINTS = 100;
//        double x, y, z;
//        Acceleration accel_0 = null, accel_1 = null;
//        int delta_t = 50; // Milliseconds
//
//        @Override
//        protected double[] doInBackground(String... params) {
//            int i = 0;
//            while (i < NUM_POINTS) {
//                bean.readAcceleration(new Callback<Acceleration>() {
//                    @Override
//                    public void onResult(Acceleration result) {
//                        accel_0 = result;
//                    }
//                });
//                try {
//                    Thread.sleep(delta_t);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                bean.readAcceleration(new Callback<Acceleration>() {
//                    @Override
//                    public void onResult(Acceleration result) {
//                        accel_1 = result;
//                    }
//                });
//                i++;
//            }
//            x = (accel_1.x() * delta_t + 0) * delta_t;
//            y = (accel_1.y() * delta_t + 0) * delta_t;
//            z = (accel_1.z() * delta_t + 0) * delta_t;
//            return new double[] {x, y, z};
//        }
//    }

    private class AsyncPlotPoints extends AsyncTask<String, Void, String> {
        boolean loop = true;
        Point prev = null;

        double max_x, min_x, max_y, min_y, max_z, min_z;

        @Override
        protected String doInBackground(String... params) {
            int i = 0;

            max_x = min_x = 0.0;
            max_y = min_y = 0.0;
            max_z = min_z = 0.0;

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

                        if (x > max_x) {
                            max_x = x;
                        }
                        if (x < min_x) {
                            min_x = x;
                        }
                        if (y > max_y) {
                            max_y = y;
                        }
                        if (y < min_y) {
                            min_y = y;
                        }
                        if (z > max_z) {
                            max_z = z;
                        }
                        if (z < min_z) {
                            min_z = z;
                        }

//                        // Report max
//                        String report = "";
//                        if (Math.abs(x) > Math.abs(y)) {
//                            if (Math.abs(x) > Math.abs(z)) {
//                                report = "X IS MAX";
//                                if (x > 0) {
//                                    report += "-positive";
//                                } else {
//                                    report += "-negative";
//                                }
//                            } else {
//                                report = "Z IS MAX";
//                                if (z > 0) {
//                                    report += "-positive";
//                                } else {
//                                    report += "-negative";
//                                }
//                            }
//                        } else {
//                            if (Math.abs(y) > Math.abs(z)) {
//                                report = "Y IS MAX";
//                                if (y > 0) {
//                                    report += "-positive";
//                                } else {
//                                    report += "-negative";
//                                }
//                            } else {
//                                report = "Z IS MAX";
//                                if (z > 0) {
//                                    report += "-positive";
//                                } else {
//                                    report += "-negative";
//                                }
//                            }
//                        }
//                        Log.v("LOG-NOTE", report);

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
                        if (prev.pz > 0.5 || prev.pz < -0.5) {
                            prev.pz = 0.0;
                        }

                        if (lv.points.size() > 1) {
                            lv.points.remove(0);
                        }

                        lv.invalidate();

                        Log.v("LOG-NOTE", p.toString() + " time: " + p.timeStamp + " position: " + p.px + ", " + p.py + ", " + p.pz);
                    }
                });
                try {
                    Thread.sleep(150);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                i++;
            }

            //FIXME remove
            Log.v("LOG-NOTE", max_x + " " + min_x + " " + max_y + " " + min_y + " " + max_z + " " + min_z);
            return "Executed";
        }
    }

    // TODO - Click event listener to handle button clicks


}
