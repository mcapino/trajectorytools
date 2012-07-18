package cz.agents.alite.trajectorytools.util;

public class TimePoint extends Point {
    private static final long serialVersionUID = 1136064568843307511L;

    double time;

    public TimePoint(double x, double y, double z, double time) {
        super(x, y, z);
        this.time = time;
    }

    public TimePoint(Point p, double time) {
        super(p);
        this.time = time;
    }

    public double getTime() {
        return time;
    }

    @Override
    public String toString() {
        return super.toString() + " @ " + time +"";
    }

}
