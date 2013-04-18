package org.jgrapht.alg;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.util.Heuristic;
import org.teneighty.heap.BinaryHeap;

public class ARAStarGeneralGraphTest extends AbstractGeneralGraphTest {

    @Override
    public void initialize() {
    }

    @Override
    GraphPath<Node, DefaultWeightedEdge> runTestedAlgorithm(
            ShortestPathProblem<Node, DefaultWeightedEdge> problem,
            GraphPath<Node, DefaultWeightedEdge> referencePath) {

        ARAStarShortestPath<Node, DefaultWeightedEdge> araStar = new ARAStarShortestPath<Node, DefaultWeightedEdge>(problem.graph, new Heuristic<Node>() {
            @Override
            public double getCostToGoalEstimate(Node current) {
                return 0;
            }
        }, problem.startVertex, problem.endVertex, 2, 0.02, new BinaryHeap<Double, Node>());

        ARAStarShortestPath.Result<Node, DefaultWeightedEdge> araResult;
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

    private void assertValidPath(Node startVertex, Node endVertex,
            GraphPath<Node, DefaultWeightedEdge> path) {
        Graph<Node, DefaultWeightedEdge> graph = path.getGraph();
        Node current = startVertex;

        for (DefaultWeightedEdge edge : path.getEdgeList()) {
            Node source = graph.getEdgeSource(edge);
            Node target = graph.getEdgeTarget(edge);

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

// End DijkstraShortestPathTest.java