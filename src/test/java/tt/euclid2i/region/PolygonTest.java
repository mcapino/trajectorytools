package tt.euclid2i.region;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import tt.euclid2i.Point;

public class PolygonTest {

    @Test
    public void PointInside() {
        Polygon polygon = new Polygon(new Point[] {new Point(0,0), new Point(10,0), new Point(10,10), new Point(0,10)});
        assertTrue(polygon.isFilledInside());
        assertTrue(polygon.isInside(new Point(5,5)));

        polygon = new Polygon(new Point[] {new Point(0,0), new Point(0,10), new Point(10,10),  new Point(10,0) });
        assertTrue(!polygon.isFilledInside());
        assertTrue(!polygon.isInside(new Point(5,5)));
    }

}
