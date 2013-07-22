package tt.planner.homotopy;

import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.alg.specifics.Specifics;
import tt.util.NotImplementedException;

import javax.vecmath.Point2d;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class HomotopyGraphWrapper<V, E> implements Graph<HNode<V>, HEdge<E>> {

    private Graph<V, E> graph;
    private Specifics<V, E> specifics;
    private List<Point2d> obstacles;

    private Set<HNode<V>> nodes;
    private Set<HEdge<V>> vertices;

    @Override
    public EdgeFactory<HNode<V>, HEdge<E>> getEdgeFactory() {
        return null;
    }

    @Override
    public HEdge<E> getEdge(HNode<V> sourceVertex, HNode<V> targetVertex) {
        throw new NotImplementedException();
    }

    @Override
    public boolean containsVertex(HNode<V> vhNode) {
        throw new NotImplementedException();
    }

    @Override
    public Set<HEdge<E>> edgeSet() {
        throw new NotImplementedException();
    }

    @Override
    public Set<HEdge<E>> edgesOf(HNode<V> vertex) {
        throw new NotImplementedException();
    }

    @Override
    public boolean removeAllEdges(Collection<? extends HEdge<E>> edges) {
        throw new NotImplementedException();
    }

    @Override
    public Set<HEdge<E>> removeAllEdges(HNode<V> sourceVertex, HNode<V> targetVertex) {
        throw new NotImplementedException();
    }

    @Override
    public boolean removeAllVertices(Collection<? extends HNode<V>> vertices) {
        throw new NotImplementedException();
    }

    @Override
    public HEdge<E> removeEdge(HNode<V> sourceVertex, HNode<V> targetVertex) {
        throw new NotImplementedException();
    }

    @Override
    public boolean removeEdge(HEdge<E> ehEdge) {
        throw new NotImplementedException();
    }

    @Override
    public boolean removeVertex(HNode<V> vhNode) {
        throw new NotImplementedException();
    }

    @Override
    public Set<HNode<V>> vertexSet() {
        throw new NotImplementedException();
    }

    @Override
    public HNode<V> getEdgeSource(HEdge<E> ehEdge) {
        throw new NotImplementedException();
    }

    @Override
    public HNode<V> getEdgeTarget(HEdge<E> ehEdge) {
        throw new NotImplementedException();
    }

    @Override
    public double getEdgeWeight(HEdge<E> ehEdge) {
        throw new NotImplementedException();
    }

    @Override
    public Set<HEdge<E>> getAllEdges(HNode<V> sourceVertex, HNode<V> targetVertex) {
        throw new NotImplementedException();
    }

    @Override
    public HEdge<E> addEdge(HNode<V> sourceVertex, HNode<V> targetVertex) {
        throw new NotImplementedException();
    }

    @Override
    public boolean addEdge(HNode<V> sourceVertex, HNode<V> targetVertex, HEdge<E> ehEdge) {
        throw new NotImplementedException();
    }

    @Override
    public boolean addVertex(HNode<V> vhNode) {
        throw new NotImplementedException();
    }

    @Override
    public boolean containsEdge(HNode<V> sourceVertex, HNode<V> targetVertex) {
        throw new NotImplementedException();
    }

    @Override
    public boolean containsEdge(HEdge<E> ehEdge) {
        throw new NotImplementedException();
    }
}
