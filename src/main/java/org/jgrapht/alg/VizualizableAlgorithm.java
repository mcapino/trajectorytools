package org.jgrapht.alg;

import org.jgrapht.GraphPath;

import java.util.Collection;

public interface VizualizableAlgorithm<V, E> {
    public GraphPath<V, E> getTraversedPath();

    public Collection<V> getOpenedNodes();

    public Collection<V> getClosedNodes();

    public V getParent(V vertex);

    public double getFValue(V vertex);

    public V getCurrentVertex();

    public int getIterationCount();
}
