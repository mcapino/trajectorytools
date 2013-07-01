package org.jgrapht.alg;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.graph.GraphPathImpl;
import org.jgrapht.util.Goal;
import org.jgrapht.util.Heuristic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GreedyBestFirstSearch<V, E> extends PlanningAlgorithm<V, E> {

    private Set<V> closed;
    private Heuristic<V> heuristic;
    private double radius;

    private List<E> edgeList = new ArrayList<E>();

    public static <V, E> GraphPath<V, E> findPathBetween(Graph<V, E> graph, Heuristic<V> heuristic, V startVertex, final V endVertex, double radius) {
        return findPathBetween(graph, heuristic, startVertex, new Goal<V>() {
            @Override
            public boolean isGoal(V current) {
                return current.equals(endVertex);
            }
        }, radius);
    }

    public static <V, E> GraphPath<V, E> findPathBetween(Graph<V, E> graph, Heuristic<V> heuristic, V startVertex, Goal<V> goal, double radius) {
        GreedyBestFirstSearch<V, E> alg = new GreedyBestFirstSearch<V, E>(graph, heuristic, startVertex, goal, radius);
        return alg.findPath();
    }

    private GreedyBestFirstSearch(Graph<V, E> graph, Heuristic<V> heuristic, V startVertex, Goal<V> goal, double radius) {
        super(graph, startVertex, goal);
        this.closed = new HashSet<V>();
        this.heuristic = heuristic;
        this.radius = radius;
    }

    private GraphPath<V, E> findPath() {
        double cost = 0;
        V current = startVertex;

        while (cost < radius && !goal.isGoal(current)) {

            double min = Double.POSITIVE_INFINITY;
            V bestSuccessorVertex = null;
            E bestSuccessorEdge = null;

            Set<E> outgoingEdges = specifics.outgoingEdgesOf(current);

            for (E edge : outgoingEdges) {
                V successor = Graphs.getOppositeVertex(graph, edge, current);

                if (current.equals(successor) || closed.contains(successor)) continue;

                double costToGoEstimate = heuristic.getCostToGoalEstimate(successor);

                if (costToGoEstimate < min) {
                    min = costToGoEstimate;
                    bestSuccessorVertex = successor;
                    bestSuccessorEdge = edge;
                }
            }

            if (bestSuccessorVertex == null) break;

            current = bestSuccessorVertex;
            cost += graph.getEdgeWeight(bestSuccessorEdge);
            edgeList.add(bestSuccessorEdge);
            closed.add(bestSuccessorVertex);
        }

        if (!goal.isGoal(current)) {
            return null;
        } else {
            return new GraphPathImpl<V, E>(graph, startVertex, current, edgeList, cost);
        }
    }
}
