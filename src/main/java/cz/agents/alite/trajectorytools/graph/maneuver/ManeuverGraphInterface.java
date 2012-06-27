package cz.agents.alite.trajectorytools.graph.maneuver;

import java.util.List;
import java.util.Random;

import org.jgrapht.WeightedGraph;

import cz.agents.alite.trajectorytools.graph.spatialwaypoint.SpatialWaypoint;
import cz.agents.alite.trajectorytools.util.Point;

public interface ManeuverGraphInterface extends WeightedGraph<SpatialWaypoint, DefaultManeuver> {

    //
    // From ManeuverGraph
    //
    public double getDuration(DefaultManeuver m);

    public double getMaxSpeed();

    //
    // From WaypointGraph
    //
    public SpatialWaypoint getEdgeNeighbor(DefaultManeuver edge,
            SpatialWaypoint waypoint);

    public SpatialWaypoint getNearestWaypoint(Point pos);

    public List<SpatialWaypoint> getOrderedNeighbors(SpatialWaypoint wp);

    public SpatialWaypoint getRandomWaypoint(Random random);

}