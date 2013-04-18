package org.jgrapht.alg;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.util.Heuristic;

public class DStarLiteEuclideanGraphTest extends AbstractEuclideanGraphTest {

    @Override
    public void initialize() {
    }

    @Override
    protected GraphPath<Point, DefaultWeightedEdge> runTestedAlgorithm(
            final ShortestPathProblem<Point, DefaultWeightedEdge> problem,
            GraphPath<Point, DefaultWeightedEdge> referencePath) {

        DStarLiteShortestPath<Point, DefaultWeightedEdge> dStarLite =
                new DStarLiteShortestPath<Point, DefaultWeightedEdge>(problem.graph, new Heuristic<Point>() {
            @Override
            public double getCostToGoalEstimate(Point current) {
                return current.euclideanDistance(problem.endVertex);
            }
        }, problem.startVertex, problem.endVertex);

        GraphPath<Point, DefaultWeightedEdge> dStarLitePath = dStarLite.iterate();

        return dStarLitePath;
    }
}