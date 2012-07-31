package cz.agents.alite.trajectorytools.util;

import javax.vecmath.Point4d;

public class TimePoint extends Point4d {
    private static final long serialVersionUID = 1136064568843307511L;

    public TimePoint(double x, double y, double z, double time) {
        super(x,y,z,time);
    }

    public TimePoint(Point p, double time) {
        super(p.x,p.y,p.z,time);
    }

    public Point getPoint3d() {
        return new Point(x,y,z);
    }

    public double getTime() {
        return w;
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
