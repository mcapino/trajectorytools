package cz.agents.alite.trajectorytools.graph.spatiotemporal.rrtstar;

import java.util.Collection;
import java.util.Random;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import cz.agents.alite.trajectorytools.graph.spatiotemporal.maneuvers.SpatioTemporalManeuver;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.maneuvers.Straight;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.region.Box4dRegion;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.region.Region;
import cz.agents.alite.trajectorytools.planner.rrtstar.Domain;
import cz.agents.alite.trajectorytools.planner.rrtstar.Extension;
import cz.agents.alite.trajectorytools.planner.rrtstar.ExtensionEstimate;
import cz.agents.alite.trajectorytools.util.MathUtil;
import cz.agents.alite.trajectorytools.util.OrientedTimePoint;
import cz.agents.alite.trajectorytools.util.SpatialPoint;
import cz.agents.alite.trajectorytools.util.TimePoint;
import cz.agents.alite.trajectorytools.util.Vector;

public class OrientedStraightLineDomain implements Domain<OrientedTimePoint, SpatioTemporalManeuver> {

    private static final double NONOPTIMAL_SPEED_PENALTY_COEF = 0.1;

    Box4dRegion bounds;
    Collection<Region> obstacles;

    OrientedTimePoint initialPoint;
    SpatialPoint target;
    double targetReachedTolerance;

    double minSpeed;
    double optSpeed;
    double maxSpeed;

    double maxYawStepInRad;
    double minSegmentDistance;
    double maxAbsPitchRad;

    Random random;

    public OrientedStraightLineDomain(Box4dRegion bounds, OrientedTimePoint initialPoint,
            Collection<Region> obstacles, SpatialPoint target, double targetReachedTolerance, double minSpeed,
            double optSpeed, double maxSpeed, double minSegmentDistance, double minTurnRadius, double maxPitchDeg, Random random) {
        super();

        this.bounds = bounds;
        this.initialPoint = initialPoint;
        this.obstacles = obstacles;
        this.target = target;
        this.targetReachedTolerance = targetReachedTolerance;
        this.minSpeed = minSpeed;
        this.optSpeed = optSpeed;
        this.maxSpeed = maxSpeed;
        this.maxAbsPitchRad = maxPitchDeg * Math.PI/180.0;
        this.minSegmentDistance = minSegmentDistance;
        this.maxYawStepInRad = minSegmentDistance / (2*minTurnRadius);
        this.random = random;

        if (!isInFreeSpace(initialPoint)) {
            throw new IllegalArgumentException("Initial point is not in free space");
        }
    }

    @Override
    public OrientedTimePoint sampleState() {
        OrientedTimePoint point;
        do {
            double x = bounds.getCorner1().x + (random.nextDouble() * (bounds.getCorner2().x - bounds.getCorner1().x));
            double y = bounds.getCorner1().y + (random.nextDouble() * (bounds.getCorner2().y - bounds.getCorner1().y));
            double z = bounds.getCorner1().z + (random.nextDouble() * (bounds.getCorner2().z - bounds.getCorner1().z));
            double t = bounds.getCorner1().w + (random.nextDouble() * (bounds.getCorner2().w - bounds.getCorner1().w));
            // angle
            double dx = random.nextDouble()*2 - 1;
            double dy = random.nextDouble()*2 - 1;
            double dz = random.nextDouble()*2 - 1;
            point = new OrientedTimePoint(new TimePoint(x, y, z, t), new Vector(dx, dy, dz) );
        } while (!isInFreeSpace(point));
        return point;
    }

    @Override
    public Extension<OrientedTimePoint, SpatioTemporalManeuver> extendTo(
            OrientedTimePoint from, OrientedTimePoint to) {
        return extendMaintainSpatialPoint(from, to);
   }

