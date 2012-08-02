package cz.agents.alite.trajectorytools.graph.spatiotemporal.rrtstar;

import java.util.Collection;
import java.util.Random;

import cz.agents.alite.trajectorytools.graph.spatiotemporal.maneuvers.SpatioTemporalManeuver;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.maneuvers.Straight;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.region.Box4dRegion;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.region.Region;
import cz.agents.alite.trajectorytools.planner.rrtstar.Domain;
import cz.agents.alite.trajectorytools.planner.rrtstar.Extension;
import cz.agents.alite.trajectorytools.planner.rrtstar.ExtensionEstimate;
import cz.agents.alite.trajectorytools.util.MathUtil;
import cz.agents.alite.trajectorytools.util.SpatialPoint;
import cz.agents.alite.trajectorytools.util.TimePoint;

public class SpatioTemporalStraightLineDomain implements Domain<TimePoint, SpatioTemporalManeuver> {

    Box4dRegion bounds;
    Collection<Region> obstacles;
    Region target;


    double minSpeed;
    double optSpeed;
    double maxSpeed;

    Random random;




    public SpatioTemporalStraightLineDomain(Box4dRegion bounds,
            Collection<Region> obstacles, Region target, double minSpeed,
            double optSpeed, double maxSpeed, Random random) {
        super();
        this.bounds = bounds;
        this.obstacles = obstacles;
        this.target = target;
        this.minSpeed = minSpeed;
        this.optSpeed = optSpeed;
        this.maxSpeed = maxSpeed;
        this.random = random;
    }

    @Override
    public TimePoint sampleState() {
        TimePoint point;
        do {
            double x = bounds.getCorner1().x + (random.nextDouble() * (bounds.getCorner2().x - bounds.getCorner1().x));
            double y = bounds.getCorner1().y + (random.nextDouble() * (bounds.getCorner2().y - bounds.getCorner1().y));
            double z = bounds.getCorner1().z + (random.nextDouble() * (bounds.getCorner2().z - bounds.getCorner1().z));
            double t = bounds.getCorner1().w + (random.nextDouble() * (bounds.getCorner2().w - bounds.getCorner1().w));
            point = new TimePoint(x, y, z, t);
        } while (!isInFreeSpace(point));
        return point;
    }

    @Override
    public Extension<TimePoint, SpatioTemporalManeuver> extendTo(
            TimePoint from, TimePoint to) {

        double distance = from.getSpatialPoint().distance(to.getSpatialPoint());
        double requiredSpeed = distance / (to.getTime() - from.getTime());
        boolean exact = (requiredSpeed >= minSpeed && requiredSpeed <= maxSpeed);
        double actualSpeed = MathUtil.clamp(requiredSpeed, minSpeed, maxSpeed);
        TimePoint extensionTarget = new TimePoint(to.getSpatialPoint(), from.getTime() + distance / actualSpeed);
        SpatioTemporalManeuver maneuver = new Straight(from, extensionTarget);
        double cost = evaluateFuelCost(from.getSpatialPoint(), extensionTarget.getSpatialPoint(), actualSpeed);

        if (isVisible(from, extensionTarget)) {
            return new Extension<TimePoint, SpatioTemporalManeuver>(from, extensionTarget,
                       maneuver, cost, exact);
        } else {
            return null;
        }
   }

    @Override
    public ExtensionEstimate<TimePoint, SpatioTemporalManeuver> estimateExtension(
            TimePoint from, TimePoint to) {

        double distance = from.getSpatialPoint().distance(to.getSpatialPoint());
        double requiredSpeed = distance / (to.getTime() - from.getTime());
        boolean exact = (requiredSpeed >= minSpeed && requiredSpeed <= maxSpeed);
        double actualSpeed = MathUtil.clamp(requiredSpeed, minSpeed, maxSpeed);
        double cost = evaluateFuelCost(from.getSpatialPoint(), to.getSpatialPoint(), actualSpeed);

        return new ExtensionEstimate<TimePoint, SpatioTemporalManeuver>(cost, exact);
    }

    @Override
    public double estimateCostToGo(TimePoint p) {
        return 0;
    }

    @Override
    public double distance(TimePoint p1, TimePoint p2) {
        return p1.distance(p2);
    }

    @Override
    public double nDimensions() {
        return 4;
    }

    @Override
    public boolean isInTargetRegion(TimePoint p) {
        return target.isInside(p);
    }

    private boolean isInFreeSpace(TimePoint p) {
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

    private boolean isReachable(TimePoint p1, TimePoint p2) {
        double tDiff = p2.getTime() - p1.getTime();
        double distance = p1.getSpatialPoint().distance(p2.getSpatialPoint());
        double speed = distance / tDiff;

        return (speed >= minSpeed &&  speed <= maxSpeed);
    }

    // Simple approximation that should force the planner to use optimal cruise speed
    private double evaluateFuelCost(SpatialPoint start, SpatialPoint end, double speed) {
        return start.distance(end) * ((Math.abs(speed - optSpeed) / optSpeed) + 1.0);
    }

    public boolean isVisible(TimePoint p1, TimePoint p2) {
        // check speed constraints
        double requiredSpeed = (p1.getSpatialPoint().distance(p2.getSpatialPoint()))
                / (p2.getTime() - p1.getTime());

        if (requiredSpeed >= minSpeed - 0.001 || requiredSpeed <= maxSpeed + 0.001) {

            // check obstacles
            for (Region obstacle : obstacles) {
                if (obstacle.intersectsLine(p1, p2)) {
                    return false;
                }
            }
            return true;

        } else {
            return false;
        }
    }
}
