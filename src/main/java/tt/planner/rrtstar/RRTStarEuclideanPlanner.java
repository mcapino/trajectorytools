package tt.planner.rrtstar;

import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeySizeException;

import java.util.HashSet;
import java.util.Set;

public class RRTStarEuclideanPlanner<S, E> extends RRTStarPlanner<S, E> {

    private Set<S> expandedStates;
    private KDTree<Vertex<S, E>> kdTree;
    private EuclideanCoordinatesProvider<S> euclideanProvider;

    public RRTStarEuclideanPlanner(Domain<S, E> domain, EuclideanCoordinatesProvider<S> euclideanProvider,
                                   S initialState, double gamma, double eta) {
        super(domain, initialState, gamma, eta);

        int dimensions = euclideanProvider.getSpaceDimension();
        this.euclideanProvider = euclideanProvider;

        this.kdTree = new KDTree<Vertex<S, E>>(dimensions);
        this.expandedStates = new HashSet<S>();
        insertIntoKDTree(euclideanProvider.getEuclideanCoordinates(initialState), root);
    }

    public RRTStarEuclideanPlanner(Domain<S, E> domain, EuclideanCoordinatesProvider<S> euclideanProvider,
                                   S initialState, double gamma) {
        this(domain, euclideanProvider, initialState, gamma, Double.POSITIVE_INFINITY);
    }

    @Override
    protected S sampleState() {
        S state;

        do {
            state = super.sampleState();
        } while (expandedStates.contains(state));

        expandedStates.add(state);

        return state;
    }

    @Override
    protected Vertex<S, E> insertExtension(Vertex<S, E> parent, Extension<S, E> extension) {
        Vertex<S, E> newVertex = super.insertExtension(parent, extension);
        if (newVertex != null) {
            S state = newVertex.state;
            insertIntoKDTree(euclideanProvider.getEuclideanCoordinates(state), newVertex);
        }

        return newVertex;
    }

    @Override
    protected Vertex<S, E> getNearestParentVertex(S state) {
        return nearestVertex(euclideanProvider.getEuclideanCoordinates(state));
    }

    private void insertIntoKDTree(double[] key, Vertex<S, E> value) {
        try {
            kdTree.insert(key, value);
        } catch (KeySizeException e) {
            throw new RuntimeException("A dimension of a state coordinates does not match the dimension of initial state");

        } catch (KeyDuplicateException e) {
            //TODO handle - if getNearestParentVertex returns parent on the very same coordinates throw it away
            throw new RuntimeException("KD-Tree does not support duplicate keys");
        }
    }

    private Vertex<S, E> nearestVertex(double[] key) {
        try {
            return kdTree.nearest(key);
        } catch (KeySizeException e) {
            throw new RuntimeException("A dimension of a state coordinates does not match the dimension of initial state");
        }
    }
}