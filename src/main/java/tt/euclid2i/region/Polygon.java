package tt.euclid2i.region;

import java.io.Serializable;
import java.util.Arrays;

import tt.euclid2d.util.Intersection;
import tt.euclid2i.Point;
import tt.euclid2i.Region;

public class Polygon implements Region, Serializable{

    private static final long serialVersionUID = -8113732712690427548L;

    private Point[] points;

    public Polygon(Point[] points) {
        super();
        this.points = points;
    }

    public void addPoint(Point point) {
        int len = points.length;
        points = Arrays.copyOf(points, len + 1);
        points[len] = point;
    }

    @Override
    public boolean intersectsLine(Point p1, Point p2) {
        for (int i = 0; i < points.length-1; i++) {
            if (Intersection.linesIntersect(p1.x, p1.y, p2.x, p2.y, points[i].x, points[i].y, points[i+1].x, points[i+1].y, true)) {
                return true;
            }
        }
        int l = points.length-1;
        if (Intersection.linesIntersect(p1.x, p1.y, p2.x, p2.y, points[l].x, points[l].y, points[0].x, points[0].y, true)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean isInside(Point p) {
          int i;
          int j;
          boolean result = false;
          for (i = 0, j = points.length - 1; i < points.length; j = i++) {
            if ((points[i].y > p.y) != (points[j].y > p.y) &&
                (p.x < (points[j].x - points[i].x) * (p.y - points[i].y) / (points[j].y-points[i].y) + points[i].x)) {
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

    @Override
    public Rectangle getBoundingBox() {
        //TODO check implementation
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Point point : points) {
            int X = point.getX();
            int Y = point.getY();

            if (X < minX) minX = X;
            if (Y < minY) minY = Y;
            if (X > maxX) maxX = X;
            if (Y > maxY) maxY = Y;
        }

        return new Rectangle(new Point(minX,minY),new Point(maxX,maxY));
    }

    public boolean isFilledInside() {
        return isClockwise(points);
    }

    public Polygon inflate(double inflateBy, int pointsAtCorner) {

        tt.euclid2d.region.Polygon polygon2d = new tt.euclid2d.region.Polygon(points);
        tt.euclid2d.region.Polygon inflatedPolygon2d = polygon2d.inflate(inflateBy, pointsAtCorner);

        // convert back to 2i
        Point[] inflatedPoints = new Point[inflatedPolygon2d.getPoints().length];
        for (int i = 0; i < inflatedPolygon2d.getPoints().length; i++) {
            inflatedPoints[i] = new Point((int) inflatedPolygon2d.getPoints()[i].x, (int) inflatedPolygon2d.getPoints()[i].y);
        }

        return new Polygon(inflatedPoints);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Polygon polygon = (Polygon) o;

        if (!Arrays.equals(points, polygon.points)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(points);
    }
}
