package tt.euclid2i.rrtstar;

import java.util.Collection;

import javax.vecmath.Vector2d;

import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.region.Rectangle;
import tt.euclid2i.region.Region;
import tt.euclid2i.util.Util;
import cz.agents.alite.trajectorytools.planner.rrtstar.Extension;

public class LimitedLengthDomain extends
        StraightLineDomain {

    double maxLength;

    public LimitedLengthDomain(Rectangle bounds,
            Collection<Region> obstacles,
            Region target, Point targetPoint, double maxLength,
            double tryGoalRatio) {
        super(bounds, obstacles, target, targetPoint, tryGoalRatio);
        this.maxLength = maxLength;
    }

    @Override
    public Extension<Point, Line>
    extendTo(Point from, Point to) {
        Extension<Point, Line> result = null;

        Vector2d direction = new Vector2d(to.x - from.x, to.y - from.y);
        direction.normalize();
        direction.scale(maxLength);

        Point actualEnd = new Point((int) Math.round(from.x + direction.x), (int) Math.round(from.y + direction.y));

        if (Util.isVisible(from, actualEnd, obstacles)) {
            Line maneuver = new Line(from, actualEnd);
            result = new Extension<Point, Line>(from, actualEnd, maneuver, maneuver.getDistance(), to.equals(actualEnd));
        }
        return result;
    }

}
