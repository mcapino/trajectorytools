package org.jgrapht.traverse;

import java.util.HashMap;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.util.FibonacciHeapNode;
import org.jgrapht.util.Heuristic;

/**
 *
 * @author Vojtech Letal <letalvoj@fel.cvut.cz>
 */
public class HeuristicIterator<V, E> extends ClosestFirstIterator<V, E> {

    private final Heuristic<V> heuristic;
    private final HashMap<V, Double> shortestPathLengths;

    public HeuristicIterator(Graph<V, E> g, Heuristic<V> heuristic, V startVertex) {
        this(g, heuristic, startVertex, Double.MAX_VALUE);
    }

    public HeuristicIterator(Graph<V, E> g, Heuristic<V> heuristic, V startVertex, double radius) {
        super(g, startVertex, radius);
        this.heuristic = heuristic;
        this.shortestPathLengths = new HashMap<V, Double>();
    }

    @Override
    protected void encounterVertex(V vertex, E edge) {
        double shortestPathLength;
        double heuristicEstimate;

        if (edge == null) {
            shortestPathLength = 0; //TODO why?
            heuristicEstimate = 0;
        } else {
            shortestPathLength = calculatePathLength(vertex, edge);
            heuristicEstimate = heuristic.getCostToGoalEstimate(vertex);
        }

        double key = shortestPathLength + heuristicEstimate;

        FibonacciHeapNode<QueueEntry<V, E>> node = createSeenData(vertex, edge);
        putSeenData(vertex, node);
        savePathLenght(vertex, shortestPathLength);
        heap.insert(node, key);
    }

    @Override
    protected void encounterVertexAgain(V vertex, E edge) {
        FibonacciHeapNode<QueueEntry<V, E>> node = getSeenData(vertex);

        if (node.getData().frozen) {
            return;
        }

        double candidatePathLength = calculatePathLength(vertex, edge);
        double heuristicEstimate = heuristic.getCostToGoalEstimate(vertex);
        double candidateKey = candidatePathLength + heuristicEstimate;

        if (candidateKey < node.getKey()) {
            node.getData().spanningTreeEdge = edge;
            heap.decreaseKey(node, candidateKey);
            savePathLenght(vertex, candidatePathLength);
        }
    }

    private double calculatePathLength(V vertex, E edge) {
        assertNonNegativeEdge(edge);

        V otherVertex = Graphs.getOppositeVertex(getGraph(), edge, vertex);


        return shortestPathLengths.get(otherVertex)
                + getGraph().getEdgeWeight(edge);
    }

    private void savePathLenght(V vertex, double length) {
        shortestPathLengths.put(vertex, length);
    }
}
