package tt.euclid2i.discretization;

import javax.vecmath.Point2d;

import tt.euclid2i.Point;

public class Line {
    private static final long serialVersionUID = -2519868162204278196L;

    private Point start;
    private Point end;

    public Line(Point start, Point end) {
        super();
        this.start = start;
        this.end = end;
    }

    public double getDistance() {
        return new Point2d(start.x, start.y).distance(new Point2d(end.x, end.y));
    }

    public Point getStart() {
        return start;
    }

    public Point getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return String.format("(%s : %s)", start, end);
    }


}
