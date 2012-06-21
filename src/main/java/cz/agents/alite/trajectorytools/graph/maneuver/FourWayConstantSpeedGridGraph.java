package cz.agents.alite.trajectorytools.graph.maneuver;


@SuppressWarnings("serial")
public class FourWayConstantSpeedGridGraph extends NWayConstantSpeedManeuverGraph {

    public FourWayConstantSpeedGridGraph(double sizeX, double sizeY, int gridX,
            int gridY, double speed) {
        super(sizeX, sizeY, gridX, gridY, speed);
    }

    @Override
    protected int[][] getEdgePattern() {
        int[][] edgePattern =
               {         {0,-1},
                {-1, 0},         { 1, 0},
                         {0, 1},          };
        return edgePattern;
    }

}
