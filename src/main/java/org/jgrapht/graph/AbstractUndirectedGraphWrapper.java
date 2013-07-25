package org.jgrapht.graph;

import org.jgrapht.UndirectedGraph;
import tt.util.NotImplementedException;

public abstract class AbstractUndirectedGraphWrapper<V, E> extends AbstractGraphWrapper<V, E> implements UndirectedGraph<V, E> {

    @Override
    public int degreeOf(V vertex) {
        throw new NotImplementedException();
    }
}
