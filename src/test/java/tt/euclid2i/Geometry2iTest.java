package tt.euclid2i;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static tt.euclid2i.Geometry2i.*;

/**
 * Created by Vojtech Letal on 2/19/14.
 */
public class Geometry2iTest {

    public static final double PRECISION = 1E-10;

    @Test
    public void testDistance() throws Exception {
        testDistanceOnSimpleLine();
        testDistanceOnZeroLengthLine();
    }

    private void testDistanceOnSimpleLine() {
        Point a = new Point(1, 2);
        Point b = new Point(3, 4);
        Line line = new Line(a, b);

        Point caseA = new Point(1, 1);
        testSingleDistanceInstance(line, caseA, 1);

        Point caseB = new Point(3, 2);
        testSingleDistanceInstance(line, caseB, Math.sqrt(2));

        Point caseC = new Point(1, 2);
        testSingleDistanceInstance(line, caseC, 0);

        Point caseD = new Point(3, 4);
        testSingleDistanceInstance(line, caseD, 0);
    }

    private void testDistanceOnZeroLengthLine() {
        Point a = new Point(1, 1);
        Line line = new Line(a, a);

        Point caseA = new Point(1, 1);
        testSingleDistanceInstance(line, caseA, 0);

        Point caseB = new Point(1, 2);
        testSingleDistanceInstance(line, caseB, 1);
    }

    private void testSingleDistanceInstance(Line l, Point p, double expected) {
        double value = distance(l, p);
        assertEquals(expected, value, PRECISION);
    }

    @Test
    public void testDot() throws Exception {
        Point a = new Point(1, 2);
        Point b = new Point(3, 4);

        double value = dot(a, b);
        double expected = 11;

        assertEquals(expected, value, PRECISION);
    }

    @Test
    public void testSub() throws Exception {
        Point a = new Point(1, 2);
        Point b = new Point(3, 4);

        Point value = sub(a, b);
        Point expected = new Point(-2, -2);

        assertEquals(expected, value);
    }

    @Test
    public void testAdd() throws Exception {
        Point a = new Point(1, 2);
        Point b = new Point(3, 4);

        Point value = add(a, b);
        Point expected = new Point(4, 6);

        assertEquals(expected, value);
    }

}
