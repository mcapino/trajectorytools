package cz.agents.alite.trajectorytools.graph.spatial;

import cz.agents.alite.trajectorytools.graph.spatial.maneuvers.SpatialManeuver;

public class SpatialManeuverGraphs {

    public static SpatialManeuverGraph  clone(SpatialManeuverGraph other) {
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
