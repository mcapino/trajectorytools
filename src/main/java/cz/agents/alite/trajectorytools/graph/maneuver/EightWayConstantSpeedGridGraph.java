package cz.agents.alite.trajectorytools.graph.maneuver;


@SuppressWarnings("serial")
public class EightWayConstantSpeedGridGraph extends NWayConstantSpeedManeuverGraph {

    public EightWayConstantSpeedGridGraph(double sizeX, double sizeY, int gridX,
            int gridY, double speed) {
        super(sizeX, sizeY, gridX, gridY, speed);
    }

    @Override
    protected int[][] getEdgePattern() {
        int[][] edgePattern = {{-1,-1}, {0,-1}, { 1,-1},
                {-1, 0},         { 1, 0},
                {-1, 1}, {0, 1}, { 1, 1}};
        return edgePattern;
    }

}
