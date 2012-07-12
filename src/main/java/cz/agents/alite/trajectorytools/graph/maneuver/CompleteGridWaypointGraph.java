package cz.agents.alite.trajectorytools.graph.maneuver;

import org.jgrapht.graph.DefaultWeightedEdge;

import cz.agents.alite.trajectorytools.graph.spatial.SpatialWaypoint;

public class CompleteGridWaypointGraph {
    
    private CompleteGridWaypointGraph() {}
    
    static public DefaultWaypointGraph create(double sizeX, double sizeY, int gridX, int gridY) {
        DefaultWaypointGraph graph = new DefaultWaypointGraph();
        SpatialWaypoint waypoints[][] = new SpatialWaypoint[gridX+1][gridY+1];
        int waypointCounter = 0;

        double xStep = sizeX/gridX;
        double yStep = sizeY/gridY;

        // Generate vertices
        for (int x=0; x <= gridX; x++) {
            for (int y=0; y <= gridX; y++) {
                SpatialWaypoint w = new SpatialWaypoint(waypointCounter++,x*xStep, y*yStep);
                waypoints[x][y] = w;
                graph.addVertex(w);
            }
        }

        // Generate edges of complete graph
        for (int x=0; x <= gridX; x++) {
            for (int y=0; y <= gridY; y++) {
                SpatialWaypoint v1 = waypoints[x][y];
                int starty = y+1;
                for (int x2=x; x2 <= gridX; x2++) {
                    for (int y2=starty; y2 <= gridY; y2++) {
                        SpatialWaypoint v2 = waypoints[x2][y2];
                        if (v1 != v2) {
                            DefaultWeightedEdge edge = graph.addEdge(v1, v2);
                            graph.setEdgeWeight(edge, v1.distance(v2));
                        }
                    }
                    starty = 0;
                }
            }
        }
        
        return graph;
    }
}
