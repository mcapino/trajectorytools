package cz.agents.alite.trajectorytools.graph.maneuver;

import cz.agents.deconfliction.util.Point;

@SuppressWarnings("serial")
public class FourWayPieWaypointGraph extends NWayPieWaypointGraph {


    public FourWayPieWaypointGraph(Point center, double radius, int parts,
			int layers) {
		super(center, radius, parts, layers);
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
