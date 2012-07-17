package cz.agents.alite.trajectorytools.graph.spatial;

import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import cz.agents.alite.trajectorytools.graph.spatial.maneuvers.SpatialManeuver;
import cz.agents.alite.trajectorytools.util.Point;
import cz.agents.alite.trajectorytools.util.Waypoint;

public class SpatialGraphs {
	public static <E> Waypoint getNearestWaypoint(Graph<Waypoint, E> graph, Point pos) {
        Waypoint nearestWaypoint = null;
        double nearestDistance = Double.POSITIVE_INFINITY;
        for (Waypoint currentWaypoint : graph.vertexSet()) {
            double distance = currentWaypoint.distance(pos);
            if (distance < nearestDistance || nearestWaypoint == null) {
                nearestWaypoint = currentWaypoint; 
                nearestDistance = distance;
            }
        }

        return nearestWaypoint;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })    
    public static <E> Graph<Waypoint, E>  clone(Graph<Waypoint, E> other) {
		DirectedWeightedMultigraph<Waypoint, E> graph = new DirectedWeightedMultigraph(
	    	new EdgeFactory() {

				@Override
				public Object createEdge(Object sourceVertex,
						Object targetVertex) {
					throw new RuntimeException("Should not be reached");
				}}
    	);
        
        for (Waypoint vertex : other.vertexSet()) {
            graph.addVertex(vertex);
        }
        
        for (E edge : other.edgeSet()) {        	
            graph.addEdge(other.getEdgeSource(edge), other.getEdgeTarget(edge), edge);
        }
        
        return graph;
    } 
    
    
    
}
