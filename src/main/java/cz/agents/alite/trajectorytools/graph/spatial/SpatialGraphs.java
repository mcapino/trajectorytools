package cz.agents.alite.trajectorytools.graph.spatial;

import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import cz.agents.alite.trajectorytools.graph.spatial.maneuvers.SpatialManeuver;
import cz.agents.alite.trajectorytools.util.Point;

public class SpatialGraphs {
	public static <E> SpatialWaypoint getNearestWaypoint(Graph<SpatialWaypoint, E> graph, Point pos) {
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

	@SuppressWarnings({ "unchecked", "rawtypes" })    
    public static <E> Graph<SpatialWaypoint, E>  clone(Graph<SpatialWaypoint, E> other) {
		DirectedWeightedMultigraph<SpatialWaypoint, E> graph = new DirectedWeightedMultigraph(
	    	new EdgeFactory() {

				@Override
				public Object createEdge(Object sourceVertex,
						Object targetVertex) {
					throw new RuntimeException("Should not be reached");
				}}
    	);
        
        for (SpatialWaypoint vertex : other.vertexSet()) {
            graph.addVertex(vertex);
        }
        
        for (E edge : other.edgeSet()) {        	
            graph.addEdge(other.getEdgeSource(edge), other.getEdgeTarget(edge), edge);
        }
        
        return graph;
    } 
    
    
    
}
