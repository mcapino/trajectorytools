package tt.euclid2i.region;

import tt.euclid2d.util.Intersection;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.util.NotImplementedException;

public class Polygon implements Region{

    private Point[] points;

    public Polygon(Point[] points) {
        super();
        this.points = points;
    }

    @Override
    public boolean intersectsLine(Point p1, Point p2) {
        for (int i = 0; i < points.length-1; i++) {
            if (Intersection.linesIntersect(p1.x, p1.y, p2.x, p2.y, points[i].x, points[i].y, points[i+1].x, points[i+1].y, true)) {
                return true;
            }
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
          return result;

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

    public Polygon inflate(double inflateBy, int pointsAtCorner) {

        // convert points to 2d
        tt.euclid2d.Point[] points2d = new tt.euclid2d.Point[points.length];
        for (int i = 0; i < points.length; i++) {
            points2d[i] = new tt.euclid2d.Point(points[i].x, points[i].y);
        }

        // inflate polygon2d
        tt.euclid2d.region.Polygon polygon2d = new tt.euclid2d.region.Polygon(points2d);
        tt.euclid2d.region.Polygon inflatedPolygon2d = polygon2d.inflate(inflateBy, pointsAtCorner);

        // convert back to 2i
        Point[] inflatedPoints = new Point[inflatedPolygon2d.getPoints().length];
        for (int i = 0; i < inflatedPolygon2d.getPoints().length; i++) {
            inflatedPoints[i] = new Point((int) inflatedPolygon2d.getPoints()[i].x, (int) inflatedPolygon2d.getPoints()[i].y);
        }

        return new Polygon(inflatedPoints);
    }


}
