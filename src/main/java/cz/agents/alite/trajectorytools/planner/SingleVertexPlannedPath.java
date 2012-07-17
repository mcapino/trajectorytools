package cz.agents.alite.trajectorytools.planner;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import cz.agents.alite.trajectorytools.graph.maneuver.Maneuver;
import cz.agents.alite.trajectorytools.graph.maneuver.ManeuverGraphInterface;
import cz.agents.alite.trajectorytools.graph.spatialwaypoint.SpatialWaypoint;

public class SingleVertexPlannedPath implements
        PlannedPath<SpatialWaypoint, Maneuver> {
    private final ManeuverGraphInterface graph;
    private final SpatialWaypoint vertex;

    public SingleVertexPlannedPath(ManeuverGraphInterface graph,
            SpatialWaypoint vertex) {
        this.graph = graph;
        this.vertex = vertex;
    }

    @Override
    public Graph<SpatialWaypoint, Maneuver> getGraph() {
        return graph;
    }

    @Override
    public SpatialWaypoint getStartVertex() {
        return vertex;
    }

    @Override
    public SpatialWaypoint getEndVertex() {
        return vertex;
    }

    @Override
    public List<Maneuver> getEdgeList() {
        return new ArrayList<Maneuver>();
    }

    @Override
    public double getWeight() {
        return 0;
    }

    @Override
    public List<Maneuver> getPathEdgeList() {
        return new ArrayList<Maneuver>();
    }

    @Override
    public GraphPath<SpatialWaypoint, Maneuver> getPath() {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getPathLength() {
        return 0;
    }
}