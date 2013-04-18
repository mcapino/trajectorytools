package org.jgrapht.alg;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.util.Heuristic;

/**
 *
 * @author Vojtech Letal <letalvoj@fel.cvut.cz>
 */
public class AStarEuclideanGraphTest extends AbstractEuclideanGraphTest {

    @Override
    public void initialize() {
    }

    @Override
    protected GraphPath<Point, DefaultWeightedEdge> runTestedAlgorithm(
            Graph<Point, DefaultWeightedEdge> graph,
            Point start,
            final Point end,
            GraphPath<Point, DefaultWeightedEdge> referencePath) {

        return AStarShortestPath.findPathBetween(graph, new Heuristic<Point>() {
            @Override
            public double getCostToGoalEstimate(Point current) {
                return current.euclideanDistance(end);
            }
        }, start, end);
    }
}