   public Extension<OrientedTimePoint, SpatioTemporalManeuver> steer(
            OrientedTimePoint from, OrientedTimePoint to) {

        double requiredDistance = from.getSpatialPoint().distance(to.getSpatialPoint());


        // Compute required speed
        double requiredSpeed = requiredDistance / (to.getTime() - from.getTime());

        // Compute yaw step needed to reach to parameter
        Vector2d toVector = new Vector2d(horizontal(to.getSpatialPoint()));
        toVector.sub(horizontal(from.getSpatialPoint()));
        double requiredYawStepInRad = angle(horizontal(from.orientation), toVector);

        // Compute pitch step needed
        double verticalDistance = to.z - from.z;
        double horizontalDistance = horizontal(from.getSpatialPoint()).distance(horizontal(to.getSpatialPoint()));
        double requiredPitchAngleInRad = Math.atan(verticalDistance / horizontalDistance);


        double distance = minSegmentDistance;
        if (Math.abs(requiredYawStepInRad) <= maxYawStepInRad &&
            Math.abs(requiredPitchAngleInRad) <= maxAbsPitchRad &&
            requiredSpeed >= minSpeed && requiredSpeed <= maxSpeed &&
            requiredDistance >= minSegmentDistance
            ) {
            // exact line is possible
            distance = requiredDistance;
        }

        // Clamp to kinematic limits
        double speed = MathUtil.clamp(requiredSpeed, minSpeed, maxSpeed);
        double yawStepRad = MathUtil.clamp(requiredYawStepInRad, -maxYawStepInRad, maxYawStepInRad);
        double pitchRad = MathUtil.clamp(requiredPitchAngleInRad, -maxAbsPitchRad, maxAbsPitchRad);

        // Compute the target point

        Vector2d horizontalOrientation = horizontal(from.orientation);
        horizontalOrientation = rotateYaw(horizontalOrientation, yawStepRad);
        horizontalOrientation.normalize();

        Vector3d edgeVector = new Vector3d(horizontalOrientation.x, horizontalOrientation.y, Math.tan(pitchRad));
        edgeVector.normalize();
        Vector3d targetOrientation = new Vector3d(edgeVector);

        edgeVector.scale(distance);
        Point3d targetSpatialPoint = from.getSpatialPoint();
        targetSpatialPoint.add(edgeVector);

        double targetTime = from.getTime() + distance / speed;

        OrientedTimePoint target = new OrientedTimePoint(new TimePoint(targetSpatialPoint, targetTime), targetOrientation);
        SpatioTemporalManeuver maneuver = new Straight(from, target);
        double cost = evaluateFuelCost(from.getSpatialPoint(), target.getSpatialPoint(), speed);

        boolean exact;
        if (target.epsilonEquals(to, 0.001)) {
            target = to;
            exact = true;
        } else {
            exact = false;
        }

        assert(satisfiesDynamicLimits(from, target));

        return new Extension<OrientedTimePoint, SpatioTemporalManeuver>(from, target, maneuver, cost, exact);

   }

   public Extension<OrientedTimePoint, SpatioTemporalManeuver> extendMaintainSpatialPoint(
            OrientedTimePoint from, OrientedTimePoint to) {

        Extension<OrientedTimePoint, SpatioTemporalManeuver> extension = steer(from, to);
        if (!intersectsObstacles(extension.source, extension.target)) {
            return extension;
        } else {
            return null;
        }
   }


    @Override
    public ExtensionEstimate estimateExtension(OrientedTimePoint from, OrientedTimePoint to) {

        Extension<OrientedTimePoint, SpatioTemporalManeuver> extension = steer(from, to);
        return new ExtensionEstimate(extension.cost, extension.exact);
    }

    @Override
    public double estimateCostToGo(OrientedTimePoint p) {
        return evaluateFuelCost(p.getSpatialPoint(), target, optSpeed);
    }

    @Override
    public double distance(OrientedTimePoint p1, OrientedTimePoint p2) {
        return p1.distance(p2);
    }

    @Override
    public double nDimensions() {
        return 7;
    }

    @Override
    public boolean isInTargetRegion(OrientedTimePoint p) {
        return target.distance(p.getSpatialPoint()) <= targetReachedTolerance;
    }

    protected boolean isInFreeSpace(TimePoint p) {
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

    // Simple approximation that should force the planner to use optimal cruise speed
    protected double evaluateFuelCost(SpatialPoint start, SpatialPoint end, double speed) {
        return start.distance(end) * ((Math.abs(speed - optSpeed) / optSpeed) * NONOPTIMAL_SPEED_PENALTY_COEF + 1.0);
    }

    protected boolean intersectsObstacles(TimePoint p1, TimePoint p2) {
            // check obstacles
            for (Region obstacle : obstacles) {
                if (obstacle.intersectsLine(p1, p2)) {
                    return true;
                }
            }
            return false;
    }

    protected boolean satisfiesDynamicLimits(TimePoint p1, TimePoint p2) {
        // check speed constraints
        double requiredSpeed = (p1.getSpatialPoint().distance(p2.getSpatialPoint()))
                / (p2.getTime() - p1.getTime());

        // check pitch limit
        double horizontalDistance = (new Point2d(p1.x, p1.y)).distance(new Point2d(p2.x, p2.y));
        double verticalDistance = (p2.z-p1.z);
        double climbAngleDeg = Math.atan(verticalDistance/horizontalDistance);

        if (Math.abs(climbAngleDeg) - maxAbsPitchRad > 0.01) {
            return false;
        }

        if (!(requiredSpeed >= minSpeed - 0.001 || requiredSpeed <= maxSpeed + 0.001)) {
            return false;
        }

        return true;
    }

    private Point2d horizontal(Point3d p) {
        return new Point2d(p.x, p.y);
    }

    private Vector2d horizontal(Vector3d p) {
        return new Vector2d(p.x, p.y);
    }

    /**
     * An angle between two vectors, adapted the implementation from VecMath to return angle in range [-PI and PI]
     */
    public double angle(Vector2d a, Vector2d b)
    {

       double vDot = a.dot(b) / ( a.length()*b.length() );
       if( vDot < -1.0) vDot = -1.0;
       if( vDot >  1.0) vDot =  1.0;

       double angle = Math.atan2( a.x*b.y - a.y*b.x, a.x*b.x + a.y*b.y );

       return(angle);
    }

    public Vector2d rotateYaw(Vector2d vector, double yawInRad)
    {
        double rx = (vector.x * Math.cos(yawInRad)) - (vector.y * Math.sin(yawInRad));
        double ry = (vector.x * Math.sin(yawInRad)) + (vector.y * Math.cos(yawInRad));
        return new Vector2d(rx, ry);
    }



}
