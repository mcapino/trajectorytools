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

import org.jgrapht.alg.planning.ARAStarShortestPath;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.graph.WeightedPseudograph;
import org.jgrapht.util.Heuristic;
import org.junit.Test;

public class ARAStarGeneralGraphTest {

    // Auxiliary class for creating random graphs
    class Node {

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

    //~ Methods ----------------------------------------------------------------
    Graph<Node, DefaultWeightedEdge> createRandomGraph(boolean directed, int nVertices, int nEdges, Random random) {
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

    void assertARAStarAndDijkstraConsistentOnTestSet(boolean directed, int trials, int nvertices, int nedges) {
        // Test directed graphs
        for (int seed = 0; seed < trials; seed++) {
            Random random = new Random(seed);
            Graph<Node, DefaultWeightedEdge> graph = createRandomGraph(directed, nvertices, nedges, random);

            Node[] vertices = graph.vertexSet().toArray(new Node[nvertices]);

            Node startVertex = vertices[random.nextInt(vertices.length)];
            Node endVertex = vertices[random.nextInt(vertices.length)];

            GraphPath<Node, DefaultWeightedEdge> dijkstraPath = new DijkstraShortestPath<Node, DefaultWeightedEdge>(graph, startVertex, endVertex).getPath();
            ARAStarShortestPath<Node, DefaultWeightedEdge> araStar = new ARAStarShortestPath<Node, DefaultWeightedEdge>(graph, new Heuristic<Node>() {
                @Override
                public double getCostToGoalEstimate(Node current) {
                    return 0;
                }
            }, startVertex, endVertex, 2, 0.02);

            ARAStarShortestPath<Node, DefaultWeightedEdge>.Result<Node, DefaultWeightedEdge> araResult;
            do {
                araResult = araStar.iterate();

                assertFalse(araResult.path == null && dijkstraPath != null);
                assertFalse(araResult.path != null && dijkstraPath == null);

                if (araResult.path == dijkstraPath) {
                    break;
                }

                assertValidPath(startVertex, endVertex, araResult.path);
            } while (araResult.suboptimalityScale != 1 && !hasSameWeight(araResult.path, dijkstraPath));

            assertTrue(araResult.path == dijkstraPath || hasSameWeight(araResult.path, dijkstraPath));
        }
    }

    private boolean hasSameWeight(GraphPath<Node, DefaultWeightedEdge> pathA,
            GraphPath<Node, DefaultWeightedEdge> pathB) {
        return Math.abs(pathA.getWeight() - pathB.getWeight()) < 0.01;
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