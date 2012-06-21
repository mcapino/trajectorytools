package cz.agents.deconfliction.waypointgraph;

import cz.agents.deconfliction.util.Point;

public class Waypoint extends Point implements Comparable{
    public final int order;
    public Waypoint(int order, double x, double y) {
        super(x,y,0.0);
        this.order = order;
    }

    @Override
    public int compareTo(Object arg0) {
        if (arg0 instanceof Waypoint) {
            return order - ((Waypoint)arg0).order;
        }
        return 0;
    }

    public String toString() {
        return "#" + order;
        //return "#" + order + " "+ super.toString();
    }   
    
}
