package cz.agents.alite.trajectorytools.graph.spatialwaypoint;

import cz.agents.alite.trajectorytools.util.Point;

public class SpatialWaypoint extends Point implements Comparable{
    public final int order;
    public SpatialWaypoint(int order, double x, double y) {
        super(x,y,0.0);
        this.order = order;
    }

    @Override
    public int compareTo(Object arg0) {
        if (arg0 instanceof SpatialWaypoint) {
            return order - ((SpatialWaypoint)arg0).order;
        }
        return 0;
    }

    public String toString() {
        return "#" + order;
        //return "#" + order + " "+ super.toString();
    }   
    
}
