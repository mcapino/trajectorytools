package cz.agents.deconfliction.waypointgraph;

import org.jgrapht.graph.DefaultWeightedEdge;

@SuppressWarnings("serial")
public class DefaultWaypointGraph extends WaypointGraph<DefaultWeightedEdge> {

    public DefaultWaypointGraph() {
        super(DefaultWeightedEdge.class);
    }
}
