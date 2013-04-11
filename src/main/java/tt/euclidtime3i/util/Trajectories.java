package tt.euclidtime3i.util;

import tt.euclidtime3i.EvaluatedTrajectory;
import tt.euclidtime3i.Point;

public class Trajectories {
    public static EvaluatedTrajectory concatenate(final EvaluatedTrajectory traj1, final EvaluatedTrajectory traj2) {

        if (traj1.getMaxTime() != traj2.getMinTime()) {
            throw new RuntimeException("The trajectories are not aligned.");
        }

        EvaluatedTrajectory result = new EvaluatedTrajectory() {

            @Override
            public int getMinTime() {
                return traj1.getMinTime();
            }

            @Override
            public int getMaxTime() {
                return traj2.getMaxTime();
            }

            @Override
            public Point get(int t) {
                if (t >= traj1.getMinTime() && t <= traj1.getMaxTime()) {
                    if (t < traj1.getMaxTime()) {
                        return traj1.get(t);
                    } else {
                        return traj2.get(t);
                    }
                }
                return null;
            }

            @Override
            public double getCost() {
                return traj1.getCost() + traj2.getCost();
            }

        };

        return result;
    }

    public static  EvaluatedTrajectory createSinglePointTrajectory(final Point point, final int time, final double cost) {
        return new EvaluatedTrajectory() {

            @Override
            public int getMinTime() {
                return time;
            }

            @Override
            public int getMaxTime() {
                return time;
            }

            @Override
            public Point get(int t) {
                return point;
            }

            @Override
            public double getCost() {
                return cost;
            }

        };
    }

    public static tt.euclid2i.EvaluatedTrajectory converetToEuclid2iTrajectory(final EvaluatedTrajectory traj) {
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
                return new tt.euclid2i.Point(timePoint.x, timePoint.y);
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
        };
    }
}
