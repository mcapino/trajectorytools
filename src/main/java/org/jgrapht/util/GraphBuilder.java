package org.jgrapht.util;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.jgrapht.DirectedGraph;

/**
 * This class provides methods that can build an explicit representation of a graph from its implicit representation.
 */
public class GraphBuilder {

    public static <V, E> DirectedGraph<V, E> build(DirectedGraph<V, E> implicitGraph, DirectedGraph<V, E> explicitGraph, V init) {

        Queue<V> open = new LinkedList<V>();
        open.offer(init);
        Set<V> closed = new HashSet<V>();

        while (!open.isEmpty()) {
            V current = open.poll();
            explicitGraph.addVertex(current);

            Set<E> outEdges = implicitGraph.outgoingEdgesOf(current);
            for (E edge : outEdges) {
                V target = implicitGraph.getEdgeTarget(edge);
                explicitGraph.addVertex(target);
                explicitGraph.addEdge(current, target, edge);

                if (!closed.contains(target)) {
                    closed.add(target);
                    open.offer(target);
                }
            }
        }

        return explicitGraph;
    }
}
