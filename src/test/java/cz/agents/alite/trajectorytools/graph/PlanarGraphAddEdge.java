package cz.agents.alite.trajectorytools.graph;

import static org.junit.Assert.assertEquals;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.junit.Before;
import org.junit.Test;

import cz.agents.alite.trajectorytools.util.SpatialPoint;

public class PlanarGraphAddEdge {

    private PlanarGraph graph;

    private SpatialPoint point1 = new SpatialPoint(2, 0, 0);
    private SpatialPoint point2 = new SpatialPoint(0, 2, 0);
    private SpatialPoint point3 = new SpatialPoint(-2, 0, 0);
    private SpatialPoint point4 = new SpatialPoint(0, -2, 0);

    @Before
    public void setup() {
        graph = PlanarGraph.createPlanarGraphCopy( new DirectedWeightedMultigraph<SpatialPoint, DefaultWeightedEdge>(DefaultWeightedEdge.class));
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
        SpatialPoint start = new SpatialPoint(0, 0, 0);
        SpatialPoint target = new SpatialPoint(4, 4, 0);

        graph.addEdge(start, target);

        assertEquals(graph.vertexSet().toString(), 7, graph.vertexSet().size());
        assertEquals(graph.edgeSet().toString(), 7, graph.edgeSet().size());
    }

    // ++  intersection
    @Test
    public void testAddEdge1a() {
        SpatialPoint start = new SpatialPoint(-4, -4, 0);
        SpatialPoint target = new SpatialPoint(4, 4, 0);

        graph.addEdge(start, target);

        assertEquals(8, graph.vertexSet().size());
        assertEquals(9, graph.edgeSet().size());
    }

    // |<  intersection
    @Test
    public void testAddEdge2() {
        SpatialPoint start = new SpatialPoint(-2, -4, 0);
        SpatialPoint target = new SpatialPoint(-2, 4, 0);

        graph.addEdge(start, target);

        assertEquals(6, graph.vertexSet().size());
        assertEquals(6, graph.edgeSet().size());
    }

    // -< intersection
    @Test
    public void testAddEdge3() {
        SpatialPoint start = new SpatialPoint(-4, 0, 0);
        SpatialPoint target = point3;

        graph.addEdge(start, target);

        assertEquals(5, graph.vertexSet().size());
        assertEquals(5, graph.edgeSet().size());
    }

    // -| intersection

    // ,- intersection
}
