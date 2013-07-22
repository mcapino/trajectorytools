package tt.euclidtime3i.discretization;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.graph.GraphDelegator;

import tt.euclid2i.Line;

@SuppressWarnings("serial")
public class FreeWhenOnTargetGraph<V extends tt.euclid2i.Point, E extends Line>
        extends GraphDelegator<V, E> implements DirectedGraph<V,E> {

    V targetVertex;

    public FreeWhenOnTargetGraph(Graph<V, E> g, V targetVertex) {
        super(g);
        this.targetVertex = targetVertex;
    }

    public FreeWhenOnTargetGraph(Graph<V, E> g) {
        super(g);
    }

    @Override
    public double getEdgeWeight(E e) {
        V edgeSource = getEdgeSource(e);
        V edgeTarget = getEdgeTarget(e);
        if (edgeSource.equals(targetVertex) && edgeTarget.equals(targetVertex)) {
            return 0;
        } else {
            return e.getDistance();
        }
    }
}
