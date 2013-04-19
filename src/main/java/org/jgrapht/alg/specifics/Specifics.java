package org.jgrapht.alg.specifics;

import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;

/**
 * Provides unified interface for operations that are different in directed
 * graphs and in undirected graphs.
 */
public abstract class Specifics<V, E> {

    abstract Graph<V, E> getGraph();

    public abstract Set<E> outgoingEdgesOf(V vertex);

    public abstract Set<E> incomingEdgesOf(V vertex);

    @Deprecated
    public Set<? extends E> edgesOf(V vertex) {
        return outgoingEdgesOf(vertex);
    }

    public Set<V> predecessorVertexSet(V vertex) {
        return iterateIntoSet(predecessorVertexIterator(vertex));
    }

    public Set<V> succesorVertexSet(V vertex) {
        return iterateIntoSet(succesorVertexIterator(vertex));
    }

    private Set<V> iterateIntoSet(Iterator<V> iterator) {
        Set<V> set = new HashSet<V>();
        for (Iterator<V> it = iterator; it.hasNext();) {
            V v = it.next();
            set.add(v);
        }
        return set;
    }

    public Iterator<V> predecessorVertexIterator(V vertex) {
        return createOpositeVertexIterator(vertex, incomingEdgesOf(vertex));
    }

    public Iterator<V> succesorVertexIterator(V vertex) {
        return createOpositeVertexIterator(vertex, outgoingEdgesOf(vertex));
    }

    private Iterator<V> createOpositeVertexIterator(final V vertex, Set<E> edges) {
        final Iterator<E> edgeIterator = edges.iterator();

        Iterator<V> verticeIterator = new Iterator<V>() {
            @Override
            public boolean hasNext() {
                return edgeIterator.hasNext();
            }

            @Override
            public V next() {
                E next = edgeIterator.next();
                return Graphs.getOppositeVertex(getGraph(), next, vertex);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Removing vertices is not supported.");
            }
        };

        return verticeIterator;
    }
}