package org.jgrapht.alg;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.util.HeuristicToGoal;

/**
 * @author Vojtech Letal <letalvoj@fel.cvut.cz>
 */
public class AStarSimpleGeneralGraphTest extends AbstractGeneralGraphTest {

    @Override
    public void initialize() {
        TRIALS = 30000;
        VERTICES = 100;
        EDGES = 500;
    }

    @Override
    GraphPath<Node, DefaultWeightedEdge> runTestedAlgorithm(ShortestPathProblem<Node, DefaultWeightedEdge> problem,
                                                            GraphPath<Node, DefaultWeightedEdge> referencePath) {

        return AStarShortestPathSimple.findPathBetween(problem.graph, new HeuristicToGoal<Node>() {
            @Override
            public double getCostToGoalEstimate(Node current) {
                return 0;
            }

        }, problem.startVertex, problem.endVertex);
    }
}
