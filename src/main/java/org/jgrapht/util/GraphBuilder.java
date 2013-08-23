package org.jgrapht.util;

import org.jgrapht.DirectedGraph;

import java.util.*;

/**
 * This class provides methods that can build an explicit representation of a graph from its implicit representation.
 */
public class GraphBuilder {

    public static <V, E> DirectedGraph<V, E> build(DirectedGraph<V, E> implicitGraph, DirectedGraph<V, E> explicitGraph, V init) {
        return build(implicitGraph, explicitGraph, Collections.singletonList(init), Integer.MAX_VALUE);
    }

    public static <V, E> DirectedGraph<V, E> build(DirectedGraph<V, E> implicitGraph,
                                                   DirectedGraph<V, E> emptyExplicitGraph,
                                                   Collection<V> init,
                                                   int maxVertices) {

        Queue<V> open = new LinkedList<V>();
        Set<V> closed = new HashSet<V>();

        for (V v : init) {
            open.offer(v);
        }

        int counter = 0;

        while (!open.isEmpty() && counter++ < maxVertices) {
            V current = open.poll();
            emptyExplicitGraph.addVertex(current);

            Set<E> outEdges = implicitGraph.outgoingEdgesOf(current);
            for (E edge : outEdges) {
                V target = implicitGraph.getEdgeTarget(edge);
                emptyExplicitGraph.addVertex(target);
                emptyExplicitGraph.addEdge(current, target, edge);

                if (!closed.contains(target)) {
                    closed.add(target);
                    open.offer(target);
                }
            }
        }

        return emptyExplicitGraph;
    }
}
