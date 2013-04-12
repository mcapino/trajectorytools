package org.jgrapht.alg.planning;

import java.util.HashMap;
import java.util.Queue;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import org.jgrapht.Graphs;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;
import org.jgrapht.util.Goal;
import org.jgrapht.util.Heuristic;
import org.jgrapht.util.QueueEntry;

public class DStarLiteShortestPath<V, E> extends PlanningAlgorithm<V, E> implements GraphListener<V, E> {

    private Heuristic<V> heuristic;
    private Set<V> inconsistentGoals;
    private Map<V, Double> rightHandSideValue;
    private Map<V, E> rightHandSideEdge;
    private Queue<QueueEntry<V, Key>> open;
    private GraphPath<V, E> path;
    //
    private V nearestGole;
    private Key nearestGoleKey;

    public DStarLiteShortestPath(Graph<V, E> graph, Heuristic<V> heuristic, V startVertex,
            final V endVertex) {

        this(graph, heuristic, startVertex, new Goal<V>() {
            @Override
            public boolean isGoal(V current) {
                return endVertex.equals(current);
            }
        });
    }

    public DStarLiteShortestPath(Graph<V, E> graph, Heuristic<V> heuristic,
            V startVertex, Goal<V> goal) {
        super(graph, startVertex, goal);

        this.heuristic = heuristic;
        initialize();
    }

    private void initialize() {
        open = new PriorityQueue<QueueEntry<V, Key>>();
        rightHandSideValue = new HashMap<V, Double>();
        rightHandSideEdge = new HashMap<V, E>();
        inconsistentGoals = new HashSet<V>();

        setRightHandSideValue(startVertex, 0.);
        open.add(new QueueEntry<V, Key>(startVertex, calculateKey(startVertex)));
    }

    public GraphPath<V, E> iterate() {
        repareShortestPath();
        return path;
    }

    private Key calculateKey(V vertex) {
        Key key = new Key();
        double min = Math.min(getShortestDistanceTo(vertex), getRightHandSideValue(vertex));

        key.k1 = min + heuristic.getCostToGoalEstimate(vertex);
        key.k2 = min;

        return key;
    }

    private void repareShortestPath() {
        V foundGoal = null;

        while (!open.isEmpty()) {

            QueueEntry<V, Key> toEntry = open.poll();
            V vertex = toEntry.vertex;

            setShortestPathTreeEdge(vertex, getRightHandSideEdge(vertex));

            double distance = getShortestDistanceTo(vertex);
            double rightHandSide = getRightHandSideValue(vertex);

            if (distance > rightHandSide) {
                setShortestDistanceToVertex(vertex, rightHandSide);

                if (goal.isGoal(vertex)) {
                    Key key = calculateKey(vertex);
                    if (nearestGoleKey == null || key.compareTo(nearestGoleKey) < 0) {
                        nearestGole = vertex;
                        nearestGoleKey = key;
                    }
                    inconsistentGoals.remove(vertex);
                }

                for (Iterator<V> it = specifics.succesorVertexIterator(vertex); it.hasNext();) {
                    V succesor = it.next();
                    if (succesor != vertex) {
                        updateVertex(succesor);
                    }
                }

            } else {
                setShortestDistanceToVertex(vertex, Double.POSITIVE_INFINITY);
                updateVertex(vertex);
            }

            //TODO clear this terminating condition
            if (nearestGoleKey != null && (open.isEmpty() || nearestGoleKey.compareTo(calculateKey(open.peek().vertex)) < 0) && inconsistentGoals.isEmpty()) {
                foundGoal = nearestGole;
                break;
            }

        }

        if (foundGoal != null) {
            path = reconstructPath(startVertex, foundGoal);
        } else {
            path = null;
        }
    }

    private void updateVertex(V vertex) {
        if (vertex != startVertex) {
            calculateRightHandSide(vertex);
        }

        //FIXME - might be slow, use some "static search instance" of QueueEntry
        QueueEntry<V, Key> entry = new QueueEntry<V, Key>(vertex, calculateKey(vertex));
        open.remove(entry);

        if (getShortestDistanceTo(vertex) != getRightHandSideValue(vertex)) {
            open.add(entry);

            if (goal.isGoal(vertex)) {
                inconsistentGoals.add(vertex);
            }
        }
    }

    private void calculateRightHandSide(V vertex) {
        //FIXME confusing name / function
        Set<E> edges = specifics.incomingEdgesOf(vertex);

        double minDist = Double.POSITIVE_INFINITY;
        E minEdge = null;

        for (E edge : edges) {
            V oposite = Graphs.getOppositeVertex(graph, edge, vertex);

            if (oposite == vertex) {
                continue;
            }

            double dist = getShortestDistanceTo(oposite) + graph.getEdgeWeight(edge);
            if (dist < minDist) {
                minDist = dist;
                minEdge = edge;
            }
        }

        if (!Double.isInfinite(minDist)) {
            setRightHandSideValue(vertex, minDist);
            setRightHandSideEdge(vertex, minEdge);
        }
    }

    private Double getRightHandSideValue(V vertex) {
        Double rhs = rightHandSideValue.get(vertex);
        return (rhs == null) ? Double.POSITIVE_INFINITY : rhs;
    }

    private void setRightHandSideEdge(V vertex, E edge) {
        rightHandSideEdge.put(vertex, edge);
    }

    private E getRightHandSideEdge(V vertex) {
        return rightHandSideEdge.get(vertex);
    }

    private void setRightHandSideValue(V vertex, double rhs) {
        rightHandSideValue.put(vertex, rhs);
    }

    // ---- GraphListener methods --------------------------------
    @Override
    public void edgeAdded(GraphEdgeChangeEvent<V, E> gece) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void edgeRemoved(GraphEdgeChangeEvent<V, E> gece) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void vertexAdded(GraphVertexChangeEvent<V> gvce) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void vertexRemoved(GraphVertexChangeEvent<V> gvce) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // ---- Local classes --------------------------------
    private class Key implements Comparable<Key> {

        double k1;
        double k2;

        public Key(double k1, double k2) {
            this.k1 = k1;
            this.k2 = k2;
        }

        public Key() {
        }

        @Override
        public int compareTo(Key that) {
            int comp1 = (int) Math.signum(this.k1 - that.k1);
            if (comp1 == 0) {
                return (int) Math.signum(this.k2 - that.k2);
            } else {
                return comp1;
            }
        }
    }
}
