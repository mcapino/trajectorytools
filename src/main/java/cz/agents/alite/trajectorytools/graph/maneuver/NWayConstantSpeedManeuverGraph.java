package cz.agents.alite.trajectorytools.graph.maneuver;

import cz.agents.alite.trajectorytools.graph.spatial.SpatialWaypoint;

public class NWayConstantSpeedManeuverGraph {
    
    private NWayConstantSpeedManeuverGraph() {}

    public static ManeuverGraph create(double sizeX, double sizeY, int gridX, int gridY, double speed, int[][] edgePattern) {
        return create(sizeX, sizeY, gridX, gridY, speed, edgePattern, true);
    }

    public static ManeuverGraph create(double sizeX, double sizeY, int gridX, int gridY, double speed, int[][] edgePattern, boolean allowWaitManeuver) {
        ManeuverGraph graph = new ManeuverGraph(speed, new ManeuverEdgeFactory(speed, (sizeX/gridX)/(speed*2)));
        SpatialWaypoint waypoints[][] = new SpatialWaypoint[gridX+1][gridY+1];
        int waypointCounter = 0;

        double xStep = sizeX/gridX;
        double yStep = sizeY/gridY;

        // Generate vertices
        for (int x=0; x <= gridX; x++) {
            for (int y=0; y <= gridY; y++) {
                SpatialWaypoint w = new SpatialWaypoint(waypointCounter++, x*xStep, y*yStep);
                waypoints[x][y] = w;
                graph.addVertex(w);
                if (allowWaitManeuver) {
                    DefaultManeuver maneuver = graph.addEdge(w, w);
                    if (maneuver != null)
                        graph.setEdgeWeight(maneuver, maneuver.getDuration());
                }
                
            }
        }

        // Generate edges
        for (int x=0; x <= gridX; x++) {
            for (int y=0; y <= gridY; y++) {
                SpatialWaypoint v1 = waypoints[x][y];
                     for (int[] edgeOffset : edgePattern) {
                         int destX = x + edgeOffset[0];
                         int destY = y + edgeOffset[1];

                         if (destX >= 0 && destX <= gridX && destY >= 0 && destY <= gridY) {                                
                        	    SpatialWaypoint v2 = waypoints[destX][destY];
                        	    
                        	    if (!graph.containsEdge(v1, v2)) {                        	    
	                                DefaultManeuver maneuverForward = graph.addEdge(v1, v2);
	                                if (maneuverForward != null)
	                                    graph.setEdgeWeight(maneuverForward, maneuverForward.getDuration());
                        	    }
                        	    
                        	    if (!graph.containsEdge(v2, v1)) {                                
	                                DefaultManeuver maneuverBack = graph.addEdge(v2, v1);
	                                if (maneuverBack != null)
	                                    graph.setEdgeWeight(maneuverBack, maneuverBack.getDuration());
                        	    }
                         }
                     }
                }
            }
        return graph;
    }
}
