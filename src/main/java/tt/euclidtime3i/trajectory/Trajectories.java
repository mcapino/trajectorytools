package tt.euclidtime3i.trajectory;

import tt.euclidtime3i.EvaluatedTrajectory;
import tt.euclidtime3i.Point;

public class Trajectories {
    public static EvaluatedTrajectory concatenate(final EvaluatedTrajectory traj1, final EvaluatedTrajectory traj2) {

        if (traj1.getMaxTime() != traj2.getMinTime()) {
            throw new RuntimeException("The trajectories are not aligned.");
        }

        return new ConcatenatedTrajectory(traj1, traj2);
    }

    public static  EvaluatedTrajectory createSinglePointTrajectory(final Point point, final int time, final double cost) {
        return new SinglePointTrajectory(point, time, cost);
    }

    public static tt.euclid2i.EvaluatedTrajectory convertToEuclid2iTrajectory(final EvaluatedTrajectory traj) {
        return new tt.euclid2i.EvaluatedTrajectory() {

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
            public tt.euclid2i.Point get(int t) {
                Point timePoint = traj.get(t);
                if (timePoint != null) {
                    return new tt.euclid2i.Point(timePoint.x, timePoint.y);
                } else {
                    return null;
                }
            }

            @Override
            public int hashCode() {
                return traj.hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                return traj.equals(obj);
            }

            @Override
            public String toString() {
                return traj.toString();
            }

        };
    }

    public static EvaluatedTrajectory convertFromEuclid2iTrajectory(final tt.euclid2i.EvaluatedTrajectory traj) {
        return new EvaluatedTrajectory() {

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

            @Override
            public int hashCode() {
                return traj.hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                return traj.equals(obj);
            }

            @Override
            public String toString() {
                return traj.toString();
            }

        };
    }
}
