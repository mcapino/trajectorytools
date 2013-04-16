package org.jgrapht.alg;

import org.jgrapht.*;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.util.Heuristic;
import org.junit.After;

public class ARAStarFibanacciVsPrioritiQeueTest extends AbstractEuclideanGraphTest {

    @Override
    public void initialize() {
//        EDGES = 10000;
//        VERTICES = 2000;
//        TRIALS = 100;
    }

    @Override
    protected GraphPath<Point, DefaultWeightedEdge> runTestedAlgorithm(Graph<Point, DefaultWeightedEdge> graph, Point startVertex, final Point endVertex, GraphPath<Point, DefaultWeightedEdge> referencePath) {
        ARAStarShortestPath<Point, DefaultWeightedEdge> araStar = new ARAStarShortestPath<Point, DefaultWeightedEdge>(graph, new Heuristic<Point>() {
            @Override
            public double getCostToGoalEstimate(Point current) {
                return current.euclideanDistance(endVertex);
            }
        }, startVertex, endVertex, 2, 0.02);

        ARAStarShortestPath.Result<Point, DefaultWeightedEdge> araResult;
        do {
            araResult = araStar.iterate();

            if (araResult.path == null) {
                break;
            }

        } while (araResult.suboptimalityScale != 1);

        return araResult.path;
    }

    @Override
    protected GraphPath<Point, DefaultWeightedEdge> runReferenceAlgorithm(Graph<Point, DefaultWeightedEdge> graph, Point startVertex, final Point endVertex) {
        ARAStarShortestPathNaive<Point, DefaultWeightedEdge> araStar = new ARAStarShortestPathNaive<Point, DefaultWeightedEdge>(graph, new Heuristic<Point>() {
            @Override
            public double getCostToGoalEstimate(Point current) {
                return current.euclideanDistance(endVertex);
            }
        }, startVertex, endVertex, 2, 0.02);

        ARAStarShortestPathNaive.Result<Point, DefaultWeightedEdge> araResult;
        do {
            araResult = araStar.iterate();

            if (araResult.path == null) {
                break;
            }

        } while (araResult.suboptimalityScale != 1);

        return araResult.path;
    }

    @After
    public void showResults() {
        printMeasuredTimes();
    }
}