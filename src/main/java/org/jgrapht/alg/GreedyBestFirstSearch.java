package org.jgrapht.alg;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.graph.GraphPathImpl;
import org.jgrapht.util.Goal;
import org.jgrapht.util.HeuristicToGoal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GreedyBestFirstSearch<V, E> extends PlanningAlgorithm<V, E> {

    protected GraphPath<V, E> path;
    protected Set<V> opened;
    protected HeuristicToGoal<V> heuristicToGoal;
    protected double radius;
    protected int depthLimit;

    protected List<E> edgeList = new ArrayList<E>();

    public static <V, E> GraphPath<V, E> findPathBetween(Graph<V, E> graph, HeuristicToGoal<V> heuristic, V startVertex, final V endVertex, double radius) {
        return findPathBetween(graph, heuristic, startVertex, new Goal<V>() {
            @Override
            public boolean isGoal(V current) {
                return current.equals(endVertex);
            }
        }, radius);
    }

    public static <V, E> GraphPath<V, E> findPathBetween(Graph<V, E> graph, HeuristicToGoal<V> heuristic, V startVertex, Goal<V> goal, double radius) {
        GreedyBestFirstSearch<V, E> alg = new GreedyBestFirstSearch<V, E>(graph, heuristic, startVertex, goal, radius, Integer.MAX_VALUE);
        return alg.findPath();
    }

    public static <V, E> GraphPath<V, E> findPathBetween(Graph<V, E> graph, HeuristicToGoal<V> heuristic, V startVertex, final V endVertex, int depthLimit) {
        return findPathBetween(graph, heuristic, startVertex, new Goal<V>() {
            @Override
            public boolean isGoal(V current) {
                return current.equals(endVertex);
            }
        }, depthLimit);
    }

    public static <V, E> GraphPath<V, E> findPathBetween(Graph<V, E> graph, HeuristicToGoal<V> heuristic, V startVertex, Goal<V> goal, int depthLimit) {
        GreedyBestFirstSearch<V, E> alg = new GreedyBestFirstSearch<V, E>(graph, heuristic, startVertex, goal, Double.POSITIVE_INFINITY, depthLimit);
        return alg.findPath();
    }

    public GreedyBestFirstSearch(Graph<V, E> graph, HeuristicToGoal<V> heuristic, V startVertex, Goal<V> goal, double radius, int depthLimit) {
        super(graph, startVertex, goal);
        this.opened = new HashSet<V>();
        this.heuristicToGoal = heuristic;
        this.depthLimit = depthLimit;
        this.radius = radius;
    }

    public GraphPath<V, E> findPath() {
        double cost = 0;
        double depth = 0;
        V current = startVertex;

        while (cost < radius && depth < depthLimit && !goal.isGoal(current)) {

            double min = Double.POSITIVE_INFINITY;
            V bestSuccessorVertex = null;
            E bestSuccessorEdge = null;

            Set<E> outgoingEdges = specifics.outgoingEdgesOf(current);

            for (E edge : outgoingEdges) {
                V successor = Graphs.getOppositeVertex(graph, edge, current);

                if (current.equals(successor) || opened.contains(successor)) continue;

                double costToGoEstimate = heuristicToGoal.getCostToGoalEstimate(successor);

                if (costToGoEstimate < min) {
                    min = costToGoEstimate;
                    bestSuccessorVertex = successor;
                    bestSuccessorEdge = edge;
                }
            }

            if (bestSuccessorVertex == null) break;

            current = bestSuccessorVertex;

            edgeList.add(bestSuccessorEdge);
            opened.add(bestSuccessorVertex);

            cost += graph.getEdgeWeight(bestSuccessorEdge);
            depth++;
        }

        path = new GraphPathImpl<V, E>(graph, startVertex, current, edgeList, cost);

        if (!goal.isGoal(current)) {
            return null;
        } else {
            return path;
        }
    }

    public GraphPath<V, E> getTraversedPath() {
        return path;
    }

    public Set<V> getVisitedVertices() {
        return opened;
    }
}
