package org.jgrapht.listenable;

import org.jgrapht.UndirectedGraph;

/**
 *
 * @author Vojtech Letal <letalvoj@fel.cvut.cz>
 */
public class ListenableUndirectedGraphWrapper<V, E> extends ListenableGraphWrapper<V, E> implements UndirectedGraph<V, E> {

    private UndirectedGraph<V, E> graph;

    public ListenableUndirectedGraphWrapper(UndirectedGraph<V, E> graph) {
        super(graph);
        this.graph = graph;
    }

    @Override
    public int degreeOf(V vertex) {
        return graph.degreeOf(vertex);
    }
}
