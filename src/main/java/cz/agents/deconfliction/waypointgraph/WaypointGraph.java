package cz.agents.deconfliction.waypointgraph;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import cz.agents.deconfliction.util.Point;

@SuppressWarnings("serial")
public class WaypointGraph<E> extends DirectedWeightedMultigraph<Waypoint,E> {


    public WaypointGraph(Class<? extends E> edgeClass) {
        super(edgeClass);
    }

    public WaypointGraph(EdgeFactory<Waypoint, E> ef) {
        super(ef);
    }

    public Waypoint getEdgeNeighbor(E edge, Waypoint waypoint) {
        if (getEdgeSource(edge) == waypoint)
            return getEdgeTarget(edge);
        if (getEdgeTarget(edge) == waypoint)
            return getEdgeSource(edge);

        return null;
    }

    public Waypoint getNearestWaypoint(Point pos) {
        Waypoint nearestWaypoint = null;
        double nearestDistance = Double.POSITIVE_INFINITY;
        for (Waypoint currentWaypoint : vertexSet()) {
            double distance = currentWaypoint.distance(pos);
            if (distance < nearestDistance || nearestWaypoint == null) {
                nearestWaypoint = currentWaypoint;
                nearestDistance = distance;
            }
        }

        return nearestWaypoint;
    }

    @SuppressWarnings("unchecked")
    public List<Waypoint> getOrderedNeighbors(Waypoint wp) {
        Set<E> edges = edgesOf(wp);
        List<Waypoint> neighbors = new LinkedList<Waypoint>();
        for (E edge : edges) {
            neighbors.add(getEdgeNeighbor(edge, wp));
        }
        Collections.sort(neighbors);
        return neighbors;
    }

    public Waypoint getRandomWaypoint(Random random) {
        Waypoint[] waypoints = vertexSet().toArray(new Waypoint[0]);
        if (waypoints.length > 0) {
            return waypoints[random.nextInt(waypoints.length)];
        } else {
            return null;
        }
    }
}
