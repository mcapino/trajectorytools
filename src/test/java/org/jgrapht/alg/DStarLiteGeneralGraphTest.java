package org.jgrapht.alg;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.listenable.ListenableDirectedWrapper;
import org.jgrapht.listenable.ListenableGraphFactory;
import org.jgrapht.listenable.ListenableWrapper;
import org.jgrapht.listenable.ListenableUndirectedWrapper;
import org.jgrapht.util.Heuristic;

public class DStarLiteGeneralGraphTest extends AbstractGeneralGraphTest {

    @Override
    public void initialize() {
    }

    @Override
    GraphPath<Node, DefaultWeightedEdge> runTestedAlgorithm(
            ShortestPathProblem<Node, DefaultWeightedEdge> problem,
            GraphPath<Node, DefaultWeightedEdge> referencePath) {

        Graph<Node, DefaultWeightedEdge> graph = problem.graph;
        ListenableWrapper<Node, DefaultWeightedEdge> listenableWrapper = ListenableGraphFactory.createListenableWrapper(graph);

        DStarLiteShortestPath<Node, DefaultWeightedEdge> dStarLite =
                new DStarLiteShortestPath<Node, DefaultWeightedEdge>(listenableWrapper, new Heuristic<Node>() {
            @Override
            public double getCostToGoalEstimate(Node current) {
                return 0;
            }
        }, problem.startVertex, problem.endVertex);
        GraphPath<Node, DefaultWeightedEdge> dStarLitePath = dStarLite.iterate();

        return dStarLitePath;
    }
}