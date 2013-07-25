package tt.planner.rrtstar;

import ags.utils.dataStructures.KdTree;
import ags.utils.dataStructures.SquareEuclideanDistanceFunction;
import ags.utils.dataStructures.utils.MaxHeap;
import tt.planner.rrtstar.domain.Domain;
import tt.planner.rrtstar.util.Extension;
import tt.planner.rrtstar.util.Vertex;
import tt.util.EuclideanCoordinatesProvider;

import java.util.*;

public class EuclideanRRTStar<S, E> extends RRTStar<S, E> {

    protected Set<S> kdKeys;
    protected KdTree<Vertex<S, E>> knnKdTree;
    protected EuclideanCoordinatesProvider<S> euclideanProvider;
    protected SquareEuclideanDistanceFunction distanceFunction;

    public EuclideanRRTStar(Domain<S, E> domain, EuclideanCoordinatesProvider<S> euclideanProvider,
                            S initialState, double gamma, double eta) {
        super(domain, initialState, gamma, eta);

        int dimensions = euclideanProvider.getSpaceDimension();
        this.euclideanProvider = euclideanProvider;

        this.distanceFunction = new SquareEuclideanDistanceFunction();

        this.knnKdTree = new KdTree<Vertex<S, E>>(dimensions);
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
            //TODO fix possible double checking kdKeys.contains(...)
            insertIntoKDTree(newVertex);
        }

        return newVertex;
    }

    private void insertIntoKDTree(Vertex<S, E> newVertex) {
        S state = newVertex.state;
        double[] key = euclideanProvider.getEuclideanCoordinates(state);

        if (!kdKeys.contains(state)) {
            knnKdTree.addPoint(key, newVertex);
            kdKeys.add(state);
        }
    }

    @Override
    protected Vertex<S, E> getNearestParentVertex(S state) {
        double[] key = euclideanProvider.getEuclideanCoordinates(state);

        MaxHeap<Vertex<S, E>> nearestNeighbour = knnKdTree.findNearestNeighbors(key, 1, distanceFunction);

        return nearestNeighbour.getMax();
    }

    @Override
    protected Collection<Vertex<S, E>> getNearParentCandidates(S state) {
        double radius = getNearBallRadius();
        List<Vertex<S, E>> parentCandidates = getVerticesWithinRadius(state, radius);
        return parentCandidates;
    }

    @Override
    protected Collection<Vertex<S, E>> getNearChildrenCandidates(S state) {
        double radius = getNearBallRadius();
        List<Vertex<S, E>> childrenCandidates = getVerticesWithinRadius(state, radius);
        removeStateFromHeadOfList(state, childrenCandidates);

        return childrenCandidates;
    }

    private void removeStateFromHeadOfList(S state, List<Vertex<S, E>> childrenCandidates) {
        if (childrenCandidates.size() == 0)
            return;

        Vertex<S, E> nearest = childrenCandidates.get(0);
        if (state.equals(nearest.state)) {
            childrenCandidates.remove(0);
        }

    }

    protected List<Vertex<S, E>> getVerticesWithinRadius(S state, double radius) {
        double radius_sq = radius * radius;
        double[] key = euclideanProvider.getEuclideanCoordinates(state);

        Iterator<Vertex<S, E>> iterator =
                knnKdTree.getNearestNeighborIterator(key, kdKeys.size(), distanceFunction);
        List<Vertex<S, E>> list = new LinkedList<Vertex<S, E>>();

        while (iterator.hasNext()) {
            Vertex<S, E> vertex = iterator.next();
            double[] key2 = euclideanProvider.getEuclideanCoordinates(vertex.state);
            if (distanceSquared(key, key2) < radius_sq) {
                list.add(vertex);
            } else {
                break;
            }
        }

        return list;
    }

    protected static double distanceSquared(double[] key1, double[] key2) {
        double dist_sq = 0;
        for (int i = 0; i < key1.length; i++) {
            double diff = key1[i] - key2[i];
            dist_sq += diff * diff;
        }
        return dist_sq;
    }
}