package org.jgrapht.alg;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.listenable.ListenableDirectedGraphWrapper;
import org.jgrapht.listenable.ListenableUndirectedGraphWrapper;
import org.jgrapht.listenable.ListenableGraphWrapper;
import org.jgrapht.util.Heuristic;

public class DStarLiteEuclideanGraphTest extends AbstractEuclideanGraphTest {

    @Override
    public void initialize() {
    }

    @Override
    protected GraphPath<Point, DefaultWeightedEdge> runTestedAlgorithm(
            final ShortestPathProblem<Point, DefaultWeightedEdge> problem,
            GraphPath<Point, DefaultWeightedEdge> referencePath) {

        Graph<Point, DefaultWeightedEdge> graph = problem.graph;
        ListenableGraphWrapper<Point, DefaultWeightedEdge> listenableGraph;

        if (graph instanceof DirectedGraph) {
            listenableGraph = new ListenableDirectedGraphWrapper<Point, DefaultWeightedEdge>((DirectedGraph) graph);
        } else {
            listenableGraph = new ListenableUndirectedGraphWrapper<Point, DefaultWeightedEdge>((UndirectedGraph) graph);
        }

        DStarLiteShortestPath<Point, DefaultWeightedEdge> dStarLite =
                new DStarLiteShortestPath<Point, DefaultWeightedEdge>(listenableGraph, new Heuristic<Point>() {
            @Override
            public double getCostToGoalEstimate(Point current) {
                return current.euclideanDistance(problem.endVertex);
            }
        }, problem.startVertex, problem.endVertex);

        GraphPath<Point, DefaultWeightedEdge> dStarLitePath = dStarLite.iterate();

        return dStarLitePath;
    }
}