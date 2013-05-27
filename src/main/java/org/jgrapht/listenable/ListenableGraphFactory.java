package org.jgrapht.listenable;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.WeightedGraph;

public class ListenableGraphFactory {

    public static<V, E> ListenableWrapper createListenableWrapper(Graph<V, E> graph) {
        if (graph instanceof DirectedGraph) {
            if (graph instanceof WeightedGraph) {
                return new ListenableDirectedWeightedWrapper((DirectedGraph) graph);
            } else {
                return new ListenableDirectedWrapper((DirectedGraph) graph);
            }

        } else {
            if (graph instanceof WeightedGraph) {
                return new ListenableUndirectedWeightedWrapper((UndirectedGraph) graph);
            } else {
                return new ListenableUndirectedWrapper((UndirectedGraph) graph);
            }
        }
    }
}
