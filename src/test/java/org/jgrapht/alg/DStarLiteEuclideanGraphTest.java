package org.jgrapht.alg;

import static org.junit.Assert.*;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.util.Heuristic;
import org.junit.Test;

public class DStarLiteEuclideanGraphTest {

    // Auxiliary class for creating random graphs that could be interpreted as "webs" in 2D plane
    class Point {

        double x;
        double y;

        public Point(double x, double y) {
            super();
            this.x = x;
            this.y = y;
        }

        public double euclideanDistance(Point goal) {
            return Math.sqrt(Math.pow(this.x - goal.x, 2) + Math.pow(this.y - goal.y, 2));
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }

    //~ Methods ----------------------------------------------------------------
    Graph<Point, DefaultWeightedEdge> createRandomGraph(boolean directed, int nVertices, int nEdges, Random random) {
        WeightedGraph<Point, DefaultWeightedEdge> graph;
        if (directed) {
            graph = new DirectedWeightedMultigraph<Point, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        } else {
            graph = new WeightedPseudograph<Point, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        }
        for (int i = 0; i < nVertices; i++) {
            graph.addVertex(new Point(Math.round(random.nextDouble() * 1000), Math.round(random.nextDouble() * 1000)));
        }

        Point[] vertices = graph.vertexSet().toArray(new Point[nVertices]);

        for (int i = 0; i < nEdges; i++) {
            Point startVertex = vertices[random.nextInt(vertices.length)];
            Point endVertex = vertices[random.nextInt(vertices.length)];
            DefaultWeightedEdge edge = graph.addEdge(startVertex, endVertex);
            graph.setEdgeWeight(edge, Math.sqrt(Math.pow(startVertex.x - endVertex.x, 2) + Math.pow(startVertex.y - endVertex.y, 2)));
        }

        return graph;
    }

    void assertARAStarAndDijkstraConsistentOnTestSet(boolean directed, int trials, int nvertices, int nedges) {
        // Test directed graphs
        for (int seed = 0; seed < trials; seed++) {
            Random random = new Random(seed);
            Graph<Point, DefaultWeightedEdge> graph = createRandomGraph(directed, nvertices, nedges, random);

            Point[] vertices = graph.vertexSet().toArray(new Point[nvertices]);

            Point startVertex = vertices[random.nextInt(vertices.length)];
            final Point endVertex = vertices[random.nextInt(vertices.length)];

            GraphPath<Point, DefaultWeightedEdge> dijkstraPath = new DijkstraShortestPath<Point, DefaultWeightedEdge>(graph, startVertex, endVertex).getPath();

            DStarLiteShortestPath<Point, DefaultWeightedEdge> dStarLite = new DStarLiteShortestPath<Point, DefaultWeightedEdge>(graph, new Heuristic<Point>() {
                @Override
                public double getCostToGoalEstimate(Point current) {
                    return current.euclideanDistance(endVertex);
                }
            }, startVertex, endVertex);

            GraphPath<Point, DefaultWeightedEdge> dStarLitePath = dStarLite.iterate();

            assertFalse(dStarLitePath == null && dijkstraPath != null);
            assertFalse(dStarLitePath != null && dijkstraPath == null);
            assertTrue(dStarLitePath == dijkstraPath || hasSameWeight(dStarLitePath, dijkstraPath));

        }
    }

    private boolean hasSameWeight(GraphPath<Point, DefaultWeightedEdge> pathA,
            GraphPath<Point, DefaultWeightedEdge> pathB) {
        return Math.abs(pathA.getWeight() - pathB.getWeight()) < 0.01;
    }

    @Test
    public void test() {
        final int TRIALS = 500;
        final int VERTICES = 100;
        final int EDGES = 200;

        // Check directed
        assertARAStarAndDijkstraConsistentOnTestSet(true, TRIALS, VERTICES, EDGES);

        // Check undirected
        assertARAStarAndDijkstraConsistentOnTestSet(false, TRIALS, VERTICES, EDGES);
    }
}

// End DijkstraShortestPathTest.java