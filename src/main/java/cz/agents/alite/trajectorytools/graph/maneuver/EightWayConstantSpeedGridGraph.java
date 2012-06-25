package cz.agents.alite.trajectorytools.graph.maneuver;

public class EightWayConstantSpeedGridGraph {

    private static final int[][] EDGE_PATTERN = {{-1,-1}, {0,-1}, { 1,-1},
                                                    {-1, 0},         { 1, 0},
                                                    {-1, 1}, {0, 1}, { 1, 1}};

    private EightWayConstantSpeedGridGraph() {}

    static public ManeuverGraph create(double sizeX, double sizeY, int gridX, int gridY, double speed) {
        return NWayConstantSpeedManeuverGraph.create(sizeX, sizeY, gridX, gridY, speed, EDGE_PATTERN);
    }
}
