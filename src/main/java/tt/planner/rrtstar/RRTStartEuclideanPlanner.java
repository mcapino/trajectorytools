package tt.planner.rrtstar;

import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeySizeException;



public class RRTStartEuclideanPlanner<S extends EuclideanPoint, E> extends RRTStarPlanner<S, E> {

    private KDTree<Vertex<S, E>> kdTree;

    public RRTStartEuclideanPlanner(Domain<S, E> domain, S initialState, double gamma, double eta) {
        super(domain, initialState, gamma, eta);

        int dimensions = initialState.getCoordinates().length;
        this.kdTree = new KDTree<Vertex<S, E>>(dimensions);
    }

    public RRTStartEuclideanPlanner(Domain<S, E> domain, S initialState, double gamma) {
        this(domain, initialState, gamma, Double.POSITIVE_INFINITY);
    }


    @Override
    protected Vertex<S, E> insertExtension(Vertex<S, E> parent, Extension<S, E> extension) {
        Vertex<S, E> newVertex = super.insertExtension(parent, extension);
        if (newVertex != null) {
            S state = newVertex.state;

            try {
                kdTree.insert(state.getCoordinates(), newVertex);
            } catch (KeySizeException e) {
                throw new RuntimeException("A dimension of a state coordinates does not match the dimension of initial state");

            } catch (KeyDuplicateException e) {
                //TODO handle - if getNearestParentVertex returns parent on the very same coordinates throw it away
                throw new RuntimeException("KD-Tree does not support duplicate keys");
            }
        }

        return newVertex;
    }

    @Override
    protected Vertex<S, E> getNearestParentVertex(S vertex) {
        try {
            return kdTree.nearest(vertex.getCoordinates());
        } catch (KeySizeException e) {
            throw new RuntimeException("A dimension of a state coordinates does not match the dimension of initial state");
        }

    }

}