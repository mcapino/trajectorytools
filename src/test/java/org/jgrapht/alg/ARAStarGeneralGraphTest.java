package org.jgrapht.alg;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.util.Heuristic;

public class ARAStarGeneralGraphTest extends AbstractGeneralGraphTest {

    @Override
    public void initialize() {
    }

    @Override
    GraphPath<Node, DefaultWeightedEdge> runTestedAlgorithm(
            Graph<Node, DefaultWeightedEdge> graph,
            Node start,
            Node end,
            GraphPath<Node, DefaultWeightedEdge> dijkstraPath) {

        ARAStarShortestPath<Node, DefaultWeightedEdge> araStar = new ARAStarShortestPath<Node, DefaultWeightedEdge>(graph, new Heuristic<Node>() {
            @Override
            public double getCostToGoalEstimate(Node current) {
                return 0;
            }
        }, start, end, 2, 0.02);

        ARAStarShortestPath.Result<Node, DefaultWeightedEdge> araResult;
        do {
            araResult = araStar.iterate();

            assertFalse(araResult.path == null && dijkstraPath != null);
            assertFalse(araResult.path != null && dijkstraPath == null);

            if (araResult.path == dijkstraPath) {
                break;
            }

            assertValidPath(start, end, araResult.path);
        } while (araResult.suboptimalityScale != 1 && !hasSameWeight(araResult.path, dijkstraPath));

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