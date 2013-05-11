package org.jgrapht.alg;

import org.jgrapht.event.ListenableWrapperChangeEvent;
import org.jgrapht.event.VertexChangeEvent;
import org.jgrapht.event.EdgeChangeEvent;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import org.jgrapht.Graphs;
import org.jgrapht.GraphPath;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.listenable.*;
import org.jgrapht.util.Goal;
import org.jgrapht.util.Heuristic;
import org.jgrapht.util.PlanningHeapWrapper;
import org.teneighty.heap.FibonacciHeap;

public class DStarLiteShortestPath<V, E> extends PlanningAlgorithm<V, E> implements ListenableWrapperListenerWrapper<V, E> {

    private Set<V> inconsistentGoals;
    private Map<V, Double> rightHandSideValue;
    private Map<V, E> rightHandSideLeastEdge;
    //
    private Heuristic<V> heuristic;
    private PlanningHeapWrapper<Key, V> heap;
    //
    private Queue<ListenableWrapperChangeEvent<V, E>> eventQueue;
    //
    private V leastDistantGole;
    private Key leastDistantGoleKey;

    public DStarLiteShortestPath(ListenableGraphWrapper<V, E> graph, Heuristic<V> heuristic, V startVertex,
            final V endVertex) {

        this(graph, heuristic, startVertex, new Goal<V>() {
            @Override
            public boolean isGoal(V current) {
                return endVertex.equals(current);
            }
        });
    }

    @SuppressWarnings("LeakingThisInConstructor")
    public DStarLiteShortestPath(ListenableGraphWrapper<V, E> graph, Heuristic<V> heuristic,
            V startVertex, Goal<V> goal) {
        super(graph, startVertex, goal);
        graph.addListeners(this);

        this.heuristic = heuristic;
        initialize();
    }

    private void initialize() {
        heap = new PlanningHeapWrapper<Key, V>(new FibonacciHeap<Key, V>());
        eventQueue = new ArrayDeque<ListenableWrapperChangeEvent<V, E>>();
        rightHandSideValue = new HashMap<V, Double>();
        rightHandSideLeastEdge = new HashMap<V, E>();
        inconsistentGoals = new HashSet<V>();

        setRightHandSideValue(startVertex, 0.);
        heap.insert(calculateKey(startVertex), startVertex);
    }

    public GraphPath<V, E> iterate() {
        handleAvailableEvents();
        return repareShortestPath();
    }

    private Key calculateKey(V vertex) {
        Key key = new Key();
        double min = Math.min(getShortestDistanceTo(vertex), getRightHandSideValue(vertex));

        key.k1 = min + heuristic.getCostToGoalEstimate(vertex);
        key.k2 = min;

        return key;
    }

    private GraphPath<V, E> repareShortestPath() {
        V foundGoal = null;

        while (!heap.isEmpty()) {
            V vertex = heap.extractMinimum().getValue();
            setShortestPathTreeEdge(vertex, getRightHandSideLeastEdge(vertex));

            double vertexDistance = getShortestDistanceTo(vertex);
            double rightHandSide = getRightHandSideValue(vertex);

            if (vertexDistance > rightHandSide) {
                setShortestDistanceTo(vertex, rightHandSide);
                checkToBeLeastDistantGoal(vertex);
                updateRightHandSideOfSuccessors(vertex);

            } else {
                setShortestDistanceTo(vertex, Double.POSITIVE_INFINITY);
                updateRightHandSide(vertex);
            }

            if (seenGoalsHaveLowerKeyThatTopOfQueue() && allSeenGoalsProcessed()) {
                foundGoal = leastDistantGole;
                break;
            }
        }

        if (foundGoal != null) {
            return reconstructPath(startVertex, foundGoal);
        } else {
            return null;
        }
    }

    private void checkToBeLeastDistantGoal(V vertex) {
        if (goal.isGoal(vertex)) {
            Key key = calculateKey(vertex);
            if (leastDistantGoleKey == null || key.compareTo(leastDistantGoleKey) < 0) {
                leastDistantGole = vertex;
                leastDistantGoleKey = key;
            }
            inconsistentGoals.remove(vertex);
        }
    }

