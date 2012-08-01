package cz.agents.alite.trajectorytools.trajectorymetrics;

import java.util.Collection;

import cz.agents.alite.trajectorytools.planner.PlannedPath;
import cz.agents.alite.trajectorytools.util.SpatialPoint;

public class TrajectorySetMetrics {

    private TrajectorySetMetrics() {}

    /**
     * See: 'Alexandra Coman: Generating Diverse Plans Using Quantitative and Qualitative Plan Distance Metrics'
     *
     * @param trajectories
     * @param distanceMetric
     * @return
     */
    public static <V extends SpatialPoint, E> double getPlanSetAvgDiversity(Collection<PlannedPath<V, E>> trajectories, TrajectoryMetric<V, E> distanceMetric) {
        if (trajectories.size() == 0) {
            return 0;
        }
        double value = 0;
        for (PlannedPath<V, E> traj1 : trajectories) {
            for (PlannedPath<V, E> traj2 : trajectories) {
                value += distanceMetric.getTrajectoryDistance(traj1, traj2);
            }
        }
        return value / ( trajectories.size() * trajectories.size() - 1 );
    }

    public static <V extends SpatialPoint, E> double getRelativePlanSetAvgDiversity(PlannedPath<V, E> trajectory, Collection<PlannedPath<V, E>> trajectories, TrajectoryMetric<V, E> distanceMetric) {
        double value = 0;
        for (PlannedPath<V, E> otherTraj : trajectories) {
            value += distanceMetric.getTrajectoryDistance(trajectory, otherTraj);
        }
        return value / trajectories.size();
    }

    public static <V extends SpatialPoint, E> double getRelativePlanSetMaxDiversity(PlannedPath<V, E> trajectory, Collection<PlannedPath<V, E>> trajectories, TrajectoryMetric<V, E> distanceMetric) {
        double maxValue = 0;
        for (PlannedPath<V, E> otherTraj : trajectories) {
            maxValue = Math.max( maxValue, distanceMetric.getTrajectoryDistance(trajectory, otherTraj));
        }
        return maxValue;
    }

    public static <V extends SpatialPoint, E> double getRelativePlanSetMinDiversity(PlannedPath<V, E> trajectory, Collection<PlannedPath<V, E>> trajectories, TrajectoryMetric<V, E> distanceMetric) {
        double minValue = Double.MAX_VALUE;
        for (PlannedPath<V, E> otherTraj : trajectories) {
            minValue = Math.min( minValue, distanceMetric.getTrajectoryDistance(trajectory, otherTraj));
        }
        return minValue;
    }
}