package org.jgrapht.graph;

import org.jgrapht.DirectedGraph;
import tt.util.NotImplementedException;

import java.util.Set;

public abstract class AbstractDirectedGraphWrapper<V, E> extends AbstractGraphWrapper<V, E> implements DirectedGraph<V, E> {

    @Override
    public int inDegreeOf(V vertex) {
        throw new NotImplementedException();
    }

    @Override
    public Set<E> incomingEdgesOf(V vertex) {
        throw new NotImplementedException();
    }

    @Override
    public int outDegreeOf(V vertex) {
        throw new NotImplementedException();
    }

    @Override
    public Set<E> outgoingEdgesOf(V vertex) {
        throw new NotImplementedException();
    }
}
