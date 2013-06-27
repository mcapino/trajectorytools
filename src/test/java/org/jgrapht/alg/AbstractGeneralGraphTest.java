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
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public abstract class AbstractGeneralGraphTest {

    protected long referenceOverallTime = 0;
    protected long testedOverallTime = 0;
    protected int TRIALS = 500;
    protected int VERTICES = 100;
    protected int EDGES = 150;
    protected boolean debugOut;
    private long timerStarted;

    abstract GraphPath<Node, DefaultWeightedEdge> runTestedAlgorithm(
            ShortestPathProblem<Node, DefaultWeightedEdge> problem,
            GraphPath<Node, DefaultWeightedEdge> dijkstraPath);

    protected GraphPath<Node, DefaultWeightedEdge> runReferenceAlgorithm(
            ShortestPathProblem<Node, DefaultWeightedEdge> problem) {

        return new DijkstraShortestPath<Node, DefaultWeightedEdge>(problem.graph, problem.startVertex, problem.endVertex).getPath();
    }

    @Before
    public abstract void initialize();

    protected void startTimer() {
        timerStarted = System.currentTimeMillis();
    }

    protected long stopTimer() {
        return System.currentTimeMillis() - timerStarted;
    }

    private Graph<Node, DefaultWeightedEdge> createRandomGraph(boolean directed, int nVertices, int nEdges, Random random) {
        WeightedGraph<Node, DefaultWeightedEdge> graph;
        if (directed) {
            graph = new DirectedWeightedMultigraph<Node, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        } else {
            graph = new WeightedPseudograph<Node, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        }
        for (int i = 0; i < nVertices; i++) {
            graph.addVertex(new Node(i));
        }

        Node[] vertices = graph.vertexSet().toArray(new Node[nVertices]);

        for (int i = 0; i < nEdges; i++) {
            Node startVertex = vertices[random.nextInt(vertices.length)];
            Node endVertex = vertices[random.nextInt(vertices.length)];
            DefaultWeightedEdge edge = graph.addEdge(startVertex, endVertex);
            graph.setEdgeWeight(edge, random.nextInt(1000));
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
            Graph<Node, DefaultWeightedEdge> graph = createRandomGraph(directed, nvertices, nedges, random);

            Node[] vertices = graph.vertexSet().toArray(new Node[nvertices]);

            Node startVertex = vertices[random.nextInt(vertices.length)];
            Node endVertex = vertices[random.nextInt(vertices.length)];


            ShortestPathProblem<Node, DefaultWeightedEdge> problem =
                    new ShortestPathProblem<Node, DefaultWeightedEdge>(graph, startVertex, endVertex);

            testOnInstance(problem);
        }
    }

    protected void testOnInstance(ShortestPathProblem<Node, DefaultWeightedEdge> problem) {
        startTimer();
        GraphPath<Node, DefaultWeightedEdge> referencePath = runReferenceAlgorithm(problem);
        referenceOverallTime += stopTimer();

        startTimer();
        GraphPath<Node, DefaultWeightedEdge> testedAlgPath = runTestedAlgorithm(problem, referencePath);
        testedOverallTime += stopTimer();

        assertPathsConsistent(referencePath, testedAlgPath);
    }

    protected void assertPathsConsistent(GraphPath<Node, DefaultWeightedEdge> referencePath, GraphPath<Node, DefaultWeightedEdge> testedAlgPath) {
        assertFalse(testedAlgPath == null && referencePath != null);
        assertFalse(testedAlgPath != null && referencePath == null);
        assertTrue(testedAlgPath == referencePath || hasSameWeight(referencePath, testedAlgPath));
    }

    protected void printMeasuredTimes() {
        System.out.println(String.format("Overall times ---- Reference: %d,  Tested: %d", referenceOverallTime, testedOverallTime));
    }

    protected boolean hasSameWeight(GraphPath<Node, DefaultWeightedEdge> pathA,
                                    GraphPath<Node, DefaultWeightedEdge> pathB) {
        return Math.abs(pathA.getWeight() - pathB.getWeight()) < 0.01;
    }

    @Test
    public void test() {
        // Check directed
        testConsistencyWithDijkstra(true, TRIALS, VERTICES, EDGES);

        // Check undirected
        testConsistencyWithDijkstra(false, TRIALS, VERTICES, EDGES);
    }

    // Auxiliary class for creating random graphs
    protected static class Node {

        int id;

        public Node(int id) {
            super();
            this.id = id;
        }

        @Override
        public String toString() {
            return "(" + id + ")";
        }
    }
}

// End DijkstraShortestPathTest.java