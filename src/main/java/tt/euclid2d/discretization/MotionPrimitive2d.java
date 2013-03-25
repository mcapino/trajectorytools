package tt.euclid2d.discretization;

import cz.agents.alite.trajectorytools.trajectory.Trajectory;

public abstract class MotionPrimitive2d {
    private static final long serialVersionUID = -7391923415142790579L;

    /**
     * @param startTime time at which the maneuver starts
     * @return the trajectory of the maneuver
     */
    abstract public Trajectory getTrajectory(double startTime);

    /**
     * @return the length of the maneuver in meters
     */
    abstract public double getDistance();

    /**
     * @return the duration of the maneuver in seconds
     */
    abstract public double getDuration();
}
