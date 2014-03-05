package tt.euclidtime3i;

import tt.euclid2d.Geometry2d;
import tt.euclid2d.Point;
import tt.euclidtime3i.discretization.Straight;

public class Geometry3i {

    private Geometry3i() {
    }

    public static double distance(Straight x, Straight y) {
        int tStart = Math.max(x.getStart().getTime(), y.getStart().getTime());
        int tEnd = Math.min(x.getEnd().getTime(), y.getEnd().getTime());

        if (tStart > tEnd)
            throw new IllegalArgumentException("Given straights do not have an intersection");

        Point a = x.interpolateFloat(tStart);
        Point b = x.interpolateFloat(tEnd);

        Point c = y.interpolateFloat(tStart);
        Point d = y.interpolateFloat(tEnd);

        return distanceOverTimeParameter(a, b, c, d);
    }

    public static double distanceOverTimeParameter(Point aStart, Point aEnd, Point bStart, Point bEnd) {
        Point ac = Geometry2d.sub(aStart, bStart);
        Point bd = Geometry2d.sub(aEnd, bEnd);

        return Geometry2d.distancePointToLie(ac, bd, Point.zero());
    }
}
