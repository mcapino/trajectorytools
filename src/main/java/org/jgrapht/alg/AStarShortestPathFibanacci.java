package org.jgrapht.alg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.graph.GraphPathImpl;
import org.jgrapht.traverse.ClosestFirstIterator;
import org.jgrapht.traverse.HeuristicIterator;
import org.jgrapht.util.Goal;
import org.jgrapht.util.Heuristic;

/**
 * An implementation of <a href="http://en.wikipedia.org/wiki/A*_search_algorithm">A* search algorithm</a>.
 */
public class AStarShortestPathFibanacci<V, E> {

    private Graph<V, E> graph;
    private Heuristic<V> heuristic;
    private V startVertex;
    private Goal<V> goal;
    private double radius;
    private GraphPath<V, E> path;

    public static <V, E> GraphPath<V, E> findPathBetween(Graph<V, E> graph,
            Heuristic<V> heuristic, V startVertex, final V endVertex) {

        return findPathBetween(graph, heuristic, startVertex, new Goal<V>() {
            @Override
            public boolean isGoal(V current) {
                return current.equals(endVertex);
            }
        });
    }

    public static <V, E> GraphPath<V, E> findPathBetween(Graph<V, E> graph, Heuristic<V> heuristic,
            V startVertex, Goal<V> goalChecker) {

        AStarShortestPathFibanacci<V, E> alg = new AStarShortestPathFibanacci<V, E>(graph,
                heuristic, startVertex, goalChecker, Double.POSITIVE_INFINITY);
        alg.findPath();

        return alg.path;
    }

    private AStarShortestPathFibanacci(Graph<V, E> graph, Heuristic<V> heuristic, V startVertex,
            Goal<V> goalChecker, double radius) {
        this.graph = graph;
        this.heuristic = heuristic;
        this.startVertex = startVertex;
        this.goal = goalChecker;
        this.radius = radius;
    }

    private void findPath() {
        HeuristicIterator<V, E> iter =
                new HeuristicIterator<V, E>(graph, heuristic, startVertex, radius);

        while (iter.hasNext()) {
            V vertex = iter.next();

            if (goal.isGoal(vertex)) {
                path = reconstructGraphPath(graph, iter, startVertex, vertex);
                return;
            }
        }

        path = null;
    }

    private static <V, E> GraphPath<V, E> reconstructGraphPath(Graph<V, E> graph,
            ClosestFirstIterator<V, E> iter, V startVertex, V endVertex) {

        List<E> edgeList = new ArrayList<E>();
        V v = endVertex;

        double pathLength = 0;

        while (true) {
            E edge = iter.getSpanningTreeEdge(v);

            if (edge == null) {
                break;
            }

            edgeList.add(edge);
            pathLength += graph.getEdgeWeight(edge);
            v = Graphs.getOppositeVertex(graph, edge, v);
        }

        Collections.reverse(edgeList);

        return new GraphPathImpl<V, E>(graph, startVertex,
                endVertex, edgeList, pathLength);
    }

}
