package cz.agents.alite.trajectorytools.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import cz.agents.alite.trajectorytools.graph.PlanarGraph;
import cz.agents.alite.trajectorytools.util.Point;

public class PlanarGraphAddLineIntersectionTest {

    private Point point1;
    private Point point2;

    @Before
    public void setup() {
        point1 = new Point(0, 0, 0);
        point2 = new Point(5, 0, 0);
    }
    
    // +  intersection
    @Test
    public void testAddLineIntersection1() {
        List<Point> line = new LinkedList<Point>(Arrays.asList(new Point(2, -2, 0), new Point(2, 2, 0)));
        
        Point intersection = PlanarGraph.addLineIntersection(point1, point2, line);
        
        assertNotNull(intersection);
        assertEquals(3, line.size());
        assertEquals(intersection, line.get(1));
        assertEquals(2, intersection.x, 0.001);
        assertEquals(0, intersection.y, 0.001);
    }

    // T  intersection
    @Test
    public void testAddLineIntersection2() {
        List<Point> line = new LinkedList<Point>(Arrays.asList(new Point(2, 0, 0), new Point(2, 2, 0)));
        
        Point intersection = PlanarGraph.addLineIntersection(point1, point2, line);
        
        assertNotNull(intersection);
        assertEquals(line.toString(), 2, line.size());
        assertEquals(2, intersection.x, 0.001);
        assertEquals(0, intersection.y, 0.001);
    }

    // |- intersection
    @Test
    public void testAddLineIntersection3() {
        List<Point> line = new LinkedList<Point>(Arrays.asList(new Point(0, -2, 0), new Point(0, 2, 0)));
        
        Point intersection = PlanarGraph.addLineIntersection(point1, point2, line);
        
        assertNotNull(intersection);
        assertEquals(3, line.size());
        assertEquals(intersection, line.get(1));
        assertEquals(intersection, point1);
    }

    // -| intersection
    @Test
    public void testAddLineIntersection3a() {
        List<Point> line = new LinkedList<Point>(Arrays.asList(new Point(5, -2, 0), new Point(5, 2, 0)));
        
        Point intersection = PlanarGraph.addLineIntersection(point1, point2, line);
        
        assertNotNull(intersection);
        assertEquals(3, line.size());
        assertEquals(intersection, line.get(1));
        assertEquals(intersection, point2);
    }

    // ,- intersection
    @Test
    public void testAddLineIntersection4() {
        List<Point> line = new LinkedList<Point>(Arrays.asList(point1, new Point(0, 2, 0)));
        
        Point intersection = PlanarGraph.addLineIntersection(point1, point2, line);
        
        assertNotNull(intersection);
        assertEquals(2, line.size());
        assertEquals(intersection, point1);
    }

    // -, intersection
    @Test
    public void testAddLineIntersection4a() {
        List<Point> line = new LinkedList<Point>(Arrays.asList(point2, new Point(5, 2, 0)));
        
        Point intersection = PlanarGraph.addLineIntersection(point1, point2, line);
        
        assertNotNull(intersection);
        assertEquals(2, line.size());
        assertEquals(intersection, point2);
    }

    // `- intersection
    @Test
    public void testAddLineIntersection4b() {
        List<Point> line = new LinkedList<Point>(Arrays.asList(new Point(0, -2, 0), point1));
        
        Point intersection = PlanarGraph.addLineIntersection(point1, point2, line);
        
        assertNotNull(intersection);
        assertEquals(2, line.size());
        assertEquals(intersection, point1);
    }

    // -, intersection
    @Test
    public void testAddLineIntersection4v() {
        List<Point> line = new LinkedList<Point>(Arrays.asList(new Point(5, -2, 0), point2));
        
        Point intersection = PlanarGraph.addLineIntersection(point1, point2, line);
        
        assertNotNull(intersection);
        assertEquals(2, line.size());
        assertEquals(intersection, point2);
    }
}
