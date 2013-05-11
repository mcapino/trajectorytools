package org.jgrapht.listenable;

import java.util.Set;
import org.jgrapht.DirectedGraph;

/**
 *
 * @author Vojtech Letal <letalvoj@fel.cvut.cz>
 */
public class ListenableDirectedGraphWrapper<V, E> extends ListenableGraphWrapper<V, E> implements DirectedGraph<V, E> {

    private DirectedGraph<V, E> graph;

    public ListenableDirectedGraphWrapper(DirectedGraph<V, E> graph) {
        super(graph);
        this.graph = graph;
    }

    @Override
    public int inDegreeOf(V vertex) {
        return graph.inDegreeOf(vertex);
    }

    @Override
    public Set<E> incomingEdgesOf(V vertex) {
        return graph.incomingEdgesOf(vertex);
    }

    @Override
    public int outDegreeOf(V vertex) {
        return graph.outDegreeOf(vertex);
    }

    @Override
    public Set<E> outgoingEdgesOf(V vertex) {
        return graph.outgoingEdgesOf(vertex);
    }
}
