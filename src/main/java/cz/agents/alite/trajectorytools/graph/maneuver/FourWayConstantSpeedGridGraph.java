package cz.agents.alite.trajectorytools.graph.maneuver;

public class FourWayConstantSpeedGridGraph {

    private static final int[][] EDGE_PATTERN = {         {0,-1},
    												{-1, 0},         { 1, 0},
    														 {0, 1},          };

    
    private FourWayConstantSpeedGridGraph() {}
    
	static public ManeuverGraph create(double sizeX, double sizeY, int gridX,
            int gridY, double speed) {
    	return NWayConstantSpeedManeuverGraph.create(sizeX, sizeY, gridX, gridY, speed, EDGE_PATTERN);
    }
}
