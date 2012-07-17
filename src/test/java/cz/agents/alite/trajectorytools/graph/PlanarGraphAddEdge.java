package cz.agents.alite.trajectorytools.graph;

import static org.junit.Assert.assertEquals;

import org.jgrapht.DummyEdgeFactory;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.junit.Before;
import org.junit.Test;

import cz.agents.alite.trajectorytools.graph.spatial.maneuvers.SpatialManeuver;
import cz.agents.alite.trajectorytools.graph.spatial.maneuvers.Straight;
import cz.agents.alite.trajectorytools.util.Waypoint;

public class PlanarGraphAddEdge {

    private PlanarGraph<Waypoint, SpatialManeuver> graph;

    private Waypoint point1 = new Waypoint(2, 0);
    private Waypoint point2 = new Waypoint(0, 2);
    private Waypoint point3 = new Waypoint(-2, 0);
    private Waypoint point4 = new Waypoint(0, -2);

    @Before
    public void setup() {
        graph = new PlanarGraph<Waypoint, SpatialManeuver>(new DirectedWeightedMultigraph<Waypoint, SpatialManeuver>(new DummyEdgeFactory<Waypoint, SpatialManeuver>()));
        graph.addVertex( point1 );
        graph.addVertex( point2 );
        graph.addVertex( point3 );
        graph.addVertex( point4 );

        graph.addEdge(point1, point2, new Straight(point1, point2, 1.0));
        graph.addEdge(point2, point3, new Straight(point2, point3, 1.0));
        graph.addEdge(point3, point4, new Straight(point3, point4, 1.0));
        graph.addEdge(point4, point1, new Straight(point4, point1, 1.0));
    }

    @Test
    public void testSetup() {
        assertEquals(graph.vertexSet().toString(), 4, graph.vertexSet().size());
        assertEquals(graph.edgeSet().toString(), 4, graph.edgeSet().size());
    }

    // +  intersection
    @Test
    public void testAddEdge1() {
        Waypoint start = new Waypoint(0, 0);
        Waypoint target = new Waypoint(4, 4);

        graph.addEdge(start, target, new Straight(start, target, 1.0));

        assertEquals(graph.vertexSet().toString(), 7, graph.vertexSet().size());
        assertEquals(graph.edgeSet().toString(), 7, graph.edgeSet().size());
    }

    // ++  intersection
    @Test
    public void testAddEdge1a() {
        Waypoint start = new Waypoint(-4, -4);
        Waypoint target = new Waypoint(4, 4);

        graph.addEdge(start, target, new Straight(start, target, 1.0));

        assertEquals(8, graph.vertexSet().size());
        assertEquals(9, graph.edgeSet().size());
    }

    // |<  intersection
    @Test
    public void testAddEdge2() {
        Waypoint start = new Waypoint(-2, -4);
        Waypoint target = new Waypoint(-2, 4);

        graph.addEdge(start, target, new Straight(start, target, 1.0));

        assertEquals(6, graph.vertexSet().size());
        assertEquals(6, graph.edgeSet().size());
    }

    // -< intersection
    @Test
    public void testAddEdge3() {
        Waypoint start = new Waypoint(-4, 0);
        Waypoint target = point3;

        graph.addEdge(start, target, new Straight(start, target, 1.0));

        assertEquals(5, graph.vertexSet().size());
        assertEquals(5, graph.edgeSet().size());
    }

    // -| intersection

    // ,- intersection
}
