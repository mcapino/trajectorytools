package cz.agents.alite.trajectorytools.graph.maneuver;

import org.jgrapht.graph.DefaultWeightedEdge;

import cz.agents.alite.trajectorytools.graph.spatialwaypoint.SpatialWaypoint;

@SuppressWarnings("serial")
public class Maneuver extends DefaultWeightedEdge {
    SpatialWaypoint source;
    SpatialWaypoint target;
    double duration;

    public Maneuver(SpatialWaypoint source, SpatialWaypoint target, double duration) {
        super();
        this.source = source;
        this.target = target;
        this.duration = duration;
    }

    @Override
    public SpatialWaypoint getSource() {
        return source;
    }

    @Override
    public SpatialWaypoint getTarget() {
        return target;
    }

    public double getDuration() {
        return duration;
    }

    public SpatialWaypoint getOtherWaypoint(SpatialWaypoint waypoint) {
        assert waypoint == source || waypoint == target;

        if (source == waypoint)
            return target;
        if (target == waypoint)
            return source;

        return null;
    }

    @Override
    public String toString()
    {
        return "(" + source + " : " + target + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Maneuver) {
            Maneuver other = (Maneuver) obj;
            return source.equals(other.source) && target.equals(other.target) && duration == other.duration;
        } else {
            return false;
        }
    }
}