    private void updateRightHandSideOfSuccessors(V vertex) {
        for (Iterator<V> it = specifics.succesorVertexIterator(vertex); it.hasNext();) {
            V succesor = it.next();
            if (succesor != vertex) {
                updateRightHandSide(succesor);
            }
        }
    }

    private boolean seenGoalsHaveLowerKeyThatTopOfQueue() {
        //FIXME rubbish
        return leastDistantGoleKey != null
                && (heap.isEmpty() || leastDistantGoleKey.compareTo(calculateKey(heap.peekMinimum().getValue())) < 0);
    }

    private boolean allSeenGoalsProcessed() {
        return inconsistentGoals.isEmpty();
    }

    private void updateRightHandSide(V vertex) {
        if (vertex != startVertex) {
            findShortestPathThruPredecessors(vertex);
        }

        if (getShortestDistanceTo(vertex) == getRightHandSideValue(vertex)) {
            heap.remove(vertex);
        } else {
            heap.insertOrUpdateKey(calculateKey(vertex), vertex);
            if (goal.isGoal(vertex)) {
                inconsistentGoals.add(vertex);
            }
        }
    }

    private void findShortestPathThruPredecessors(V vertex) {
        //FIXME confusing name of function
        Set<E> edges = specifics.incomingEdgesOf(vertex);

        double minDistance = Double.POSITIVE_INFINITY;
        E minEdge = null;

        for (E incomingEdge : edges) {
            V opositeVertex = Graphs.getOppositeVertex(graph, incomingEdge, vertex);

            if (opositeVertex == vertex) {
                continue;
            }

            double candidateDistance = getShortestDistanceTo(opositeVertex)
                    + graph.getEdgeWeight(incomingEdge);

            if (candidateDistance < minDistance) {
                minDistance = candidateDistance;
                minEdge = incomingEdge;
            }
        }

        if (!Double.isInfinite(minDistance)) {
            setRightHandSideValue(vertex, minDistance);
            setRightHandSideLeastEdge(vertex, minEdge);
        }
    }

    private E getRightHandSideLeastEdge(V vertex) {
        return rightHandSideLeastEdge.get(vertex);
    }

    private void setRightHandSideLeastEdge(V vertex, E edge) {
        rightHandSideLeastEdge.put(vertex, edge);
    }

    private Double getRightHandSideValue(V vertex) {
        Double rhs = rightHandSideValue.get(vertex);
        return (rhs == null) ? Double.POSITIVE_INFINITY : rhs;
    }

    private void setRightHandSideValue(V vertex, double rhs) {
        rightHandSideValue.put(vertex, rhs);
    }

    // ---- GraphListener methods --------------------------------
    @Override
    public void handleEdgeEvent(EdgeChangeEvent<V, E> event) {
        eventQueue.offer(event);
    }

    @Override
    public void handleVertexEvent(VertexChangeEvent<V, E> event) {
        eventQueue.offer(event);
    }

    private void handleAvailableEvents() {
        while (!eventQueue.isEmpty()) {
            handleEvent(eventQueue.poll());
        }
    }

    private void handleEvent(ListenableWrapperChangeEvent event) {
        if (event instanceof EdgeChangeEvent) {
            handleEdgeChangeEvent((EdgeChangeEvent) event);
        } else {
            handleVertexChangeEvent((VertexChangeEvent) event);
        }
    }

    private void handleVertexChangeEvent(VertexChangeEvent<V, E> vertexEvent) {
        int type = vertexEvent.getType();
        V vertex = vertexEvent.getVertex();

        if (type == VertexChangeEvent.VERTEX_REMOVED) {
            handleVertexRemoved(vertex);
        }
    }

    private void handleEdgeChangeEvent(EdgeChangeEvent<V, E> edgeEvent) {
        V source = edgeEvent.getSource();
        V target = edgeEvent.getTarget();

        if (graph.containsVertex(target)) {
            updateRightHandSide(target);
        }
        if (graph instanceof UndirectedGraph && graph.containsVertex(source)) {
            updateRightHandSide(source);
        }
    }

    private void handleVertexRemoved(V vertex) {
        removeShortestDistanceTo(vertex);
        removeShortestPathTreeEdge(vertex);
        heap.remove(vertex);
        rightHandSideValue.remove(vertex);
        rightHandSideLeastEdge.remove(vertex);
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
