package org.jgrapht.alg;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.util.Goal;
import org.jgrapht.util.HeuristicToGoal;
import org.teneighty.heap.FibonacciHeap;
import org.teneighty.heap.Heap;

import java.util.*;

public class AStarShortestPathSimple<V, E> extends PlanningAlgorithm<V, E> {

    protected Heap<Double, V> heap;
    protected HeuristicToGoal<V> heuristicToGoal;
    protected Map<V, Heap.Entry<Double, V>> opened;
    protected Set<V> closed;
    protected final int iterationLimit;
    protected int iterationCounter;
    //
    protected GraphPath<V, E> path;
    protected V current;


    public static <V, E> GraphPath<V, E> findPathBetween(Graph<V, E> graph, HeuristicToGoal<V> heuristic, V startVertex, final V endVertex) {
        return findPathBetween(graph, heuristic, startVertex, endVertex, Integer.MAX_VALUE);
    }

    public static <V, E> GraphPath<V, E> findPathBetween(Graph<V, E> graph, HeuristicToGoal<V> heuristic, V startVertex, Goal<V> goal) {
        return findPathBetween(graph, heuristic, startVertex, goal, Integer.MAX_VALUE);
    }

    public static <V, E> GraphPath<V, E> findPathBetween(Graph<V, E> graph, HeuristicToGoal<V> heuristic, V startVertex, final V endVertex, int iterationLimit) {
        return findPathBetween(graph, heuristic, startVertex, new Goal<V>() {
            @Override
            public boolean isGoal(V current) {
                return current.equals(endVertex);
            }
        }, iterationLimit);
    }

    public static <V, E> GraphPath<V, E> findPathBetween(Graph<V, E> graph, HeuristicToGoal<V> heuristic, V startVertex, Goal<V> goal, int iterationLimit) {
        AStarShortestPathSimple<V, E> alg = new AStarShortestPathSimple<V, E>(graph, heuristic, startVertex, goal, iterationLimit);
        return alg.findShortestPath();
    }


    public AStarShortestPathSimple(Graph<V, E> graph, HeuristicToGoal<V> heuristic, V startVertex, final V endVertex, int iterationLimit) {

        this(graph, heuristic, startVertex, new Goal<V>() {
            @Override
            public boolean isGoal(V current) {
                return endVertex.equals(current);
            }
        }, iterationLimit);
    }

    public AStarShortestPathSimple(Graph<V, E> graph, HeuristicToGoal<V> heuristic, V startVertex, Goal<V> goal, int iterationLimit) {
        super(graph, startVertex, goal);

        this.heuristicToGoal = heuristic;
        this.heap = new FibonacciHeap<Double, V>();
        this.opened = new HashMap<V, Heap.Entry<Double, V>>();
        this.closed = new HashSet<V>();
        this.iterationLimit = iterationLimit;
        initialize();
    }

    private void initialize() {
        setShortestDistanceTo(startVertex, 0.);
        Heap.Entry<Double, V> entry = heap.insert(calculateKey(startVertex), startVertex);
        opened.put(startVertex, entry);
    }

    protected GraphPath<V, E> findShortestPath() {
        V foundGoal = null;

        while (!heap.isEmpty() && iterationCounter++ < iterationLimit) {
            current = heap.extractMinimum().getValue();

            if (goal.isGoal(current)) {
                foundGoal = current;
                break;
            }

            opened.remove(current);
            closed.add(current);

            double vertexDistance = getShortestDistanceTo(current);

            Set<E> edges = specifics.outgoingEdgesOf(current);
            for (E edge : edges) {
                V child = Graphs.getOppositeVertex(graph, edge, current);

                if (closed.contains(child)) {
                    continue;
                }

                double edgeCost = graph.getEdgeWeight(edge);
                double childDistance = getShortestDistanceTo(child);
                double candidateDistance = vertexDistance + edgeCost;

                Heap.Entry<Double, V> entry = opened.get(child);
                if (entry == null) {
                    entry = heap.insert(candidateDistance, child);
                    opened.put(child, entry);

                    setShortestDistanceTo(child, candidateDistance);
                    setShortestPathTreeEdge(child, edge);
                } else if (candidateDistance < childDistance) {
                    heap.decreaseKey(entry, candidateDistance);

                    setShortestDistanceTo(child, candidateDistance);
                    setShortestPathTreeEdge(child, edge);
                }
            }
        }

        if (foundGoal != null) {
            path = reconstructPath(startVertex, foundGoal);
        } else {
            path = null;
        }

        return path;
    }


    private double calculateKey(V vertex) {
        return getShortestDistanceTo(vertex) + heuristicToGoal.getCostToGoalEstimate(vertex);
    }

    public Collection<V> getOpenedNodes() {
        return opened.keySet();
    }

    public Collection<V> getClosedNodes() {
        return closed;
    }

    public V getParent(V vertex) {
        return Graphs.getOppositeVertex(graph, getShortestPathTreeEdge(vertex), vertex);
    }

    public double getFValue(V vertex) {
        return calculateKey(vertex);
    }

    public V getCurrentVertex() {
        return current;
    }

}
