package cz.agents.alite.trajectorytools.planner.rrtstar;

/**
 * The definition of a planning problem for RRT*
 * @author Michal Cap
 *
 * @param <P> class representing a state in the search space
 * @param <E> class representing a transition between state in the search space
 */
public interface Domain<P,E> {
    /**
     * @return a sample state from the free space
     */
    P sampleState();

    /**
     * @return constructs an extension from one state towards another state,
     * returns null if the extension cannot be constructed
     */
    Extension<P,E> extendTo(P from, P to);

    /**
     * @return an estimate of the extension cost without collision checking
     * (used to sort neighbors before trying to construct extensions)
     */
    ExtensionEstimate<P,E> estimateExtension(P p1, P p2);

    /**
     * @return the lower bound heuristic estimate of the cost from point p1 to the target region
     */
    double estimateCostToGo(P p);

    /**
     * @return the distance between points p1 and p2 (used to find the k-nearest neighbors)
     */
    double distance(P p1, P p2);

    /**
     * @return the number of dimensions of the state space
     */
    double nDimensions();

    /**
     * @return true if the point p is in the target region, false otherwise
     */
    boolean isInTargetRegion(P p);

}
