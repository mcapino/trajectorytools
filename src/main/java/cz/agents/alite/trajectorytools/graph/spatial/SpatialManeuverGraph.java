package cz.agents.alite.trajectorytools.graph.spatial;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import cz.agents.alite.trajectorytools.graph.spatial.maneuvers.SpatialManeuver;

@SuppressWarnings("serial")
public interface SpatialManeuverGraph extends
		WeightedGraph<SpatialWaypoint, SpatialManeuver>,
		DirectedGraph<SpatialWaypoint, SpatialManeuver> {
}
