package cz.agents.alite.trajectorytools.graph.spatial.rrtstar;

import java.util.Collection;
import java.util.Random;

import cz.agents.alite.trajectorytools.graph.spatial.maneuvers.SpatialManeuver;
import cz.agents.alite.trajectorytools.graph.spatial.maneuvers.Straight;
import cz.agents.alite.trajectorytools.graph.spatial.region.BoxRegion;
import cz.agents.alite.trajectorytools.graph.spatial.region.Region;
import cz.agents.alite.trajectorytools.planner.rrtstar.Domain;
import cz.agents.alite.trajectorytools.planner.rrtstar.Extension;
import cz.agents.alite.trajectorytools.planner.rrtstar.ExtensionEstimate;
import cz.agents.alite.trajectorytools.util.Point;

public class SpatialStraightLineDomain implements Domain<Point, SpatialManeuver> {

    BoxRegion bounds;
    Collection<Region> obstacles;
    Region target;
    Random random;
    double speed;

    public SpatialStraightLineDomain(BoxRegion bounds, Collection<Region> obstacles, Region target, double speed) {
        super();
        this.bounds = bounds;
        this.obstacles = obstacles;
        this.target = target;
        this.random = new Random(1);
        this.speed = speed;
    }

    @Override
    public Point sampleState() {
        Point point;
        do {
            double x = bounds.getCorner1().x + (random.nextDouble() * (bounds.getCorner2().x - bounds.getCorner1().x));
            double y = bounds.getCorner1().y + (random.nextDouble() * (bounds.getCorner2().y - bounds.getCorner1().y));
            double z = 0;
            point = new Point(x, y, z);
        } while (!isInFreeSpace(point));
        return point;
    }

    @Override
    public Extension<Point, SpatialManeuver>
    extendTo(Point from, Point to) {
        Extension<Point, SpatialManeuver> result = null;
        if (isVisible(from, to)) {
            Straight maneuver = new Straight(from, to, speed);
            result = new Extension<Point, SpatialManeuver>(from, to, maneuver, maneuver.getDuration(), true);
        }
        return result;
    }

    private boolean isVisible(Point p1, Point p2) {

        // check obstacles
        for (Region obstacle : obstacles) {
            if (obstacle.intersectsLine(p1, p2)) {
                return false;
            }
        }
        return true;
    }

    private boolean isInFreeSpace(Point p) {
        if (bounds.isInside(p)) {
            for (Region obstacle : obstacles) {
                if (obstacle.isInside(p)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ExtensionEstimate<Point, SpatialManeuver> estimateExtension(Point p1, Point p2) {
        return new ExtensionEstimate<Point, SpatialManeuver>(p1.distance(p2)/speed, true);
    }

    @Override
    public double distance(Point p1, Point p2) {
        return p1.distance(p2);
    }

    @Override
    public double nDimensions() {
        return 2;
    }

    @Override
    public boolean isInTargetRegion(Point p) {
        return target.isInside(p);
    }

    @Override
    public double estimateCostToGo(Point p1) {
        return 0.0;
    }
}
