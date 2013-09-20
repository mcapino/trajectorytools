package org.jgrapht.alg;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.util.HeuristicToGoal;

/**
 * @author Vojtech Letal <letalvoj@fel.cvut.cz>
 */
public class AStarEuclideanGraphTest extends AbstractEuclideanGraphTest {

    @Override
    public void initialize() {
    }

    @Override
    public void after() {
    }

    @Override
    protected GraphPath<Point, DefaultWeightedEdge> runTestedAlgorithm(
            final ShortestPathProblem<Point, DefaultWeightedEdge> problem,
            GraphPath<Point, DefaultWeightedEdge> referencePath) {

        return AStarShortestPath.findPathBetween(problem.graph, new HeuristicToGoal<Point>() {
            @Override
            public double getCostToGoalEstimate(Point current) {
                return current.euclideanDistance(problem.endVertex);
            }

        }, problem.startVertex, problem.endVertex);
    }
}
