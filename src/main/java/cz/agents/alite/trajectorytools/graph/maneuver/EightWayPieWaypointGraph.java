package cz.agents.alite.trajectorytools.graph.maneuver;

import cz.agents.deconfliction.util.Point;

@SuppressWarnings("serial")
public class EightWayPieWaypointGraph extends NWayPieWaypointGraph {

	
    public EightWayPieWaypointGraph(Point center, double radius, int parts,
			int layers) {
		super(center, radius, parts, layers);
	}

	@Override
    protected int[][] getEdgePattern() {
        int[][] edgePattern = {{-1,-1}, {0,-1}, { 1,-1},
                {-1, 0},         { 1, 0},
                {-1, 1}, {0, 1}, { 1, 1}};
        return edgePattern;
    }

}
