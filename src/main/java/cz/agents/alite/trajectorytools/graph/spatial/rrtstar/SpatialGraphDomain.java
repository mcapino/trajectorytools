package cz.agents.alite.trajectorytools.graph.spatial.rrtstar;

import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import cz.agents.alite.trajectorytools.graph.spatial.SpatialGraphs;
import cz.agents.alite.trajectorytools.graph.spatial.maneuvers.SpatialManeuver;
import cz.agents.alite.trajectorytools.planner.DistanceFunction;
import cz.agents.alite.trajectorytools.planner.HeuristicFunction;
import cz.agents.alite.trajectorytools.planner.rrtstar.Domain;
import cz.agents.alite.trajectorytools.planner.rrtstar.Extension;
import cz.agents.alite.trajectorytools.planner.rrtstar.ExtensionEstimate;
import cz.agents.alite.trajectorytools.util.SpatialPoint;

public class SpatialGraphDomain<S extends SpatialPoint, E extends SpatialManeuver> implements Domain<S, GraphPath<S,E>> {


    protected Graph<S,E> graph;
    protected S target;

    protected final DistanceFunction<S> distance;

    protected double maxExtensionCost;
    protected Random random;

    protected HeuristicFunction<S> h;

    public SpatialGraphDomain(Graph<S, E> graph, S target,
            final DistanceFunction<S> distance, double maxExtensionCost, Random random) {
        super();
        this.graph = graph;
        this.target = target;
        this.distance = distance;
        this.maxExtensionCost = maxExtensionCost;
        this.random = random;

        this.h = new HeuristicFunction<S>() {

            @Override
            public double getHeuristicEstimate(S current, S goal) {
                return SpatialGraphDomain.this.distance.getDistance(current, goal);
            }
        };
    }

    @Override
    public S sampleState() {
        return SpatialGraphs.getRandomVertex(graph, random);
    }

    @Override
    public Extension<S, GraphPath<S,E>> extendTo(S from, S to) {
        GraphPath<S, E> path = SpatialGraphs.greedySearch(graph, from, to, h, maxExtensionCost);
        boolean exact = path.getEndVertex().equals(to);
        return new Extension<S, GraphPath<S,E>>(from, path.getEndVertex(), path, path.getWeight(), exact);
    }

    @Override
    public ExtensionEstimate estimateExtension(S from, S to) {
        double cost = distance.getDistance(from, to);
        return new ExtensionEstimate(cost, cost <= maxExtensionCost);
    }

    @Override
    public double estimateCostToGo(S s) {
        return h.getHeuristicEstimate(s, target);
    }

    @Override
    public double distance(S s1, S s2) {
        return distance.getDistance(s1, s2);
    }

    @Override
    public double nDimensions() {
        return 3;
    }

    @Override
    public boolean isInTargetRegion(S s) {
        return target.equals(s);
    }

}
