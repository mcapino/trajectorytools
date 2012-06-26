package cz.agents.alite.trajectorytools.graph.maneuver;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.jgrapht.graph.DefaultListenableGraph;

import cz.agents.alite.trajectorytools.graph.spatialwaypoint.SpatialWaypoint;
import cz.agents.alite.trajectorytools.util.Point;

public class ListenableManeuverGraph extends DefaultListenableGraph<SpatialWaypoint, Maneuver> implements ManeuverGraphInterface {
	private static final long serialVersionUID = 3428956208593195747L;

	double maxSpeed;

	public ListenableManeuverGraph(ManeuverGraph g) {
		super(g);
		maxSpeed = g.maxSpeed;
	}

	//
	// From ManeuverGraph
	//
    /* (non-Javadoc)
     * @see cz.agents.alite.trajectorytools.graph.maneuver.ManeuverGraphInterface#getDuration(cz.agents.alite.trajectorytools.graph.maneuver.Maneuver)
     */
    @Override
    public double getDuration(Maneuver m) {
        return getEdgeWeight(m);
    }
    
    /* (non-Javadoc)
     * @see cz.agents.alite.trajectorytools.graph.maneuver.ManeuverGraphInterface#getMaxSpeed()
     */
    @Override
    public double getMaxSpeed() {
		return maxSpeed;
	}

	//
	// From WaypointGraph
	//
    /* (non-Javadoc)
     * @see cz.agents.alite.trajectorytools.graph.maneuver.ManeuverGraphInterface#getEdgeNeighbor(cz.agents.alite.trajectorytools.graph.maneuver.Maneuver, cz.agents.alite.trajectorytools.graph.spatialwaypoint.SpatialWaypoint)
     */
    @Override
    public SpatialWaypoint getEdgeNeighbor(Maneuver edge, SpatialWaypoint waypoint) {
        if (getEdgeSource(edge) == waypoint)
            return getEdgeTarget(edge);
        if (getEdgeTarget(edge) == waypoint)
            return getEdgeSource(edge);

        return null;
    }

    /* (non-Javadoc)
     * @see cz.agents.alite.trajectorytools.graph.maneuver.ManeuverGraphInterface#getNearestWaypoint(cz.agents.alite.trajectorytools.util.Point)
     */
    @Override
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

    /* (non-Javadoc)
     * @see cz.agents.alite.trajectorytools.graph.maneuver.ManeuverGraphInterface#getOrderedNeighbors(cz.agents.alite.trajectorytools.graph.spatialwaypoint.SpatialWaypoint)
     */
    @Override
    public List<SpatialWaypoint> getOrderedNeighbors(SpatialWaypoint wp) {
        Set<Maneuver> edges = edgesOf(wp);
        List<SpatialWaypoint> neighbors = new LinkedList<SpatialWaypoint>();
        for (Maneuver edge : edges) {
            neighbors.add(getEdgeNeighbor(edge, wp));
        }
        Collections.sort(neighbors);
        return neighbors;
    }

    /* (non-Javadoc)
     * @see cz.agents.alite.trajectorytools.graph.maneuver.ManeuverGraphInterface#getRandomWaypoint(java.util.Random)
     */
    @Override
    public SpatialWaypoint getRandomWaypoint(Random random) {
        SpatialWaypoint[] waypoints = vertexSet().toArray(new SpatialWaypoint[0]);
        if (waypoints.length > 0) {
            return waypoints[random.nextInt(waypoints.length)];
        } else {
            return null;
        }
    }

}
