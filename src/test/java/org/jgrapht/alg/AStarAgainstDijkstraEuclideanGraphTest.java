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

import static org.junit.Assert.*;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.util.Heuristic;
import org.junit.Test;

public class AStarAgainstDijkstraEuclideanGraphTest {

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

    void assertAStarAndDijkstraConsistentOnTestSet(boolean directed, int trials, int nvertices, int nedges) {
        // Test directed graphs
        for (int seed = 0; seed < trials; seed++) {
            //System.out.printf("Trial %d/%d \n", seed, trials);

            Random random = new Random(seed);
            Graph<Point, DefaultWeightedEdge> graph = createRandomGraph(directed, nvertices, nedges, random);

            Point[] vertices = graph.vertexSet().toArray(new Point[nvertices]);

            Point startVertex = vertices[random.nextInt(vertices.length)];
            final Point endVertex = vertices[random.nextInt(vertices.length)];

            GraphPath<Point, DefaultWeightedEdge> dijkstraPath = new DijkstraShortestPath<Point, DefaultWeightedEdge>(graph, startVertex, endVertex).getPath();
            GraphPath<Point, DefaultWeightedEdge> aStarFibanaci = AStarShortestPath.findPathBetween(graph, new Heuristic<Point>() {
                @Override
                public double getCostToGoalEstimate(Point current) {
                    return current.euclideanDistance(endVertex);
                }
            }, startVertex, endVertex);

            assertFalse(aStarFibanaci == null && dijkstraPath != null);
            assertFalse(aStarFibanaci != null && dijkstraPath == null);
            assertTrue(aStarFibanaci == dijkstraPath || Math.abs(aStarFibanaci.getWeight() - dijkstraPath.getWeight()) < 0.01);
        }
    }

    @Test
    public void test() {
        final int TRIALS = 500;
        final int VERTICES = 100;
        final int EDGES = 150;

        // Check directed
        assertAStarAndDijkstraConsistentOnTestSet(true, TRIALS, VERTICES, EDGES);

        // Check undirected
        assertAStarAndDijkstraConsistentOnTestSet(false, TRIALS, VERTICES, EDGES);
    }
}

// End DijkstraShortestPathTest.java