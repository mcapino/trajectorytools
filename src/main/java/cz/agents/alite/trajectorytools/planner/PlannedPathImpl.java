package cz.agents.alite.trajectorytools.planner;

import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.GraphPathImpl;

public class PlannedPathImpl <V, E> extends GraphPathImpl<V, E> implements PlannedPath<V, E> {
    
    //~ Methods ----------------------------------------------------------------

    public PlannedPathImpl(Graph<V, E> graph, V startVertex, V endVertex,
            List<E> edgeList, double weight) {
        super(graph, startVertex, endVertex, edgeList, weight);
    }

    /**
     * Return the edges making up the path found.
     *
     * @return List of Edges, or null if no path exists
     */
    public List<E> getPathEdgeList()
    {
        return getEdgeList();
    }

    /**
     * Return the path found.
     *
     * @return path representation, or null if no path exists
     */
    public GraphPath<V, E> getPath()
    {
        return this;
    }

    /**
     * Return the length of the path found.
     *
     * @return path length, or Double.POSITIVE_INFINITY if no path exists
     */
    public double getPathLength()
    {
        return getPath().getWeight();
    }
}
