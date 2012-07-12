package cz.agents.alite.trajectorytools.planner;

import java.util.ArrayList;
import java.util.Arrays;
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
    @Override
    public List<E> getPathEdgeList()
    {
        return getEdgeList();
    }

    /**
     * Return the path found.
     *
     * @return path representation, or null if no path exists
     */
    @Override
    public GraphPath<V, E> getPath()
    {
        return this;
    }

    /**
     * Return the length of the path found.
     *
     * @return path length, or Double.POSITIVE_INFINITY if no path exists
     */
    @Override
    public double getPathLength()
    {
        return getPath().getWeight();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PlannedPath<?, ?>) {
            @SuppressWarnings("unchecked")
            PlannedPath<V, E> path = (PlannedPath<V, E>) obj;
            if (getPathLength() != path.getPathLength()) {
                return false;
            } else {
                return Arrays.equals(new ArrayList<E>(path.getEdgeList()).toArray(), new ArrayList<E>(getEdgeList()).toArray());
            }
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
