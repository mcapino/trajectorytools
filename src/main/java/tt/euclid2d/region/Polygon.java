package tt.euclid2d.region;

import org.apache.commons.lang3.ArrayUtils;

import tt.euclid2d.Point;
import tt.euclid2d.util.Intersection;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

public class Polygon {

    private Point[] points;

    public Polygon(Point[] points) {
        super();
        this.points = points;
    }

    public Polygon(tt.euclid2i.Point[] points) {
        super();
        this.points = new Point[points.length];

        for (int i = 0; i < points.length; i++) {
            this.points[i] = new Point(points[i].x, points[i].y);
        }
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

        if (isFilledInside()) {
            return result;
        } else {
            return !result;
        }

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

        if (isFilledInside()) {
            // filled inside -- inflate
            inflateBy = inflateBy;
        } else {
            // filled outside -- deflate
            inflateBy = -inflateBy;
        }

        Geometry jtsPolygon = new com.vividsolutions.jts.geom.Polygon(ring, new LinearRing[]{}, geometryFactory);
        Geometry buffered = jtsPolygon.buffer(inflateBy, pointsAtCorner);

        Coordinate[] bufferedCoordinates = buffered.getCoordinates();

        if (bufferedCoordinates.length > 0) {
            int bufferedSize = bufferedCoordinates.length - 1;
            Point[] bufferedPoints = new Point[bufferedSize];

            for (int i = 0; i < bufferedSize; i++) {
                bufferedPoints[i] = new Point(bufferedCoordinates[i].x, bufferedCoordinates[i].y);
            }

            if (!(isFilledInside() == isClockwise(bufferedPoints))) {
                ArrayUtils.reverse(bufferedPoints);
            }

            return new Polygon(bufferedPoints);

        } else {
            return new Polygon(new Point[0]);
        }



    }

    public boolean isFilledInside() {
        return isClockwise(points);
    }

    /**
     * Determines if the ring of points is defined in a clockwise direction
     * @param points the array of points constituting the border of the polygon
     * @return true if the ring is defined clockwise
     */
    public static boolean isClockwise(Point[] points){

        double sumOverEdges = 0;

        for (int i = 0; i < points.length; i++) {
            if(i < points.length - 1){
                sumOverEdges += ((points[i + 1].x - points[i].x) * (points[i + 1].y + points[i].y));
            }
            else{
                sumOverEdges += ((points[0].x - points[i].x) * (points[0].y + points[i].y));
            }
        }

        return (sumOverEdges < 0);
    }
}
