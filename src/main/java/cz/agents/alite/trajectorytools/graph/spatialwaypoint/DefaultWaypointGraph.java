package cz.agents.alite.trajectorytools.graph.spatialwaypoint;

import org.jgrapht.graph.DefaultWeightedEdge;

@SuppressWarnings("serial")
public class DefaultWaypointGraph extends WaypointGraph<DefaultWeightedEdge> {

    public DefaultWaypointGraph() {
        super(DefaultWeightedEdge.class);
    }
}
