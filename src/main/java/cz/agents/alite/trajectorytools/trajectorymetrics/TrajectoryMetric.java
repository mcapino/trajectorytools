package cz.agents.alite.trajectorytools.trajectorymetrics;

import java.util.Collection;

import cz.agents.alite.trajectorytools.planner.PlannedPath;

public interface TrajectoryMetric<V, E> {

    double getTrajectoryValue(PlannedPath<V, E> path, Collection<PlannedPath<V, E>> otherPaths);
    
    String getName();
}
