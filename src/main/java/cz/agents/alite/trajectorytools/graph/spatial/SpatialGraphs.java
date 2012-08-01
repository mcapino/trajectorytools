package cz.agents.alite.trajectorytools.graph.spatial;

import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import cz.agents.alite.trajectorytools.util.SpatialPoint;

public class SpatialGraphs {
    public static <V extends SpatialPoint, E> V getNearestVertex(Graph<V, E> graph, SpatialPoint pos) {
        V nearestVertex = null;
        double nearestDistance = Double.POSITIVE_INFINITY;
        for (V currentVertex : graph.vertexSet()) {
            double distance = currentVertex.distance(pos);
            if (distance < nearestDistance || nearestVertex == null) {
                nearestVertex = currentVertex;
                nearestDistance = distance;
            }
        }

        return nearestVertex;
    }

    public static <V, E> Graph<V, E>  clone(Graph<V, E> other) {
        DirectedWeightedMultigraph<V, E> graph = new DirectedWeightedMultigraph<V,E>(
            new EdgeFactory<V,E>() {

                @Override
                public E createEdge(V sourceVertex,
                        V targetVertex) {
                    throw new RuntimeException("Should not be reached");
                }}
        );

        for (V vertex : other.vertexSet()) {
            graph.addVertex(vertex);
        }

        for (E edge : other.edgeSet()) {
            graph.addEdge(other.getEdgeSource(edge), other.getEdgeTarget(edge), edge);
        }

        return graph;
    }



}
