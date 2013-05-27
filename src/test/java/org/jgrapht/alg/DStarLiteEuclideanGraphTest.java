package org.jgrapht.alg;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.listenable.ListenableWrapper;
import org.jgrapht.listenable.ListenableGraphFactory;
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
        ListenableWrapper<Point, DefaultWeightedEdge> listenableWrapper = ListenableGraphFactory.createListenableWrapper(graph);

        DStarLiteShortestPath<Point, DefaultWeightedEdge> dStarLite =
                new DStarLiteShortestPath<Point, DefaultWeightedEdge>(listenableWrapper, new Heuristic<Point>() {
            @Override
            public double getCostToGoalEstimate(Point current) {
                return current.euclideanDistance(problem.endVertex);
            }
        }, problem.startVertex, problem.endVertex);

        GraphPath<Point, DefaultWeightedEdge> dStarLitePath = dStarLite.iterate();

        return dStarLitePath;
    }
}