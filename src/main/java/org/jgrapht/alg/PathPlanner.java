package org.jgrapht.alg;

import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.AStarShortestPath.Heuristic;

public interface PathPlanner<V, E> {

    /**
     * Creates and executes a new AStarShortestPath algorithm instance. An
     * instance is only good for a single search; after construction, it can be
     * accessed to retrieve information about the path found.
     *
     * @param graph the graph to be searched
     * @param startVertex the vertex at which the path should start
     * @param endVertex the vertex at which the path should end
     * 
     * @return true iff a path has been found
     */
    public abstract boolean planPath(Graph<V, E> graph, V startVertex,
            V endVertex, Heuristic<V> h);

    /**
     * Return the edges making up the path found.
     *
     * @return List of Edges, or null if no path exists
     */
    public abstract List<E> getPathEdgeList();

    /**
     * Return the path found.
     *
     * @return path representation, or null if no path exists
     */
    public abstract GraphPath<V, E> getPath();

    /**
     * Return the length of the path found.
     *
     * @return path length, or Double.POSITIVE_INFINITY if no path exists
     */
    public abstract double getPathLength();

}