package org.jgrapht.alg;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.listenable.ListenableDirectedGraphWrapper;
import org.jgrapht.listenable.ListenableGraphWrapper;
import org.jgrapht.listenable.ListenableUndirectedGraphWrapper;
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
        ListenableGraphWrapper<Node, DefaultWeightedEdge> listenableGraph;

        if (graph instanceof DirectedGraph) {
            listenableGraph = new ListenableDirectedGraphWrapper<Node, DefaultWeightedEdge>((DirectedGraph) graph);
        } else {
            listenableGraph = new ListenableUndirectedGraphWrapper<Node, DefaultWeightedEdge>((UndirectedGraph) graph);
        }

        DStarLiteShortestPath<Node, DefaultWeightedEdge> dStarLite =
                new DStarLiteShortestPath<Node, DefaultWeightedEdge>(listenableGraph, new Heuristic<Node>() {
            @Override
            public double getCostToGoalEstimate(Node current) {
                return 0;
            }
        }, problem.startVertex, problem.endVertex);
        GraphPath<Node, DefaultWeightedEdge> dStarLitePath = dStarLite.iterate();

        return dStarLitePath;
    }
}