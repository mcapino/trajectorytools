package cz.agents.alite.trajectorytools.planner.rrtstar;

public interface Domain<P,E> {
    /**
     * @return a sample state from free space
     */
    P getRandomSample();

    Extension<P,E> extendTo(P from, P to);
    /**
     * @return an estimate of extension cost without collision checking
     */
    ExtensionEstimate<P,E> estimateExtension(P p1, P p2);
    /**
     * @return the lower bound heuristic estimate of the cost from point p1 to the target region
     */
    double estimateCostToGo(P p);
    /**
     * @return distance between points p1 and p2
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
