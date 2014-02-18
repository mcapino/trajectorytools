package tt.euclid2d.region;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import tt.euclid2d.Point;
import tt.euclid2d.util.Intersection;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

public class Polygon {

    private Point[] polygonPointsArray;

    public Polygon(Point[] points) {
        super();
        this.polygonPointsArray = points;
    }

    public Polygon(tt.euclid2i.Point[] points) {
        super();
        this.polygonPointsArray = new Point[points.length];

        for (int i = 0; i < points.length; i++) {
            this.polygonPointsArray[i] = new Point(points[i].x, points[i].y);
        }
    }

    public boolean intersectsLine(Point p1, Point p2) {
        for (int i = 0; i < polygonPointsArray.length - 1; i++) {
            if (Intersection.linesIntersect(p1.x, p1.y, p2.x, p2.y, polygonPointsArray[i].x, polygonPointsArray[i].y, polygonPointsArray[i + 1].x, polygonPointsArray[i + 1].y, true)) {
                return true;
            }
        }

        return false;
    }

    public boolean isInside(Point p) {
        int i;
        int j;
        boolean result = false;
        for (i = 0, j = polygonPointsArray.length - 1; i < polygonPointsArray.length; j = i++) {
            if ((polygonPointsArray[i].y > p.y) != (polygonPointsArray[j].y > p.y) &&
                    (p.x < (polygonPointsArray[j].x - polygonPointsArray[i].x) * (p.y - polygonPointsArray[i].y) / (polygonPointsArray[j].y - polygonPointsArray[i].y) + polygonPointsArray[i].x)) {
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
        return polygonPointsArray;
    }

    public List<Polygon> inflate(double inflateBy, int pointsAtCorner) {
        int size = polygonPointsArray.length;

        Coordinate[] coordinates = new Coordinate[size + 1];
        for (int i = 0; i < size; i++) {
            coordinates[i] = new Coordinate(polygonPointsArray[i].getX(), polygonPointsArray[i].getY());
        }
        coordinates[size] = coordinates[0];

        GeometryFactory geometryFactory = new GeometryFactory();
        LinearRing ring = new LinearRing(new CoordinateArraySequence(coordinates), geometryFactory);

        if (!isFilledInside()) {
            // filled outside, deflate
            inflateBy = -inflateBy;
        }

        Geometry jtsPolygon = new com.vividsolutions.jts.geom.Polygon(ring, new LinearRing[]{}, geometryFactory);
        Geometry buffered = jtsPolygon.buffer(inflateBy, pointsAtCorner);

        Coordinate[] bufferedCoordinates = buffered.getCoordinates();
        // if we have a an ouside filled polygon, then we can get more polygons as a result of inflation
        // the array will consist of multiple closed rings that need to be split apart
        List<Polygon> polygons = new LinkedList<Polygon>();

        if (bufferedCoordinates.length > 0) {
            int bufferedSize = bufferedCoordinates.length;
            List<Point> polygonPoints = null;
            for (int i=0; i < bufferedSize; i++) {
                if (polygonPoints == null) {
                    // start a new polygon
                    polygonPoints = new LinkedList<Point>();
                    polygonPoints.add(new Point(bufferedCoordinates[i].x, bufferedCoordinates[i].y));
                } else {
                    // Add points to current polygon
                    Point currentPoint = new Point(bufferedCoordinates[i].x, bufferedCoordinates[i].y);
                    polygonPoints.add(currentPoint);
                    if (currentPoint.equals(polygonPoints.get(0))) {
                        // We found the last (closing) point of the polygon -- create a new polygon from the sequence
                        polygonPointsArray = polygonPoints.toArray(new Point[polygonPoints.size()]);
                        if (!(isFilledInside() == isClockwise(polygonPointsArray))) {
                            ArrayUtils.reverse(polygonPointsArray);
                        }
                        polygons.add(new Polygon(polygonPointsArray));
                        polygonPoints = null;
                    }
                }
            }


            return polygons;
        } else {
            return new LinkedList<Polygon>();
        }



    }

    public boolean isFilledInside() {
        return isClockwise(polygonPointsArray);
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
