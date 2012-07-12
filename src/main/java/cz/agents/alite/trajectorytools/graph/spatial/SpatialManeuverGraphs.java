package cz.agents.alite.trajectorytools.graph.spatial;

import cz.agents.alite.trajectorytools.graph.spatial.maneuvers.SpatialManeuver;
import cz.agents.alite.trajectorytools.util.Point;

public class SpatialManeuverGraphs {
	public static SpatialWaypoint getNearestWaypoint(SpatialManeuverGraph graph, Point pos) {
        SpatialWaypoint nearestWaypoint = null;
        double nearestDistance = Double.POSITIVE_INFINITY;
        for (SpatialWaypoint currentWaypoint : graph.vertexSet()) {
            double distance = currentWaypoint.distance(pos);
            if (distance < nearestDistance || nearestWaypoint == null) {
                nearestWaypoint = currentWaypoint; 
                nearestDistance = distance;
            }
        }

        return nearestWaypoint;
	}
	
    public static SpatialManeuverGraph clone(SpatialManeuverGraph other) {
        SpatialManeuverGraph graph = new DefaultSpatialManeuverGraph();
        
        for (SpatialWaypoint vertex : other.vertexSet()) {
            graph.addVertex(vertex);
        }
        
        for (SpatialManeuver edge : other.edgeSet()) {        	
            graph.addEdge(other.getEdgeSource(edge), other.getEdgeTarget(edge), edge);
        }
        
        return graph;
    }
	
}
