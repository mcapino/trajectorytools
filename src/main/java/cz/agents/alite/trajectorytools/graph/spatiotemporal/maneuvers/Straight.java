package cz.agents.alite.trajectorytools.graph.spatiotemporal.maneuvers;

import cz.agents.alite.trajectorytools.trajectory.Trajectory;
import cz.agents.alite.trajectorytools.util.TimePoint;

public class Straight extends SpatioTemporalManeuver {
    private static final long serialVersionUID = -2519868162204278196L;
    private cz.agents.alite.trajectorytools.graph.spatial.maneuvers.Straight
        spatialStraight;

    TimePoint start;

    public Straight(TimePoint start, TimePoint end) {
        super();
        this.start = start;
        double speed = start.getPoint3d().distance(end.getPoint3d()) / (end.getTime() - start.getTime());
        this.spatialStraight = new cz.agents.alite.trajectorytools.graph.spatial.maneuvers.Straight(start.getPoint3d(), end.getPoint3d(), speed);
    }

    @Override
    public Trajectory getTrajectory() {
        return spatialStraight.getTrajectory(start.getTime());
    }

    @Override
    public double getStartTime() {
        return start.getTime();
    }

    @Override
    public double getDistance() {
        return spatialStraight.getDistance();
    }

    @Override
    public double getDuration() {
        return spatialStraight.getDuration();
    }



}
