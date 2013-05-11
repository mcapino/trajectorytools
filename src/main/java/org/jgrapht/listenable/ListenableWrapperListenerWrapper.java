package org.jgrapht.listenable;

import org.jgrapht.event.VertexChangeEvent;
import org.jgrapht.event.EdgeChangeEvent;

public interface ListenableWrapperListenerWrapper<V, E> {

    public void handleEdgeEvent(EdgeChangeEvent<V, E> event);

    public void handleVertexEvent(VertexChangeEvent<V, E> event);
}
