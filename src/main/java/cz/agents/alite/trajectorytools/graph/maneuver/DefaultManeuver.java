package cz.agents.alite.trajectorytools.graph.maneuver;

import org.jgrapht.graph.DefaultWeightedEdge;

import cz.agents.alite.trajectorytools.graph.spatial.SpatialWaypoint;
import cz.agents.alite.trajectorytools.trajectory.Trajectory;

@SuppressWarnings("serial")
public class DefaultManeuver extends DefaultWeightedEdge {
    SpatialWaypoint source;
    SpatialWaypoint target;
    double duration;
    double distance;

    public DefaultManeuver(SpatialWaypoint source, SpatialWaypoint target, double duration) {
        super();
        this.source = source;
        this.target = target;
        this.duration = duration;
    }

    public SpatialWaypoint getSource() {
        return source;
    }

    public SpatialWaypoint getTarget() {
        return target;
    }

    public double getDuration() {
        return duration;
    }
    
    public double getDistance() {
        return duration;
    }
    
    public Trajectory getTrajectory(double time) {
    	// TODO
    	return null;
    }

    public String toString()
    {
        return "(" + source + " : " + target + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DefaultManeuver) {
            DefaultManeuver other = (DefaultManeuver) obj;
            return source.equals(other.source) && target.equals(other.target) && duration == other.duration;
        } else {
            return false;
        }
    }
}
