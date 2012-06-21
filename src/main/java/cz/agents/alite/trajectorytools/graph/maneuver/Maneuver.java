package cz.agents.alite.trajectorytools.graph.maneuver;

import org.jgrapht.graph.DefaultWeightedEdge;

import cz.agents.deconfliction.planner4d.State;
import cz.agents.deconfliction.waypointgraph.Waypoint;

@SuppressWarnings("serial")
public class Maneuver extends DefaultWeightedEdge {
    Waypoint source;
    Waypoint target;
    double duration;

    public Maneuver(Waypoint source, Waypoint target, double duration) {
        super();
        this.source = source;
        this.target = target;
        this.duration = duration;
    }

    public Waypoint getSource() {
        return source;
    }

    public Waypoint getTarget() {
        return target;
    }

    public double getDuration() {
        return duration;
    }

    public Waypoint getOtherWaypoint(Waypoint waypoint) {
        //assert waypoint == source || waypoint == target;

        if (source == waypoint)
            return target;
        if (target == waypoint)
            return source;

        return null;
    }

    public String toString()
    {
        return "(" + source + " : " + target + ")";
    }
}
