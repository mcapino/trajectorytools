package org.jgrapht.listenable;

import org.jgrapht.event.EdgeChangeEvent;
import org.jgrapht.DirectedGraph;
import org.jgrapht.WeightedGraph;

public class ListenableDirectedWeightedGraphWrapper<V, E> extends ListenableDirectedGraphWrapper<V, E> implements WeightedGraph<V, E> {

    private WeightedGraph<V, E> graph;

    public ListenableDirectedWeightedGraphWrapper(DirectedGraph<V, E> graph) {
        super(graph);
        if (graph instanceof WeightedGraph) {
            this.graph = (WeightedGraph<V, E>) graph;
        } else {
            throw new RuntimeException("ListenableDirectedWeightedGraphWrapper must be Directed and Weighted");
        }
    }

    @Override
    public void setEdgeWeight(E edge, double weight) {
        if (graph.containsEdge(edge)) {
            graph.setEdgeWeight(edge, weight);

            V source = graph.getEdgeSource(edge);
            V target = graph.getEdgeTarget(edge);

            fireEdgeEvent(edge, source, target, EdgeChangeEvent.EDGE_WEIGHT_CHANGED);
        }
    }
}