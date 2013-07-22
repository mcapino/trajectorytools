package tt.planner.homotopy;

import org.jscience.mathematics.number.Complex;

public class HEdge<V, E> {

    private E edge;
    private Complex lIncrement;
    private HNode<V> source;
    private HNode<V> target;

    public HEdge(E edge, Complex lIncrement, HNode<V> source, HNode<V> target) {
        this.edge = edge;
        this.lIncrement = lIncrement;
        this.source = source;
        this.target = target;
    }

    public E getEdge() {
        return edge;
    }

    public Complex getlIncrement() {
        return lIncrement;
    }

    public HNode<V> getSource() {
        return source;
    }

    public HNode<V> getTarget() {
        return target;
    }
}
