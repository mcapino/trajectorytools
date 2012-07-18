package cz.agents.alite.trajectorytools.graph;

import static org.junit.Assert.assertEquals;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.junit.Before;
import org.junit.Test;

import cz.agents.alite.trajectorytools.util.Point;

public class PlanarGraphAddEdge {

    private PlanarGraph graph;

    private Point point1 = new Point(2, 0, 0);
    private Point point2 = new Point(0, 2, 0);
    private Point point3 = new Point(-2, 0, 0);
    private Point point4 = new Point(0, -2, 0);

    @Before
    public void setup() {
        graph = PlanarGraph.createPlanarGraphCopy( new DirectedWeightedMultigraph<Point, DefaultWeightedEdge>(DefaultWeightedEdge.class));
        graph.addVertex( point1 );
        graph.addVertex( point2 );
        graph.addVertex( point3 );
        graph.addVertex( point4 );

        graph.addEdge(point1, point2);
        graph.addEdge(point2, point3);
        graph.addEdge(point3, point4);
        graph.addEdge(point4, point1);
    }

    @Test
    public void testSetup() {
        assertEquals(graph.vertexSet().toString(), 4, graph.vertexSet().size());
        assertEquals(graph.edgeSet().toString(), 4, graph.edgeSet().size());
    }

    // +  intersection
    @Test
    public void testAddEdge1() {
        Point start = new Point(0, 0, 0);
        Point target = new Point(4, 4, 0);

        graph.addEdge(start, target);

        assertEquals(graph.vertexSet().toString(), 7, graph.vertexSet().size());
        assertEquals(graph.edgeSet().toString(), 7, graph.edgeSet().size());
    }

    // ++  intersection
    @Test
    public void testAddEdge1a() {
        Point start = new Point(-4, -4, 0);
        Point target = new Point(4, 4, 0);

        graph.addEdge(start, target);

        assertEquals(8, graph.vertexSet().size());
        assertEquals(9, graph.edgeSet().size());
    }

    // |<  intersection
    @Test
    public void testAddEdge2() {
        Point start = new Point(-2, -4, 0);
        Point target = new Point(-2, 4, 0);

        graph.addEdge(start, target);

        assertEquals(6, graph.vertexSet().size());
        assertEquals(6, graph.edgeSet().size());
    }

    // -< intersection
    @Test
    public void testAddEdge3() {
        Point start = new Point(-4, 0, 0);
        Point target = point3;

        graph.addEdge(start, target);

        assertEquals(5, graph.vertexSet().size());
        assertEquals(5, graph.edgeSet().size());
    }

    // -| intersection

    // ,- intersection
}
