package edu.unc.muttaqee.legume;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.punchthrough.bean.sdk.Bean;
import com.punchthrough.bean.sdk.BeanDiscoveryListener;
import com.punchthrough.bean.sdk.BeanListener;
import com.punchthrough.bean.sdk.BeanManager;
import com.punchthrough.bean.sdk.message.Acceleration;
import com.punchthrough.bean.sdk.message.BeanError;
import com.punchthrough.bean.sdk.message.Callback;
import com.punchthrough.bean.sdk.message.ScratchBank;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    Bitmap bitmap;
    Canvas canvas;
    static LegumeView lv;

    int counter = 0;
    Point pointTrace[] = new Point[1000];
    Point traceArray[][] = new Point[80][1000];

    boolean running = false;
    Button start, stop;
    SeekBar red, blue, green;

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

        start = (Button) findViewById(R.id.startButton);
        start.setEnabled(false);

        stop = (Button) findViewById(R.id.stopButton);
        stop.setEnabled(false);


        TabHost host = (TabHost) findViewById(R.id.tabHost);
        host.setup();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("About");
        spec.setContent(R.id.About);
        spec.setIndicator("About");
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Sandbox");
        spec.setContent(R.id.Sandbox);
        spec.setIndicator("Sandbox");
        host.addTab(spec);

        red = (SeekBar) findViewById(R.id.seekBarRed);
        blue = (SeekBar) findViewById(R.id.seekBarBlue);
        green = (SeekBar) findViewById(R.id.seekBarGreen);
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
            start.setEnabled(true);

            //new AsyncPlotPoints().execute();

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

    /*private class AsyncPlotPointPairs extends AsyncTask<String, Void, String> {
        final int NUM_POINTS = 100;
        double x, y, z;
        Acceleration accel_0 = null, accel_1 = null;
        int delta_t = 50; // Milliseconds

        @Override
        protected double[] doInBackground(String... params) {
            int i = 0;
            while (i < NUM_POINTS) {
                bean.readAcceleration(new Callback<Acceleration>() {
                    @Override
                    public void onResult(Acceleration result) {
                        accel_0 = result;
                    }
                });
                try {
                    Thread.sleep(delta_t);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                bean.readAcceleration(new Callback<Acceleration>() {
                    @Override
                    public void onResult(Acceleration result) {
                        accel_1 = result;
                    }
                });
                i++;
            }
            x = (accel_1.x() * delta_t + 0) * delta_t;
            y = (accel_1.y() * delta_t + 0) * delta_t;
            z = (accel_1.z() * delta_t + 0) * delta_t;
            return new double[] {x, y, z};
        }
    }*/

    private class AsyncPlotPoints extends AsyncTask<String, Void, String> {
        boolean loop = true;
        Point prev = null;

        double max_x, min_x, max_y, min_y, max_z, min_z;

        @Override
        protected String doInBackground(String... params) {
            counter = 0;

            max_x = min_x = 0.0;
            max_y = min_y = 0.0;
            max_z = min_z = 0.0;


            while (running) {
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
                        pointTrace[counter] = p;
                        prev = p;

                        // TODO THINGY
                        if (prev.getPx() > 0.5) {
                            prev.setPx(0.5);
                        } else if (prev.getPx() < -0.5) {
                            prev.setPx(-0.5);
                        }
                        if (prev.getPy() > 0.5) {
                            prev.setPy(0.5);
                        } else if (prev.getPy() < -0.5) {
                            prev.setPy(-0.5);
                        }
                        if (prev.getPz() > 0.5) {
                            prev.setPz(0.5);
                        } else if (prev.getPz() < -0.5) {
                            prev.setPz(-0.5);
                        }

                        // TAIL
                        if (lv.points.size() > 25) {
                            lv.points.remove(0);
                        }

                        lv.setRed(red.getProgress());
                        lv.setBlue(blue.getProgress());
                        lv.setGreen(green.getProgress());

                        lv.invalidate();

                        Log.v("LOG-NOTE", p.toString() + " time: " + p.getTimeStamp() + " position: " + p.getPx() + ", " + p.getPy() + ", " + p.getPz());
                    }
                });
                try {
                    Thread.sleep(150);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                counter++;
            }

            //FIXME remove
            Log.v("LOG-NOTE", max_x + " " + min_x + " " + max_y + " " + min_y + " " + max_z + " " + min_z);
            return "Executed";
        }
    }

    class PostTrace extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... uri) {
            try {
                URL url = new URL("site.html");
                HttpURLConnection urlConnection = new HttpURLConnection(url) {
                    @Override
                    public void connect() throws IOException {
                        this.setRequestProperty("Content-Type", "application/json");
                        this.setRequestProperty("Accept", "*/*");
                        this.setRequestMethod("POST");
                        OutputStream outputStream = this.getOutputStream();
                        //serialize pointTrace array to string
                        outputStream.write(getJSON(pointTrace).getBytes("UTF-8"));
                    }

                    @Override
                    public void disconnect() {
                    }

                    @Override
                    public boolean usingProxy() {
                        return false;
                    }
                };
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }

        //middle one is for onprogressupdate
        class GetTraces extends AsyncTask<String, String, String> {
            @Override
            protected String doInBackground(String... uri) {
                try {
                    URL url = new URL("site.html");
                    HttpURLConnection urlConnection = new HttpURLConnection(url) {
                        @Override
                        public void connect() throws IOException {
                            this.setRequestProperty("Accept", "*/*");
                            this.setRequestMethod("GET");
                        }

                        @Override
                        public void disconnect() {
                        }

                        @Override
                        public boolean usingProxy() {
                            return false;
                        }
                    };
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    return inputStreamString(in, "UTF-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "failed";
            }

            @Override
            protected void onPostExecute(String result) {
                if (result.equals("failed")) {
                    //inform user of failure
                } else {
                    //deserialize
                    try {
                        JSONObject downloads = new JSONObject(result);
                        ObjectMapper mapper = new ObjectMapper();
                        for (int i = 0; i < downloads.length(); i++) {
                            //get the i'th pointTrace JSON
                            JSONObject download = downloads.getJSONObject(String.valueOf(i));
                            String jsonInString = download.toString();
                            Point[] trace = mapper.readValue(jsonInString, Point[].class);
                            //row,col
                            for (int col = 0; col < 1000; col++) {
                                traceArray[i][col] = trace[col];
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // TODO - Click event listener to handle button clicks

        //populate list of downloaded traces
        public void populateTraceList() {
            //TODO
        }

        //load a trace upon selection from TraceList
        public void loadForPlayback() {
            //TODO
        }

        //clear the trace "recording" (possibly unnecessary)
        public void clearTrace() {
            Arrays.fill(pointTrace, null);
        }

        //sends trace recording to server
        public void uploadTrace() {
            new PostTrace().execute();
        }

        //download user's traces from server
        public void downloadTraces() {
            new GetTraces().execute();
        }

        //return a point trace from a trace array, by row
        public Point[] getTraceByRow(Point[][] array, int row) {
            Point[] trace = new Point[1000];
            for (int i = 0; i < 1000; i++) {
                trace[i] = array[row][i];
            }
            return trace;
        }

        //convert array to JSON String
        public String getJSON(Point[] array) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.writeValueAsString(array);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Converting Array to String failed";
        }

        //convert input stream to string, with given encoding
        public String inputStreamString(InputStream inputStream, String encoding) {
            StringWriter writer = new StringWriter();
            try {
                IOUtils.copy(inputStream, writer, encoding);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return writer.toString();
        }
    }

    public void startMovement(View v) {
        running = true;
        new AsyncPlotPoints().execute();
        start.setEnabled(false);
        stop.setEnabled(true);
    }

    public void stopMovement(View v) {
        running = false;
        start.setEnabled(true);
        stop.setEnabled(false);
        lv.points.clear();
        lv.invalidate();
    }
}
