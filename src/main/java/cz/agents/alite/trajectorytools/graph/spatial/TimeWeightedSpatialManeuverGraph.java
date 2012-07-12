package cz.agents.alite.trajectorytools.graph.spatial;

import cz.agents.alite.trajectorytools.graph.spatial.maneuvers.SpatialManeuver;

@SuppressWarnings("serial")
public class TimeWeightedSpatialManeuverGraph extends
		DefaultSpatialManeuverGraph {

	@Override
	public double getEdgeWeight(SpatialManeuver e) {
		return e.getDuration();
	}
}
