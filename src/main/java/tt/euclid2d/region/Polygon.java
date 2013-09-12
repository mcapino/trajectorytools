package tt.euclid2d.region;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import tt.euclid2d.Point;
import tt.euclid2d.util.Intersection;

public class Polygon {

    private Point[] points;

    public Polygon(Point[] points) {
        super();
        this.points = points;
    }

    public boolean intersectsLine(Point p1, Point p2) {
        for (int i = 0; i < points.length - 1; i++) {
            if (Intersection.linesIntersect(p1.x, p1.y, p2.x, p2.y, points[i].x, points[i].y, points[i + 1].x, points[i + 1].y, true)) {
                return true;
            }
        }

        return false;
    }

    public boolean isInside(Point p) {
        int i;
        int j;
        boolean result = false;
        for (i = 0, j = points.length - 1; i < points.length; j = i++) {
            if ((points[i].y > p.y) != (points[j].y > p.y) &&
                    (p.x < (points[j].x - points[i].x) * (p.y - points[i].y) / (points[j].y - points[i].y) + points[i].x)) {
                result = !result;
            }
        }
        return result;

    }

    public Point[] getPoints() {
        return points;
    }

    public Polygon inflate(double inflateBy, int pointsAtCorner) {
        int size = points.length;

        Coordinate[] coordinates = new Coordinate[size + 1];
        for (int i = 0; i < size; i++) {
            coordinates[i] = new Coordinate(points[i].getX(), points[i].getY());
        }
        coordinates[size] = coordinates[0];

        GeometryFactory geometryFactory = new GeometryFactory();
        LinearRing ring = new LinearRing(new CoordinateArraySequence(coordinates), geometryFactory);

        Geometry jtsPolygon = new com.vividsolutions.jts.geom.Polygon(ring, new LinearRing[]{}, geometryFactory);
        Geometry buffered = jtsPolygon.buffer(inflateBy, pointsAtCorner);


        Coordinate[] bufferedCoordinates = buffered.getCoordinates();
        int bufferedSize = bufferedCoordinates.length - 1;
        Point[] bufferedPoints = new Point[bufferedSize];

        for (int i = 0; i < bufferedSize; i++) {
            bufferedPoints[i] = new Point(bufferedCoordinates[i].x, bufferedCoordinates[i].y);
        }

        return new Polygon(bufferedPoints);
    }
}
