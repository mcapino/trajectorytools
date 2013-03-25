package tt.euclid2i;

import javax.vecmath.Point2d;
import javax.vecmath.Point2i;

public class Point extends Point2i {

    public Point(int x, int y) {
        super(x, y);
    }

    public double distance(Point2i other) {
        return new Point2d(x,y).distance(new Point2d(other.x,other.y));
    }

}
