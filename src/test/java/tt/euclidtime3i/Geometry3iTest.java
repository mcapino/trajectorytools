package tt.euclidtime3i;

import org.junit.Test;
import tt.euclid2i.Point;
import tt.euclidtime3i.discretization.Straight;

import static org.junit.Assert.assertEquals;

public class Geometry3iTest {

    private static final double PRECISION = 0.01;

    @Test
    public void testDistanceDisjoint() {
        Straight a = new Straight(new Point(0, -1), -50, new Point(0, 3), 150);
        Straight b = new Straight(new Point(2, 0), 0, new Point(0, 0), 100);
        double distance = Geometry3i.distance(a, b);
        double expected = Math.sqrt(2);

        assertEquals(distance, expected, PRECISION);
    }

    @Test
    public void testDistanceIntersects() {
        Straight a = new Straight(new Point(0, 0), 0, new Point(0, 2), 100);
        Straight b = new Straight(new Point(0, 0), 0, new Point(3, 0), 150);
        double distance = Geometry3i.distance(a, b);
        double expected = 0;

        assertEquals(distance, expected, PRECISION);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDistanceNonOverlapping() {
        Straight a = new Straight(new Point(0, 0), 0, new Point(0, 2), 50);
        Straight b = new Straight(new Point(0, 0), 100, new Point(3, 0), 150);
        Geometry3i.distance(a, b);
    }

    @Test
    public void testDistanceSinglePointInIntersection() {
        Straight a = new Straight(new Point(0, 0), 0, new Point(0, 2), 100);
        Straight b = new Straight(new Point(0, 0), 100, new Point(3, 0), 150);
        double distance = Geometry3i.distance(a, b);
        double expected = 2;

        assertEquals(distance, expected, PRECISION);
    }
}
