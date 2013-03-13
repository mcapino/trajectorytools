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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.AStarShortestPath.Heuristic;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.graph.WeightedPseudograph;
import org.junit.Test;

public class AStarAgainstDijkstraGeneralGraphTest
{

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
        for (int i=0; i<nVertices; i++) {
            graph.addVertex(new Node(i));
        }

        Node[] vertices = graph.vertexSet().toArray(new Node[nVertices]);

        for (int i=0; i<nEdges; i++) {
            Node startVertex = vertices[random.nextInt(vertices.length)];
            Node endVertex = vertices[random.nextInt(vertices.length)];
            DefaultWeightedEdge edge = graph.addEdge(startVertex, endVertex);
            graph.setEdgeWeight(edge, random.nextInt(1000));
        }

        return graph;
    }

    void assertAStarAndDijkstraConsistentOnTestSet(boolean directed, int trials, int nvertices, int nedges) {
           // Test directed graphs
        for (int seed = 0; seed < trials; seed++) {
            Random random = new Random(seed);
            Graph<Node, DefaultWeightedEdge> graph = createRandomGraph(directed, nvertices, nedges, random);

            Node[] vertices = graph.vertexSet().toArray(new Node[nvertices]);

            Node startVertex = vertices[random.nextInt(vertices.length)];
            Node endVertex = vertices[random.nextInt(vertices.length)];

            GraphPath<Node, DefaultWeightedEdge> aStarPath = new AStarShortestPath<Node, DefaultWeightedEdge>(graph, startVertex, endVertex, new Heuristic<Node>() {
                @Override
                public double getHeuristicEstimate(Node current, Node goal) {
                    return 0;
                }
            }).getPath();

            GraphPath<Node, DefaultWeightedEdge> dijkstraPath = new DijkstraShortestPath<Node, DefaultWeightedEdge>(graph, startVertex, endVertex).getPath();

            assertFalse(aStarPath == null && dijkstraPath != null);
            assertFalse(aStarPath != null && dijkstraPath == null);

            assertTrue(aStarPath == dijkstraPath || Math.abs(aStarPath.getWeight() - dijkstraPath.getWeight()) < 0.01);
        }
    }

    @Test
    public void test()
    {
        final int TRIALS = 5000;
        final int VERTICES = 500;
        final int EDGES = 250;

        // Check directed
        assertAStarAndDijkstraConsistentOnTestSet(true, TRIALS, VERTICES, EDGES);

        // Check undirected
        assertAStarAndDijkstraConsistentOnTestSet(false, TRIALS, VERTICES, EDGES);
     }
}

// End DijkstraShortestPathTest.java