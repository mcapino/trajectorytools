/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */
/* ------------------------------
 * DijkstraShortestPathTest.java
 * ------------------------------
 * (C) Copyright 2003-2008, by John V. Sichi and Contributors.
 *
 * Original Author:  John V. Sichi
 * Contributor(s):   -
 *
 * $Id$
 *
 * Changes
 * -------
 * 03-Sept-2003 : Initial revision (JVS);
 * 14-Jan-2006 : Factored out ShortestPathTestCase (JVS);
 *
 */
package org.jgrapht.alg;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.graph.WeightedPseudograph;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public abstract class AbstractEuclideanGraphTest {

    private long timerStarted;
    protected long referenceOverallTime = 0;
    protected long testedOverallTime = 0;
    protected int TRIALS = 500;
    protected int VERTICES = 100;
    protected int EDGES = 150;
    protected boolean debugOut;

    @Before
    public abstract void initialize();

    @After
    public abstract void after();

    protected abstract GraphPath<Point, DefaultWeightedEdge> runTestedAlgorithm(
            ShortestPathProblem<Point, DefaultWeightedEdge> problem,
            GraphPath<Point, DefaultWeightedEdge> referencePath);

    protected GraphPath<Point, DefaultWeightedEdge> runReferenceAlgorithm(
            ShortestPathProblem<Point, DefaultWeightedEdge> problem) {

        return new DijkstraShortestPath<Point, DefaultWeightedEdge>(problem.graph, problem.startVertex, problem.endVertex).getPath();
    }

    protected void startTimer() {
        timerStarted = System.currentTimeMillis();
    }

    protected long stopTimer() {
        return System.currentTimeMillis() - timerStarted;
    }

    //~ Methods ----------------------------------------------------------------
    private Graph<Point, DefaultWeightedEdge> createRandomGraph(boolean directed, int nVertices, int nEdges, Random random) {

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

    private void testConsistencyWithDijkstra(boolean directed, int trials, int nvertices, int nedges) {
        // Test directed graphs
        for (int seed = 0; seed < trials; seed++) {
            if (debugOut) {
                System.out.printf("Trial %d/%d \n", seed, trials);
            }

            Random random = new Random(seed);
            Graph<Point, DefaultWeightedEdge> graph = createRandomGraph(directed, nvertices, nedges, random);

            Point[] vertices = graph.vertexSet().toArray(new Point[nvertices]);

            Point startVertex = vertices[random.nextInt(vertices.length)];
            final Point endVertex = vertices[random.nextInt(vertices.length)];

            ShortestPathProblem<Point, DefaultWeightedEdge> problem =
                    new ShortestPathProblem<Point, DefaultWeightedEdge>(graph, startVertex, endVertex);

            testOnInstance(problem);
        }
    }

    protected void testOnInstance(ShortestPathProblem<Point, DefaultWeightedEdge> problem) {
        startTimer();
        GraphPath<Point, DefaultWeightedEdge> referencePath = runReferenceAlgorithm(problem);
        referenceOverallTime += stopTimer();

        startTimer();
        GraphPath<Point, DefaultWeightedEdge> testedAlgPath = runTestedAlgorithm(problem, referencePath);
        testedOverallTime += stopTimer();

        assertPathsConsistent(referencePath, testedAlgPath);
    }

    protected void assertPathsConsistent(GraphPath<Point, DefaultWeightedEdge> referencePath, GraphPath<Point, DefaultWeightedEdge> testedAlgPath) {
        assertFalse(testedAlgPath == null && referencePath != null);
        assertFalse(testedAlgPath != null && referencePath == null);

        if (testedAlgPath != null && referencePath != null) {
            assertTrue(hasSameWeight(referencePath, testedAlgPath));
            //System.out.println(String.format("Paths consistent: %f %f", referencePath.getWeight(), testedAlgPath.getWeight()));
        }
    }

    protected void printMeasuredTimes() {
        System.out.println(String.format("Overall times ---- Reference: %d,  Tested: %d", referenceOverallTime, testedOverallTime));
    }

    protected boolean hasSameWeight(GraphPath<Point, DefaultWeightedEdge> pathA,
                                    GraphPath<Point, DefaultWeightedEdge> pathB) {
        double weightA = pathA.getWeight();
        double weightB = pathB.getWeight();
        double mean = (weightA + weightB) / 2;

        return Math.abs(weightA - weightB) <= 0.0001*mean;
    }

    @Test
    public void test() {
        // Check directed
        testConsistencyWithDijkstra(true, TRIALS, VERTICES, EDGES);

        // Check undirected
        testConsistencyWithDijkstra(false, TRIALS, VERTICES, EDGES);
    }

    // Auxiliary class for creating random graphs that could be interpreted as "webs" in 2D plane
    protected static class Point {

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
}

// End DijkstraShortestPathTest.java