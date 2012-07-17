package cz.agents.alite.trajectorytools.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import cz.agents.alite.trajectorytools.graph.PlanarGraph;
import cz.agents.alite.trajectorytools.util.Waypoint;

public class PlanarGraphAddLineIntersectionTest {

    private Waypoint point1;
    private Waypoint point2;

    @Before
    public void setup() {
        point1 = new Waypoint(0, 0);
        point2 = new Waypoint(5, 0);
    }
    
    // +  intersection
    @Test
    public void testAddLineIntersection1() {
        List<Waypoint> line = new LinkedList<Waypoint>(Arrays.asList(new Waypoint(2, -2), new Waypoint(2, 2)));
        
        Waypoint intersection = PlanarGraph.addLineIntersection(point1, point2, line);
        
        assertNotNull(intersection);
        assertEquals(3, line.size());
        assertEquals(intersection, line.get(1));
        assertEquals(2, intersection.x, 0.001);
        assertEquals(0, intersection.y, 0.001);
    }

    // T  intersection
    @Test
    public void testAddLineIntersection2() {
        List<Waypoint> line = new LinkedList<Waypoint>(Arrays.asList(new Waypoint(2, 0), new Waypoint(2, 2)));
        
        Waypoint intersection = PlanarGraph.addLineIntersection(point1, point2, line);
        
        assertNotNull(intersection);
        assertEquals(line.toString(), 2, line.size());
        assertEquals(2, intersection.x, 0.001);
        assertEquals(0, intersection.y, 0.001);
    }

    // |- intersection
    @Test
    public void testAddLineIntersection3() {
        List<Waypoint> line = new LinkedList<Waypoint>(Arrays.asList(new Waypoint(0, -2), new Waypoint(0, 2)));
        
        Waypoint intersection = PlanarGraph.addLineIntersection(point1, point2, line);
        
        assertNotNull(intersection);
        assertEquals(3, line.size());
        assertEquals(intersection, line.get(1));
        assertEquals(intersection, point1);
    }

    // -| intersection
    @Test
    public void testAddLineIntersection3a() {
        List<Waypoint> line = new LinkedList<Waypoint>(Arrays.asList(new Waypoint(5, -2), new Waypoint(5, 2)));
        
        Waypoint intersection = PlanarGraph.addLineIntersection(point1, point2, line);
        
        assertNotNull(intersection);
        assertEquals(3, line.size());
        assertEquals(intersection, line.get(1));
        assertEquals(intersection, point2);
    }

    // ,- intersection
    @Test
    public void testAddLineIntersection4() {
        List<Waypoint> line = new LinkedList<Waypoint>(Arrays.asList(point1, new Waypoint(0, 2)));
        
        Waypoint intersection = PlanarGraph.addLineIntersection(point1, point2, line);
        
        assertNotNull(intersection);
        assertEquals(2, line.size());
        assertEquals(intersection, point1);
    }

    // -, intersection
    @Test
    public void testAddLineIntersection4a() {
        List<Waypoint> line = new LinkedList<Waypoint>(Arrays.asList(point2, new Waypoint(5, 2)));
        
        Waypoint intersection = PlanarGraph.addLineIntersection(point1, point2, line);
        
        assertNotNull(intersection);
        assertEquals(2, line.size());
        assertEquals(intersection, point2);
    }

    // `- intersection
    @Test
    public void testAddLineIntersection4b() {
        List<Waypoint> line = new LinkedList<Waypoint>(Arrays.asList(new Waypoint(0, -2), point1));
        
        Waypoint intersection = PlanarGraph.addLineIntersection(point1, point2, line);
        
        assertNotNull(intersection);
        assertEquals(2, line.size());
        assertEquals(intersection, point1);
    }

    // -, intersection
    @Test
    public void testAddLineIntersection4v() {
        List<Waypoint> line = new LinkedList<Waypoint>(Arrays.asList(new Waypoint(5, -2), point2));
        
        Waypoint intersection = PlanarGraph.addLineIntersection(point1, point2, line);
        
        assertNotNull(intersection);
        assertEquals(2, line.size());
        assertEquals(intersection, point2);
    }
}
