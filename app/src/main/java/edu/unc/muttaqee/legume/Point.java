package edu.unc.muttaqee.legume;

public class Point {

    public static double vmax = 0.15, vmin = -0.15;

    private long timeStamp;

    // Physical values
    private double px, py, pz; // pz is weight on canvas
    private double vx, vy, vz;

    // Canvas values range [0, MainActivity.getLegumeViewWidth()]
    private int cx, cy, cz;

    // Last point
    private Point prev;

    public Point(long t, double ax, double ay, double az, Point prev) {
        this.timeStamp = t;
        if (prev == null) {
            px = py = pz = 0.0;
            vx = vy = vz = 0.0;
        } else {
            double delta_t = 0.15;
            // Using previous point, convert accel data values (m/s^2) to position values (m)
            this.vx = ax * delta_t + prev.vx;
            this.vy = ay * delta_t + prev.vy;
            this.vz = az * delta_t + prev.vz;

            // Bound velocity values
            if (this.vx > this.vmax) {
                this.vx = this.vmax;
            } else if (this.vx < this.vmin) {
                this.vx = this.vmin;
            }
            if (this.vy > this.vmax) {
                this.vy = this.vmax;
            } else if (this.vy < this.vmin) {
                this.vy = this.vmin;
            }
            if (this.vz > this.vmax) {
                this.vz = this.vmax;
            } else if (this.vz < this.vmin) {
                this.vz = this.vmin;
            }

            this.px = this.vx * delta_t + prev.px;
            this.py = this.vy * delta_t + prev.py;
            this.pz = this.vz * delta_t + prev.pz;

        }
        // Map position values to canvas (x, y, z)
        this.cx = this.getCanvasCoordFromPositionVal(px);
        this.cy = this.getCanvasCoordFromPositionVal(py);
        this.cz = this.getCanvasCoordFromPositionVal(pz);
    }

    //Correct position - fix within [-0.5, 0.5] range
    private static double correctPositionValue(double p) {
        if (p < -0.5) {
            return -0.5;
        } else if (p > 0.5) {
            return 0.5;
        } else {
            return p;
        }
    }

    public long getTimeStamp(){
        return this.timeStamp;
    }

    public double getPx(){
        return px;
    }

    public double getPy(){
        return py;
    }

    public double getPz() {
        return pz;
    }

    public int getCx(){
        return cx;
    }

    public int getCy(){
        return cy;
    }

    public int getCz() {
        return cz;
    }

    public void setPx(double n){
        px = n;
    }
    public void setPy(double n){
        py = n;
    }
    public void setPz(double n){
        pz = n;
    }

    public static int getCanvasCoordFromPositionVal(double p) {
        return (int) ((correctPositionValue(p) + 0.5) * MainActivity.getLegumeViewWidth());
    }

    @Override
    public String toString() {
        return "(" + cx + ", " + cy + ", " + cz + ")";
    }
}
