package org.jgrapht.alg;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;



/**
 * An implementation of <a href="http://en.wikipedia.org/wiki/A*_search_algorithm">A* search algorithm</a>.
 *
 * @author Michal Cap
 * @since Apr 16, 2012
 */
public final class AStarShortestPath<V, E> implements PathPlanner<V, E>
{
    public static interface Heuristic<V> {
        double getHeuristicEstimate(V current, V goal);
    }

    //~ Instance fields --------------------------------------------------------

    Set<V> closed = new HashSet<V>();

    Map<V,Double> gScores = new HashMap<V,Double>();
    Map<V,Double> hScores = new HashMap<V,Double>();
    Map<V,Double> fScores = new HashMap<V,Double>();

    PriorityQueue<V> open = new PriorityQueue<V>(100,new Comparator<V>() {

        public int compare(V o1, V o2) {
            return (int) Math.signum(fScores.get(o1) - fScores.get(o2));
        };
    });

    Map<V,V> cameFrom = new HashMap<V,V>();

    private Graph<V, E> graph;
    private GraphPath<V, E> path;

    //~ Constructors -----------------------------------------------------------

    /* (non-Javadoc)
     * @see org.jgrapht.alg.PathPlanner#planPath(org.jgrapht.Graph, V, V, org.jgrapht.alg.AStarShortestPath.Heuristic)
     */
    @Override
    public boolean planPath(Graph<V, E> graph, V startVertex,
                                   V endVertex, Heuristic<V> h) {
        this.graph = graph;

        if (!graph.containsVertex(endVertex)) {
            throw new IllegalArgumentException(
                    "graph must contain the end vertex");
        }

        open.add(startVertex);


        double hScoreStart = h.getHeuristicEstimate(startVertex, endVertex);
        gScores.put(startVertex, 0.0);
        hScores.put(startVertex, hScoreStart);
        fScores.put(startVertex, hScoreStart);

        while (!open.isEmpty()) {
            V current = open.poll();

            if (current.equals(endVertex)) {
                List<E> edgeList = reconstructEdgeList(endVertex);

                double length = 0.0;
                for (E edge : edgeList) {
                    length += graph.getEdgeWeight(edge);
                }

                path = new GraphPathImpl<V, E>(
                    graph,
                    startVertex,
                    endVertex,
                    edgeList,
                    length);


                return true;
            }

            closed.add(current);

            Set<E> neighborEdges = graph.edgesOf(current);
            for (E edge : neighborEdges) {

                V neighbor = Graphs.getOppositeVertex(graph, edge, current);

                if (closed.contains(neighbor)) {
                    continue;
                }

                double tentativeGScore = gScores.get(current)
                        + graph.getEdgeWeight(edge);

                if (!open.contains(neighbor)) {
                    hScores.put(neighbor, h.getHeuristicEstimate(neighbor, endVertex));

                    cameFrom.put(neighbor, current);
                    gScores.put(neighbor, tentativeGScore);
                    fScores.put(neighbor, tentativeGScore + hScores.get(neighbor));

                    open.add(neighbor);

                } else if (tentativeGScore < gScores.get(neighbor)) {
                    cameFrom.put(neighbor, current);
                    gScores.put(neighbor, tentativeGScore);
                    fScores.put(neighbor, tentativeGScore + hScores.get(neighbor));
                    // Required to sort the open list again
                    open.remove(neighbor);
                    open.add(neighbor);
                }
            }
        }
        
        return false;
    }

    private List<E> reconstructEdgeList(V vertex) {
        if (cameFrom.containsKey(vertex)) {
            Set<E> edges = graph.getAllEdges(cameFrom.get(vertex), vertex);
            E edge = Collections.min(edges, new Comparator<E>() {
                @Override
                public int compare(E o1, E o2) {
                    return (int) Math.signum((graph.getEdgeWeight(o1) - graph.getEdgeWeight(o2))) ;
                }
            });

            List<E> priorPath = reconstructEdgeList(cameFrom.get(vertex));
            priorPath.add(edge);
            return priorPath;

        } else {
            return new LinkedList<E>();
        }
    }

    //~ Methods ----------------------------------------------------------------

    /* (non-Javadoc)
     * @see org.jgrapht.alg.PathPlanner#getPathEdgeList()
     */
    @Override
    public List<E> getPathEdgeList()
    {
        if (path == null) {
            return null;
        } else {
            return path.getEdgeList();
        }
    }

    /* (non-Javadoc)
     * @see org.jgrapht.alg.PathPlanner#getPath()
     */
    @Override
    public GraphPath<V, E> getPath()
    {
        return path;
    }

    /* (non-Javadoc)
     * @see org.jgrapht.alg.PathPlanner#getPathLength()
     */
    @Override
    public double getPathLength()
    {
        if (path == null) {
            return Double.POSITIVE_INFINITY;
        } else {
            return getPath().getWeight();
        }
    }


}
