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
	
	
	Graph<S,E> graph;
	S target;
	Random random;
	HeuristicFunction<S> h;
	DistanceFunction<S> distance;
	double maxExtensionLength;
	
	
	@Override
	public S sampleState() {
		return SpatialGraphs.getRandomVertex(graph, random);
	}

	@Override
	public Extension<S, GraphPath<S,E>> extendTo(S from, S to) {
		GraphPath<S, E> path = SpatialGraphs.greedySearch(graph, from, to, h, maxExtensionLength);
		boolean exact = path.getEndVertex().equals(to);
		return new Extension<S, GraphPath<S,E>>(from, path.getEndVertex(), path, path.getWeight(), exact);
	}

	@Override
	public ExtensionEstimate estimateExtension(S from, S to) {
		double cost = distance.getDistance(from, to);
		return new ExtensionEstimate(cost, cost <= maxExtensionLength);
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
