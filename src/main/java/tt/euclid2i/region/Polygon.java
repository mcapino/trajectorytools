package tt.euclid2i.region;

import tt.euclid2d.util.Intersection;
import tt.euclid2i.Point;

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
}
