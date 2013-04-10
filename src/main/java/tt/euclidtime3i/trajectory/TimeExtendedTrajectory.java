package tt.euclidtime3i.trajectory;

import tt.euclidtime3i.EvaluatedTrajectory;
import tt.euclidtime3i.Point;

/**
 * A wrapper from trajectory in euclidean2i space to euclidean2i+time space.
 */
public class TimeExtendedTrajectory implements EvaluatedTrajectory {

    tt.euclid2i.EvaluatedTrajectory traj;

    public TimeExtendedTrajectory(tt.euclid2i.EvaluatedTrajectory traj) {
        super();
        this.traj = traj;
    }

    @Override
    public double getCost() {
        return traj.getCost();
    }

    @Override
    public int getMinTime() {
        return traj.getMinTime();
    }

    @Override
    public int getMaxTime() {
        return traj.getMaxTime();
    }

    @Override
    public Point get(int t) {
        tt.euclid2i.Point pos = traj.get(t);
        return new Point(pos.x, pos.y, t);
    }
}
