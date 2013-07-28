package tt.planner.rrtstar;

import tt.planner.rrtstar.domain.Domain;
import tt.planner.rrtstar.util.Vertex;
import tt.util.EuclideanCoordinatesProvider;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class EuclideanKRRTStar<S extends tt.euclid2i.Point, E> extends EuclideanRRTStar<S, E> {

    protected int maxVertices;

    public EuclideanKRRTStar(Domain<S, E> domain, EuclideanCoordinatesProvider<S> euclideanProvider,
                             S initialState, int maxVertices) {
        super(domain, euclideanProvider, initialState, Double.POSITIVE_INFINITY);

        this.maxVertices = maxVertices;
    }

    /**
     * Returns maximum number of vertices which should be returned by getVerticesWithinRadius(...) function
     */
    @Override
    public double getNearBallRadius() {
        return maxVertices;
    }

    @Override
    protected List<Vertex<S, E>> getVerticesWithinRadius(S state, double maxVertices) {
        double[] key = euclideanProvider.getEuclideanCoordinates(state);

        Iterator<Vertex<S, E>> iterator =
                knnKdTree.getNearestNeighborIterator(key, kdKeys.size(), distanceFunction);
        List<Vertex<S, E>> list = new LinkedList<Vertex<S, E>>();

        Vertex<S, E> vertex = null;
//        int vertices = 0;

        for (int i = 0; i < maxVertices; i++) {
            if (iterator.hasNext()) {
                vertex = iterator.next();
                list.add(vertex);
//                vertices++;
            } else {
                break;
            }
        }
//        if (vertex != null)
//            System.out.println(String.format("ball,%f,%d", state.distance(vertex.state), vertices));
        return list;
    }
}
