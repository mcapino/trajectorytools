package cz.agents.alite.trajectorytools.graph.maneuver;

import org.jgrapht.graph.DefaultWeightedEdge;

import sun.security.action.GetLongAction;

import cz.agents.deconfliction.waypointgraph.DefaultWaypointGraph;
import cz.agents.deconfliction.waypointgraph.Waypoint;

@SuppressWarnings("serial")
public abstract class NWayConstantSpeedManeuverGraph extends ManeuverGraph {
    Waypoint waypoints[][];
    double speed;

    protected abstract int[][] getEdgePattern();
    
    protected boolean allowWaitManeuver() {
    	return true;
    }
    
    public NWayConstantSpeedManeuverGraph(double sizeX, double sizeY, int gridX, int gridY, double speed) {
        super(speed, new ManeuverEdgeFactory(speed, (sizeX/gridX)/(speed*2)));
        waypoints = new Waypoint[gridX+1][gridY+1];
        int waypointCounter = 0;

        double xStep = sizeX/gridX;
        double yStep = sizeY/gridY;

        this.speed = speed;

        // Generate vertices
        for (int x=0; x <= gridX; x++) {
            for (int y=0; y <= gridY; y++) {
                Waypoint w = new Waypoint(waypointCounter++, x*xStep, y*yStep);
                waypoints[x][y] = w;
                addVertex(w);
                if (allowWaitManeuver()) {
                    Maneuver maneuver = addEdge(w, w);
                    if (maneuver != null)
                        setEdgeWeight(maneuver, maneuver.getDuration());
                }
                
            }
        }

        // Generate edges
        for (int x=0; x <= gridX; x++) {
            for (int y=0; y <= gridY; y++) {
                Waypoint v1 = waypoints[x][y];
                     for (int[] edgeOffset : getEdgePattern()) {
                         int destX = x + edgeOffset[0];
                         int destY = y + edgeOffset[1];

                         if (destX >= 0 && destX <= gridX && destY >= 0 && destY <= gridY) {                                
                        	    Waypoint v2 = waypoints[destX][destY];
                        	    
                        	    if (!containsEdge(v1, v2)) {                        	    
	                                Maneuver maneuverForward = addEdge(v1, v2);
	                                if (maneuverForward != null)
	                                    setEdgeWeight(maneuverForward, maneuverForward.getDuration());
                        	    }
                        	    
                        	    if (!containsEdge(v2, v1)) {                                
	                                Maneuver maneuverBack = addEdge(v2, v1);
	                                if (maneuverBack != null)
	                                    setEdgeWeight(maneuverBack, maneuverBack.getDuration());
                        	    }
                         }
                     }
                }
            }
        }

}
