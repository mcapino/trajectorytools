package cz.agents.alite.trajectorytools.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import cz.agents.alite.trajectorytools.graph.PlanarGraph;
import cz.agents.alite.trajectorytools.util.SpatialPoint;

public class PlanarGraphAddLineIntersectionTest {

    private SpatialPoint point1;
    private SpatialPoint point2;

    @Before
    public void setup() {
        point1 = new SpatialPoint(0, 0, 0);
        point2 = new SpatialPoint(5, 0, 0);
    }
    
    // +  intersection
    @Test
    public void testAddLineIntersection1() {
        List<SpatialPoint> line = new LinkedList<SpatialPoint>(Arrays.asList(new SpatialPoint(2, -2, 0), new SpatialPoint(2, 2, 0)));
        
        SpatialPoint intersection = PlanarGraph.addLineIntersection(point1, point2, line);
        
        assertNotNull(intersection);
        assertEquals(3, line.size());
        assertEquals(intersection, line.get(1));
        assertEquals(2, intersection.x, 0.001);
        assertEquals(0, intersection.y, 0.001);
    }

    // T  intersection
    @Test
    public void testAddLineIntersection2() {
        List<SpatialPoint> line = new LinkedList<SpatialPoint>(Arrays.asList(new SpatialPoint(2, 0, 0), new SpatialPoint(2, 2, 0)));
        
        SpatialPoint intersection = PlanarGraph.addLineIntersection(point1, point2, line);
        
        assertNotNull(intersection);
        assertEquals(line.toString(), 2, line.size());
        assertEquals(2, intersection.x, 0.001);
        assertEquals(0, intersection.y, 0.001);
    }

    // |- intersection
    @Test
    public void testAddLineIntersection3() {
        List<SpatialPoint> line = new LinkedList<SpatialPoint>(Arrays.asList(new SpatialPoint(0, -2, 0), new SpatialPoint(0, 2, 0)));
        
        SpatialPoint intersection = PlanarGraph.addLineIntersection(point1, point2, line);
        
        assertNotNull(intersection);
        assertEquals(3, line.size());
        assertEquals(intersection, line.get(1));
        assertEquals(intersection, point1);
    }

    // -| intersection
    @Test
    public void testAddLineIntersection3a() {
        List<SpatialPoint> line = new LinkedList<SpatialPoint>(Arrays.asList(new SpatialPoint(5, -2, 0), new SpatialPoint(5, 2, 0)));
        
        SpatialPoint intersection = PlanarGraph.addLineIntersection(point1, point2, line);
        
        assertNotNull(intersection);
        assertEquals(3, line.size());
        assertEquals(intersection, line.get(1));
        assertEquals(intersection, point2);
    }

    // ,- intersection
    @Test
    public void testAddLineIntersection4() {
        List<SpatialPoint> line = new LinkedList<SpatialPoint>(Arrays.asList(point1, new SpatialPoint(0, 2, 0)));
        
        SpatialPoint intersection = PlanarGraph.addLineIntersection(point1, point2, line);
        
        assertNotNull(intersection);
        assertEquals(2, line.size());
        assertEquals(intersection, point1);
    }

    // -, intersection
    @Test
    public void testAddLineIntersection4a() {
        List<SpatialPoint> line = new LinkedList<SpatialPoint>(Arrays.asList(point2, new SpatialPoint(5, 2, 0)));
        
        SpatialPoint intersection = PlanarGraph.addLineIntersection(point1, point2, line);
        
        assertNotNull(intersection);
        assertEquals(2, line.size());
        assertEquals(intersection, point2);
    }

    // `- intersection
    @Test
    public void testAddLineIntersection4b() {
        List<SpatialPoint> line = new LinkedList<SpatialPoint>(Arrays.asList(new SpatialPoint(0, -2, 0), point1));
        
        SpatialPoint intersection = PlanarGraph.addLineIntersection(point1, point2, line);
        
        assertNotNull(intersection);
        assertEquals(2, line.size());
        assertEquals(intersection, point1);
    }

    // -, intersection
    @Test
    public void testAddLineIntersection4v() {
        List<SpatialPoint> line = new LinkedList<SpatialPoint>(Arrays.asList(new SpatialPoint(5, -2, 0), point2));
        
        SpatialPoint intersection = PlanarGraph.addLineIntersection(point1, point2, line);
        
        assertNotNull(intersection);
        assertEquals(2, line.size());
        assertEquals(intersection, point2);
    }
}
