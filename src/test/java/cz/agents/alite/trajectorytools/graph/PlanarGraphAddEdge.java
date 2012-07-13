package cz.agents.alite.trajectorytools.graph;

import static org.junit.Assert.assertEquals;

import org.jgrapht.graph.SimpleGraph;
import org.junit.Before;
import org.junit.Test;

import cz.agents.alite.trajectorytools.graph.PlanarGraph;
import cz.agents.alite.trajectorytools.graph.spatial.DefaultSpatialManeuverGraph;
import cz.agents.alite.trajectorytools.graph.spatial.SpatialWaypoint;
import cz.agents.alite.trajectorytools.graph.spatial.maneuvers.SpatialManeuver;

public class PlanarGraphAddEdge {

    private PlanarGraph<SpatialManeuver> graph;
    
    private SpatialWaypoint point1 = new SpatialWaypoint(2, 0);
    private SpatialWaypoint point2 = new SpatialWaypoint(0, 2);
    private SpatialWaypoint point3 = new SpatialWaypoint(-2, 0);
    private SpatialWaypoint point4 = new SpatialWaypoint(0, -2);

    @Before
    public void setup() {
        graph = new PlanarGraph<SpatialManeuver>(new DefaultSpatialManeuverGraph());
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
        SpatialWaypoint start = new SpatialWaypoint(0, 0);
        SpatialWaypoint target = new SpatialWaypoint(4, 4);
        
        graph.addEdge(start, target);
        
        assertEquals(graph.vertexSet().toString(), 7, graph.vertexSet().size());
        assertEquals(graph.edgeSet().toString(), 7, graph.edgeSet().size());
    }

    // ++  intersection
    @Test
    public void testAddEdge1a() {
        SpatialWaypoint start = new SpatialWaypoint(-4, -4);
        SpatialWaypoint target = new SpatialWaypoint(4, 4);
        
        graph.addEdge(start, target);
        
        assertEquals(8, graph.vertexSet().size());
        assertEquals(9, graph.edgeSet().size());
    }

    // |<  intersection
    @Test
    public void testAddEdge2() {
        SpatialWaypoint start = new SpatialWaypoint(-2, -4);
        SpatialWaypoint target = new SpatialWaypoint(-2, 4);
        
        graph.addEdge(start, target);
        
        assertEquals(6, graph.vertexSet().size());
        assertEquals(6, graph.edgeSet().size());
    }

    // -< intersection
    @Test
    public void testAddEdge3() {
        SpatialWaypoint start = new SpatialWaypoint(-4, 0);
        SpatialWaypoint target = point3;
        
        graph.addEdge(start, target);
        
        assertEquals(5, graph.vertexSet().size());
        assertEquals(5, graph.edgeSet().size());
    }

    // -| intersection

    // ,- intersection
}
