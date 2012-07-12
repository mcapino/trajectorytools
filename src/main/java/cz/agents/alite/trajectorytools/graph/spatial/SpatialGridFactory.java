package cz.agents.alite.trajectorytools.graph.spatial;

import cz.agents.alite.trajectorytools.graph.spatial.maneuvers.SpatialManeuver;
import cz.agents.alite.trajectorytools.graph.spatial.maneuvers.Straight;
import cz.agents.alite.trajectorytools.graph.spatial.maneuvers.Wait;

public class SpatialGridFactory {
	   public static SpatialManeuverGraph createNWayGrid(double sizeX, double sizeY, int gridX, int gridY, double speed, int[][] edgePattern, boolean allowWaitManeuver) {
		    SpatialManeuverGraph graph 	= new TimeWeightedSpatialManeuverGraph(); 
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
	                    SpatialManeuver wait = new Wait(w, xStep/speed);
	                	graph.addEdge(w, w, wait);
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
		                                SpatialManeuver maneuverForward = new Straight(v1, v2, speed);
		                                graph.addEdge(v1, v2, maneuverForward);
	                        	    }
	                        	    
	                        	    if (!graph.containsEdge(v2, v1)) {                                
		                                SpatialManeuver maneuverBack = new Straight(v2, v1, speed);
		                                graph.addEdge(v2, v1, maneuverBack);
	                        	    }
	                         }
	                     }
	                }
	            }
	        return graph;
	    }
	   



	static public SpatialManeuverGraph create4WayGrid(double sizeX, double sizeY,
			int gridX, int gridY, double speed) {
		
	    final int[][] EDGE_PATTERN = {           {0,-1},
			                             {-1, 0},         { 1, 0},
					                              {0, 1},          };

		
		return createNWayGrid(sizeX, sizeY, gridX, gridY, speed, EDGE_PATTERN, true);
	}
	
	static public SpatialManeuverGraph create8WayGrid(double sizeX, double sizeY,
			int gridX, int gridY, double speed) {
		
	    final int[][] EDGE_PATTERN = {{-1,-1}, {0,-1}, { 1,-1},
									   {-1, 0},         { 1, 0},
									   {-1, 1}, {0, 1}, { 1, 1}};

		
		return createNWayGrid(sizeX, sizeY, gridX, gridY, speed, EDGE_PATTERN, true);
	}

	
}

