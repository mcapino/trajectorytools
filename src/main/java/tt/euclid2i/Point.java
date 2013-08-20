package tt.euclid2i;

import javax.vecmath.Point2d;
import javax.vecmath.Point2i;
import javax.vecmath.Tuple2i;

public class Point extends Point2i {


    public Point() {
    }

    public Point(int[] t) {
        super(t);
    }

    public Point(Tuple2i t1) {
        super(t1);
    }

    public Point(int x, int y) {
        super(x, y);
    }

    public double distance(Point2i other) {
        return new Point2d(x, y).distance(new Point2d(other.x, other.y));
    }

    public double distanceL1(Point2i other) {
        return new Point2d(x, y).distanceL1(new Point2d(other.x, other.y));
    }

    public Point divide(double scalar) {

        int xsc = (int) Math.round(x / scalar);
        int ysc = (int) Math.round(y / scalar);

        return new Point(xsc, ysc);
    }

}
