package cz.agents.alite.trajectorytools.graph.maneuver;

import cz.agents.alite.trajectorytools.graph.spatialwaypoint.SpatialWaypoint;

public class CopyManeuverGraph {
    
    private CopyManeuverGraph() {}

    public static ManeuverGraph create(ManeuverGraphInterface other) {
        ManeuverGraph graph = new ManeuverGraph(other.getMaxSpeed(), other.getEdgeFactory());
        
        for (SpatialWaypoint vertex : other.vertexSet()) {
            graph.addVertex(vertex);
        }
        
        for (Maneuver edge : other.edgeSet()) {
            graph.addEdge(edge.source, edge.target);
        }
        
        return graph;
    }
}
