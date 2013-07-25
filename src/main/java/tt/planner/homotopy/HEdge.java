package tt.planner.homotopy;

public class HEdge<V, E> {

    private E edge;
    private HNode<V> source;
    private HNode<V> target;

    HEdge(E edge, HNode<V> source, HNode<V> target) {
        this.edge = edge;
        this.source = source;
        this.target = target;
    }

    public E getEdge() {
        return edge;
    }

    public HNode<V> getSource() {
        return source;
    }

    public HNode<V> getTarget() {
        return target;
    }
}
