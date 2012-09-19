package cz.agents.alite.trajectorytools.trajectory;


/**
 * A trajectory having a certain cost.
 */
interface EvaluatedTrajectory extends Trajectory {
    public double getCost();
}
