package org.jgrapht.alg;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.util.Heuristic;

public class DStarLiteGeneralGraphTest extends AbstractGeneralGraphTest {

    @Override
    public void initialize() {
    }

    @Override
    GraphPath<Node, DefaultWeightedEdge> runTestedAlgorithm(
            Graph<Node, DefaultWeightedEdge> graph,
            Node startVertex,
            Node endVertex,
            GraphPath<Node, DefaultWeightedEdge> referencePath) {

        DStarLiteShortestPath<Node, DefaultWeightedEdge> dStarLite = new DStarLiteShortestPath<Node, DefaultWeightedEdge>(graph, new Heuristic<Node>() {
            @Override
            public double getCostToGoalEstimate(Node current) {
                return 0;
            }
        }, startVertex, endVertex);
        GraphPath<Node, DefaultWeightedEdge> dStarLitePath = dStarLite.iterate();

        return dStarLitePath;
    }
}