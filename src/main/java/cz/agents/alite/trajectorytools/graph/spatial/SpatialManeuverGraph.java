package cz.agents.alite.trajectorytools.graph.spatial;

import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import cz.agents.alite.trajectorytools.graph.spatial.maneuvers.SpatialManeuver;

@SuppressWarnings("serial")
public abstract class SpatialManeuverGraph extends DirectedWeightedMultigraph<SpatialWaypoint, SpatialManeuver> {

	public SpatialManeuverGraph(Class<? extends SpatialManeuver> arg0) {
		super(arg0);
	}

	public SpatialManeuverGraph(
			EdgeFactory<SpatialWaypoint, SpatialManeuver> arg0) {
		super(arg0);
	}
	
}
