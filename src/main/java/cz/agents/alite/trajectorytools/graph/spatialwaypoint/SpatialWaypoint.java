package cz.agents.alite.trajectorytools.graph.spatialwaypoint;

import cz.agents.alite.trajectorytools.util.Point;

public class SpatialWaypoint extends Point implements Comparable<SpatialWaypoint> {
    private static final long serialVersionUID = -9146741978852925425L;

    public final int order;
    
    static int curOrder = 0;
    public SpatialWaypoint(int order, double x, double y) {
        super(x,y,0.0);
        this.order = order;
        if (order >= curOrder) {
            curOrder = order + 1;
        }
    }

    public SpatialWaypoint(double x, double y) {
        this(curOrder, x, y);
    }

    public SpatialWaypoint(Point point) {
        this(curOrder, point.x, point.y);
    }

    @Override
    public int compareTo(SpatialWaypoint other) {
        return order - other.order;
    }

    @Override
    public String toString() {
//        return "#" + order;
        return "#" + order + " "+ super.toString();
    }   
}
