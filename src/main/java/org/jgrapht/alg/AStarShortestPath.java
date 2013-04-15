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
 * An implementation of <a
 * href="http://en.wikipedia.org/wiki/A*_search_algorithm">A* search
 * algorithm</a>.
 */
public class AStarShortestPath<V, E> {


    private Graph<V, E> graph;
    private Heuristic<V> heuristic;
    private V startVertex;
    private Goal<V> goal;
    private double radius;
    private GraphPath<V, E> path;

    public long expandedStates = 0;
    public long searchRuntime = 0;


    private static final long INF = Long.MAX_VALUE;

    public static <V, E> GraphPath<V, E> findPathBetween(Graph<V, E> graph,
            Heuristic<V> heuristic, V startVertex, final V endVertex) {

        return findPathBetween(graph, heuristic, startVertex, endVertex, Double.POSITIVE_INFINITY, INF);
    }

    public static <V, E> GraphPath<V, E> findPathBetween(Graph<V, E> graph,
            Heuristic<V> heuristic, V startVertex, Goal<V> goal) {

        return findPathBetween(graph, heuristic, startVertex, goal, Double.POSITIVE_INFINITY, INF);
    }

    public static <V, E> GraphPath<V, E> findPathBetween(Graph<V, E> graph,
            Heuristic<V> heuristic, V startVertex, final V endVertex, double radius, long timeoutNs) {

        return findPathBetween(graph, heuristic, startVertex, new Goal<V>() {
            @Override
            public boolean isGoal(V current) {
                return current.equals(endVertex);
            }
        }, radius, timeoutNs);
    }

    public static <V, E> GraphPath<V, E> findPathBetween(Graph<V, E> graph, Heuristic<V> heuristic,
            V startVertex, Goal<V> goal, double radius, long timeoutNs) {

        AStarShortestPath<V, E> alg = new AStarShortestPath<V, E>(graph,
                heuristic, startVertex, goal, radius);
        alg.findPath(timeoutNs);

        return alg.path;
    }

    public AStarShortestPath(Graph<V, E> graph, Heuristic<V> heuristic, V startVertex,
            Goal<V> goalChecker, double radius) {
        this.graph = graph;
        this.heuristic = heuristic;
        this.startVertex = startVertex;
        this.goal = goalChecker;
        this.radius = radius;
    }

    public GraphPath<V, E> findPath(long timeoutNs) {
        HeuristicIterator<V, E> iter =
                new HeuristicIterator<V, E>(graph, heuristic, startVertex, radius);

        long startTimeNs = System.nanoTime();

        while (iter.hasNext() && (System.nanoTime() - startTimeNs) < timeoutNs) {
            V vertex = iter.next();
            expandedStates++;

            if (goal.isGoal(vertex)) {
                path = reconstructGraphPath(graph, iter, startVertex, vertex);
                searchRuntime += System.nanoTime() - startTimeNs;
                return path;
            }
        }

        searchRuntime += System.nanoTime() - startTimeNs;
        path = null;
        return path;
    }

    protected static <V, E> GraphPath<V, E> reconstructGraphPath(Graph<V, E> graph,
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
