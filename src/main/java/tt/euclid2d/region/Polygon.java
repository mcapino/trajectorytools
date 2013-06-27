package tt.euclid2d.region;

import javax.vecmath.Vector2d;

import tt.euclid2d.Point;
import tt.euclid2d.util.Intersection;

public class Polygon {

    private Point[] points;

    public Polygon(Point[] points) {
        super();
        this.points = points;
    }

    public boolean intersectsLine(Point p1, Point p2) {
        for (int i = 0; i < points.length-1; i++) {
            if (Intersection.linesIntersect(p1.x, p1.y, p2.x, p2.y, points[i].x, points[i].y, points[i+1].x, points[i+1].y, true)) {
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
                (p.x < (points[j].x - points[i].x) * (p.y - points[i].y) / (points[j].y-points[i].y) + points[i].x)) {
              result = !result;
             }
          }
          return result;

    }

    public Point[] getPoints() {
        return points;
    }

    public Polygon inflate(double inflateBy, int pointsAtCorner) {
        Point[] inflatedPolygon = new Point[points.length*3];

        // find the geometric center
        float xSum = 0;
        float ySum = 0;

        for (int j = 0; j < points.length; j++) {
            xSum += points[j].x;
            ySum += points[j].y;
        }

        Point geomCenter = new Point(xSum/points.length, ySum/points.length);

        for (int j = 0; j < points.length; j++) {
            Vector2d v = new Vector2d(points[j].x, points[j].y);
            v.sub(geomCenter);
            v.normalize();
            v.scale(inflateBy);

            if (pointsAtCorner == 2) {
                //+ rotation with rotation matrix
                double alpha = Math.PI/4;
                v = new Vector2d(v.x*Math.cos(alpha) - v.y*Math.sin(alpha), v.x*Math.sin(alpha) + v.y*Math.cos(alpha));
                inflatedPolygon[2*j + 1] = new Point(points[j].x + v.x, points[j].y + v.y);

                //- rotation with rotation matrix
                double beta = -2*alpha;
                v = new Vector2d(v.x*Math.cos(beta) - v.y*Math.sin(beta), v.x*Math.sin(beta) + v.y*Math.cos(beta));
                inflatedPolygon[2*j] = new Point(points[j].x + v.x, points[j].y + v.y);

            } else if (pointsAtCorner == 3){
                inflatedPolygon[3*j + 1] = new Point(points[j].x + v.x, points[j].y + v.y);

                //+ rotation with rotation matrix
                double alpha = Math.PI/4;
                v = new Vector2d(v.x*Math.cos(alpha) - v.y*Math.sin(alpha), v.x*Math.sin(alpha) + v.y*Math.cos(alpha));
                inflatedPolygon[3*j + 2] = new Point(points[j].x + v.x, points[j].y + v.y);

                //- rotation with rotation matrix
                double beta = -2*alpha;
                v = new Vector2d(v.x*Math.cos(beta) - v.y*Math.sin(beta), v.x*Math.sin(beta) + v.y*Math.cos(beta));
                inflatedPolygon[3*j] = new Point(points[j].x + v.x, points[j].y + v.y);
            } else {
                throw new RuntimeException("This method only supports inflation using 2 or 3 points at the corner");
            }
        }

        return new Polygon(inflatedPolygon);
    }
}
