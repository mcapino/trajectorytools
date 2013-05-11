package org.jgrapht.listenable;

import org.jgrapht.event.EdgeChangeEvent;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.WeightedGraph;

public class ListenableUndirectedWeightedGraphWrapper<V, E> extends ListenableUndirectedGraphWrapper<V, E> implements WeightedGraph<V, E> {

    private WeightedGraph<V, E> graph;

    public ListenableUndirectedWeightedGraphWrapper(UndirectedGraph<V, E> graph) {
        super(graph);
        if (graph instanceof WeightedGraph) {
            this.graph = (WeightedGraph<V, E>) graph;
        } else {
            throw new RuntimeException("ListenableUndirectedWeightedGraphWrapper must be Undirected and Weighted");
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