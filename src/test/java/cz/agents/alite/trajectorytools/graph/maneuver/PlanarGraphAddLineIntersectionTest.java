package cz.agents.alite.trajectorytools.graph.maneuver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import cz.agents.alite.trajectorytools.graph.spatialwaypoint.SpatialWaypoint;

public class PlanarGraphAddLineIntersectionTest {

    private SpatialWaypoint point1;
    private SpatialWaypoint point2;

    @Before
    public void setup() {
        point1 = new SpatialWaypoint(0, 0);
        point2 = new SpatialWaypoint(5, 0);
    }
    
    // +  intersection
    @Test
    public void testAddLineIntersection1() {
        List<SpatialWaypoint> line = new LinkedList<SpatialWaypoint>(Arrays.asList(new SpatialWaypoint(2, -2), new SpatialWaypoint(2, 2)));
        
        SpatialWaypoint intersection = PlanarGraph.addLineIntersection(point1, point2, line);
        
        assertNotNull(intersection);
        assertEquals(3, line.size());
        assertEquals(intersection, line.get(1));
        assertEquals(2, intersection.x, 0.001);
        assertEquals(0, intersection.y, 0.001);
    }

    // T  intersection
    @Test
    public void testAddLineIntersection2() {
        List<SpatialWaypoint> line = new LinkedList<SpatialWaypoint>(Arrays.asList(new SpatialWaypoint(2, 0), new SpatialWaypoint(2, 2)));
        
        SpatialWaypoint intersection = PlanarGraph.addLineIntersection(point1, point2, line);
        
        assertNotNull(intersection);
        assertEquals(line.toString(), 2, line.size());
        assertEquals(2, intersection.x, 0.001);
        assertEquals(0, intersection.y, 0.001);
    }

    // |- intersection
    @Test
    public void testAddLineIntersection3() {
        List<SpatialWaypoint> line = new LinkedList<SpatialWaypoint>(Arrays.asList(new SpatialWaypoint(0, -2), new SpatialWaypoint(0, 2)));
        
        SpatialWaypoint intersection = PlanarGraph.addLineIntersection(point1, point2, line);
        
        assertNotNull(intersection);
        assertEquals(3, line.size());
        assertEquals(intersection, line.get(1));
        assertEquals(intersection, point1);
    }

    // -| intersection
    @Test
    public void testAddLineIntersection3a() {
        List<SpatialWaypoint> line = new LinkedList<SpatialWaypoint>(Arrays.asList(new SpatialWaypoint(5, -2), new SpatialWaypoint(5, 2)));
        
        SpatialWaypoint intersection = PlanarGraph.addLineIntersection(point1, point2, line);
        
        assertNotNull(intersection);
        assertEquals(3, line.size());
        assertEquals(intersection, line.get(1));
        assertEquals(intersection, point2);
    }

    // ,- intersection
    @Test
    public void testAddLineIntersection4() {
        List<SpatialWaypoint> line = new LinkedList<SpatialWaypoint>(Arrays.asList(point1, new SpatialWaypoint(0, 2)));
        
        SpatialWaypoint intersection = PlanarGraph.addLineIntersection(point1, point2, line);
        
        assertNotNull(intersection);
        assertEquals(2, line.size());
        assertEquals(intersection, point1);
    }

    // -, intersection
    @Test
    public void testAddLineIntersection4a() {
        List<SpatialWaypoint> line = new LinkedList<SpatialWaypoint>(Arrays.asList(point2, new SpatialWaypoint(5, 2)));
        
        SpatialWaypoint intersection = PlanarGraph.addLineIntersection(point1, point2, line);
        
        assertNotNull(intersection);
        assertEquals(2, line.size());
        assertEquals(intersection, point2);
    }

    // `- intersection
    @Test
    public void testAddLineIntersection4b() {
        List<SpatialWaypoint> line = new LinkedList<SpatialWaypoint>(Arrays.asList(new SpatialWaypoint(0, -2), point1));
        
        SpatialWaypoint intersection = PlanarGraph.addLineIntersection(point1, point2, line);
        
        assertNotNull(intersection);
        assertEquals(2, line.size());
        assertEquals(intersection, point1);
    }

    // -, intersection
    @Test
    public void testAddLineIntersection4v() {
        List<SpatialWaypoint> line = new LinkedList<SpatialWaypoint>(Arrays.asList(new SpatialWaypoint(5, -2), point2));
        
        SpatialWaypoint intersection = PlanarGraph.addLineIntersection(point1, point2, line);
        
        assertNotNull(intersection);
        assertEquals(2, line.size());
        assertEquals(intersection, point2);
    }
}
