package cz.agents.alite.trajectorytools.graph.spatial;

import org.jgrapht.graph.DefaultListenableGraph;

import cz.agents.alite.trajectorytools.graph.spatial.maneuvers.SpatialManeuver;

public class ListenableSpatialManeuverGraph extends DefaultListenableGraph<SpatialWaypoint, SpatialManeuver> {
	private static final long serialVersionUID = 3428956208593195747L;

	public ListenableSpatialManeuverGraph(SpatialManeuverGraph g) {
		super(g);
	}
}
