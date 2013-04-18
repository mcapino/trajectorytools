package org.jgrapht.alg;

import org.jgrapht.Graph;

/**
 *
 * @author Vojtech Letal <letalvoj@fel.cvut.cz>
 */
public class ShortestPathProblem<V, E> {

    public Graph<V, E> graph;
    public V startVertex;
    public V endVertex;

    public ShortestPathProblem(Graph<V, E> graph, V startVertex, V endVertex) {
        this.graph = graph;
        this.startVertex = startVertex;
        this.endVertex = endVertex;
    }
}
