package org.jgrapht.alg;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.util.Heuristic;

public class DStarLiteEuclideanGraphTest extends AbstractEuclideanGraphTest {

    @Override
    GraphPath<Point, DefaultWeightedEdge> runTestedAlgorithm(Graph<Point, DefaultWeightedEdge> graph, Point startVertex, final Point endVertex, GraphPath<Point, DefaultWeightedEdge> dijkstraPath) {
        DStarLiteShortestPath<Point, DefaultWeightedEdge> dStarLite = new DStarLiteShortestPath<Point, DefaultWeightedEdge>(graph, new Heuristic<Point>() {
            @Override
            public double getCostToGoalEstimate(Point current) {
                return current.euclideanDistance(endVertex);
            }
        }, startVertex, endVertex);

        GraphPath<Point, DefaultWeightedEdge> dStarLitePath = dStarLite.iterate();

        return dStarLitePath;
    }

    @Override
    public void initialize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}