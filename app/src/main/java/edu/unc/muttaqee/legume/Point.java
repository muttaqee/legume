package edu.unc.muttaqee.legume;

public class Point {

    public static double vmax = 0.1, vmin = -0.1;

    long timeStamp;

    // Physical values
    double px, py, pz; // pz is weight on canvas
    double vx, vy, vz;

    // Canvas values range [0, MainActivity.getLegumeViewWidth()]
    int cx, cy, cz;

    // Last point
    Point prev;

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
        cx = this.getCanvasCoordFromPositionVal(px);
        cy = this.getCanvasCoordFromPositionVal(py);
        cz = this.getCanvasCoordFromPositionVal(pz);
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

    public static int getCanvasCoordFromPositionVal(double p) {
        return (int) ((correctPositionValue(p) + 0.5) * MainActivity.getLegumeViewWidth());
    }

    @Override
    public String toString() {
        return "(" + cx + ", " + cy + ", " + cz + ")";
    }
}
