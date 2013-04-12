package org.jgrapht.alg.planning;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.specifics.Specifics;
import org.jgrapht.alg.specifics.SpecificsFactory;
import org.jgrapht.graph.GraphPathImpl;
import org.jgrapht.util.Goal;

class PlanningAlgorithm<V, E> {

    protected Graph<V, E> graph;
    protected V startVertex;
    protected Goal<V> goal;
    protected Specifics<V, E> specifics;
    private Map<V, E> shortestPathTreeEdges;
    private Map<E, V> startingVerticesOfShortestPathTreeEdges;
    private Map<V, Double> shortestDistanceToVertex;

    public PlanningAlgorithm(Graph<V, E> graph, V startVertex, Goal<V> goal) {
        this.graph = graph;
        this.specifics = SpecificsFactory.create(graph);
        this.startVertex = startVertex;
        this.goal = goal;
        this.shortestPathTreeEdges = new HashMap<V, E>();
        this.startingVerticesOfShortestPathTreeEdges = new HashMap<E, V>();
        this.shortestDistanceToVertex = new HashMap<V, Double>();
    }

    protected E getShortestPathTreeEdge(V v) {
        return shortestPathTreeEdges.get(v);
    }

    protected V getStartVertexOfShortestPathTreeEdge(E e) {
        return startingVerticesOfShortestPathTreeEdges.get(e);
    }

    protected void setShortestPathTreeEdge(V v, E e) {
        shortestPathTreeEdges.put(v, e);
        startingVerticesOfShortestPathTreeEdges.put(e, v);
    }

    protected double getShortestDistanceTo(V v) {
        Double dist = shortestDistanceToVertex.get(v);
        return (dist == null) ? Double.POSITIVE_INFINITY : dist;
    }

    protected void setShortestDistanceToVertex(V v, double distance) {
        shortestDistanceToVertex.put(v, distance);
    }

    protected GraphPath<V, E> reconstructPath(V startVertex, V endVertex) {
        List<E> edgeList = reconstructEdgeList(startVertex, endVertex);
        double pathLength = getShortestDistanceTo(endVertex);

        return new GraphPathImpl<V, E>(graph, startVertex, endVertex, edgeList, pathLength);
    }

    protected List<E> reconstructEdgeList(V startVertex, V endVertex) {
        LinkedList<E> edgeList = new LinkedList<E>();
        V current = endVertex;

        while (!current.equals(startVertex)) {
            E edge = getShortestPathTreeEdge(current);
            edgeList.addFirst(edge);
            current = Graphs.getOppositeVertex(graph, edge, current);
        }

        return edgeList;
    }
}
