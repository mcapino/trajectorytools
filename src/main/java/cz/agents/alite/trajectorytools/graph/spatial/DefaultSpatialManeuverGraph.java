package cz.agents.alite.trajectorytools.graph.spatial;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import cz.agents.alite.trajectorytools.graph.spatial.maneuvers.SpatialManeuver;
import cz.agents.alite.trajectorytools.util.Point;

@SuppressWarnings("serial")
public class DefaultSpatialManeuverGraph extends SpatialManeuverGraph {


    public DefaultSpatialManeuverGraph() {
        super(SpatialManeuver.class);
    }

    public SpatialWaypoint getEdgeNeighbor(SpatialManeuver edge, SpatialWaypoint waypoint) {
        if (getEdgeSource(edge) == waypoint)
            return getEdgeTarget(edge);
        if (getEdgeTarget(edge) == waypoint)
            return getEdgeSource(edge);

        return null;
    }

    public SpatialWaypoint getNearestWaypoint(Point pos) {
        SpatialWaypoint nearestWaypoint = null;
        double nearestDistance = Double.POSITIVE_INFINITY;
        for (SpatialWaypoint currentWaypoint : vertexSet()) {
            double distance = currentWaypoint.distance(pos);
            if (distance < nearestDistance || nearestWaypoint == null) {
                nearestWaypoint = currentWaypoint;
                nearestDistance = distance;
            }
        }

        return nearestWaypoint;
    }

    public List<SpatialWaypoint> getOrderedNeighbors(SpatialWaypoint wp) {
        Set<SpatialManeuver> edges = edgesOf(wp);
        List<SpatialWaypoint> neighbors = new LinkedList<SpatialWaypoint>();
        for (SpatialManeuver edge : edges) {
            neighbors.add(getEdgeNeighbor(edge, wp));
        }
        Collections.sort(neighbors);
        return neighbors;
    }

    public SpatialWaypoint getRandomWaypoint(Random random) {
        SpatialWaypoint[] waypoints = vertexSet().toArray(new SpatialWaypoint[0]);
        if (waypoints.length > 0) {
            return waypoints[random.nextInt(waypoints.length)];
        } else {
            return null;
        }
    }
}
