package org.jgrapht.alg;

import org.jgrapht.*;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.util.Heuristic;
import org.junit.After;

public class AStarFibanacciVsPrioritiQeueTest extends AbstractEuclideanGraphTest {

    @Override
    public void initialize() {
//        EDGES = 10000;
//        VERTICES = 2000;
//        TRIALS = 100;
    }

    @Override
    protected GraphPath<Point, DefaultWeightedEdge> runTestedAlgorithm(Graph<Point, DefaultWeightedEdge> graph, Point startVertex, final Point endVertex, GraphPath<Point, DefaultWeightedEdge> referencePath) {
        return AStarShortestPath.findPathBetween(graph, new Heuristic<Point>() {
            @Override
            public double getCostToGoalEstimate(Point current) {
                return current.euclideanDistance(endVertex);
            }
        }, startVertex, endVertex);
    }

    @Override
    protected GraphPath<Point, DefaultWeightedEdge> runReferenceAlgorithm(Graph<Point, DefaultWeightedEdge> graph, Point startVertex, Point endVertex) {
        return new AStarShortestPathSimple<Point, DefaultWeightedEdge>(graph, startVertex, endVertex, new org.jgrapht.alg.AStarShortestPathSimple.Heuristic<Point>() {
            @Override
            public double getHeuristicEstimate(Point current, Point goal) {
                return Math.sqrt(Math.pow(current.x - goal.x, 2) + Math.pow(current.y - goal.y, 2));
            }
        }).getPath();
    }

    @After
    public void showResults() {
        printMeasuredTimes();
    }
}