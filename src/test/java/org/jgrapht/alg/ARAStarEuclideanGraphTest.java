package org.jgrapht.alg;

import static org.junit.Assert.*;
import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.util.Heuristic;
import org.teneighty.heap.BinaryHeap;

public class ARAStarEuclideanGraphTest extends AbstractEuclideanGraphTest {

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

        ARAStarShortestPath<Point, DefaultWeightedEdge> araStar = new ARAStarShortestPath<Point, DefaultWeightedEdge>(problem.graph, new Heuristic<Point>() {
            @Override
            public double getCostToGoalEstimate(Point current) {
                return current.euclideanDistance(problem.endVertex);
            }
        }, problem.startVertex, problem.endVertex, 2, 0.02, new BinaryHeap<Double, Point>());

        ARAStarShortestPath.Result<Point, DefaultWeightedEdge> araResult;
        
        do {
            araResult = araStar.iterate();

            assertFalse(araResult.path == null && referencePath != null);
            assertFalse(araResult.path != null && referencePath == null);

            if (araResult.path == referencePath) {
                break;
            }

            assertValidPath(problem.startVertex, problem.endVertex, araResult.path);
        } while (araResult.suboptimalityScale != 1 && !hasSameWeight(araResult.path, referencePath));

        return araResult.path;
    }

    private void assertValidPath(Point startVertex, Point endVertex,
            GraphPath<Point, DefaultWeightedEdge> path) {
        Graph<Point, DefaultWeightedEdge> graph = path.getGraph();
        Point current = startVertex;

        for (DefaultWeightedEdge edge : path.getEdgeList()) {
            Point source = graph.getEdgeSource(edge);
            Point target = graph.getEdgeTarget(edge);

            if (current.equals(source)) {
                current = target;
            } else if (current.equals(target) && graph instanceof UndirectedGraph<?, ?>) {
                current = source;
            } else {
                assertFalse(true);
            }
        }
        assertTrue(current.equals(endVertex));
    }
}