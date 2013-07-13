package tt.planner.rrtstar;

import tt.planner.rrtstar.util.Vertex;
import tt.planner.rrtstar.domain.Domain;
import tt.planner.rrtstar.util.Extension;
import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeySizeException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class EuclideanRRTStar<S, E> extends RRTStar<S, E> {

    private Set<S> kdKeys;
    private KDTree<Vertex<S, E>> kdTree;
    private EuclideanCoordinatesProvider<S> euclideanProvider;

    public EuclideanRRTStar(Domain<S, E> domain, EuclideanCoordinatesProvider<S> euclideanProvider,
                                   S initialState, double gamma, double eta) {
        super(domain, initialState, gamma, eta);

        int dimensions = euclideanProvider.getSpaceDimension();
        this.euclideanProvider = euclideanProvider;

        this.kdTree = new KDTree<Vertex<S, E>>(dimensions);
        this.kdKeys = new HashSet<S>();
        insertIntoKDTree(root);
    }

    public EuclideanRRTStar(Domain<S, E> domain, EuclideanCoordinatesProvider<S> euclideanProvider,
                                   S initialState, double gamma) {
        this(domain, euclideanProvider, initialState, gamma, Double.POSITIVE_INFINITY);
    }

    @Override
    protected S sampleState() {
        S state;

        do {
            state = super.sampleState();
        } while (kdKeys.contains(state));

        return state;
    }

    @Override
    protected Vertex<S, E> insertExtension(Vertex<S, E> parent, Extension<S, E> extension) {
        Vertex<S, E> newVertex;

        if (kdKeys.contains(extension.target)) {
            //TODO might be better to check whether the new path is shorter
            newVertex = null;
        } else {
            newVertex = super.insertExtension(parent, extension);
        }

        if (newVertex != null) {
            //TODO possible double checking kdKeys.contains(...)
            insertIntoKDTree(newVertex);
        }

        return newVertex;
    }

    private void insertIntoKDTree(Vertex<S, E> newVertex) {
        S state = newVertex.state;
        double[] key = euclideanProvider.getEuclideanCoordinates(state);

        if (!kdKeys.contains(state))
            try {
                kdTree.insert(key, newVertex);
                kdKeys.add(state);

            } catch (KeySizeException e) {
                throw new RuntimeException("A dimension of a state coordinates does not match the dimension of initial state");

            } catch (KeyDuplicateException e) {
                throw new RuntimeException("KD-Tree does not support duplicate keys");
            }
    }

    @Override
    protected Vertex<S, E> getNearestParentVertex(S state) {
        double[] key = euclideanProvider.getEuclideanCoordinates(state);
        return getNearestVertex(key);
    }

    protected Vertex<S, E> getNearestVertex(double[] key) {
        try {
            return kdTree.nearest(key);
        } catch (KeySizeException e) {
            throw new RuntimeException("A dimension of a state coordinates does not match the dimension of initial state");
        }
    }

    @Override
    protected Collection<Vertex<S, E>> getNearParentCandidates(S state) {
        double radius = getNearBallRadius();
        Collection<Vertex<S, E>> parentCandidates = getVerticesInRadius(state, radius);
        return parentCandidates;
    }

    @Override
    protected Collection<Vertex<S, E>> getNearChildrenCandidates(S state) {
        double[] key = euclideanProvider.getEuclideanCoordinates(state);
        double radius = getNearBallRadius();

        Collection<Vertex<S, E>> childrenCandidates = getVerticesInRadius(state, radius);

        try {
            Vertex<S, E> stateVertex = kdTree.search(key);
            if (stateVertex != null) childrenCandidates.remove(stateVertex);

        } catch (KeySizeException e) {
            throw new RuntimeException("A dimension of a state coordinates does not match the dimension of initial state");
        }

        return childrenCandidates;
    }

    private Collection<Vertex<S, E>> getVerticesInRadius(S state, double radius) {
        double[] key = euclideanProvider.getEuclideanCoordinates(state);
        try {
            return kdTree.nearestEuclidean(key, radius);
        } catch (KeySizeException e) {
            throw new RuntimeException("A dimension of a state coordinates does not match the dimension of initial state");
        }
    }
}