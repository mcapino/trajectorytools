package org.jgrapht.alg;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.util.Heuristic;

/**
 *
 * @author Vojtech Letal <letalvoj@fel.cvut.cz>
 */
public class AStarGeneralGraphTest extends AbstractGeneralGraphTest {

    @Override
    public void initialize() {
    }

    @Override
    GraphPath<Node, DefaultWeightedEdge> runTestedAlgorithm(
            ShortestPathProblem<Node, DefaultWeightedEdge> problem,
            GraphPath<Node, DefaultWeightedEdge> referencePath) {

        return AStarShortestPath.findPathBetween(problem.graph, new Heuristic<Node>() {
            @Override
            public double getCostToGoalEstimate(Node current) {
                return 0;
            }
        }, problem.startVertex, problem.endVertex);
    }
}
