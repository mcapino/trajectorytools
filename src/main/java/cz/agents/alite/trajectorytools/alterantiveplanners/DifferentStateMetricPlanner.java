package cz.agents.alite.trajectorytools.alterantiveplanners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cz.agents.alite.trajectorytools.graph.maneuver.Maneuver;
import cz.agents.alite.trajectorytools.graph.maneuver.ManeuverGraphWithObstacles;
import cz.agents.alite.trajectorytools.graph.spatialwaypoint.SpatialWaypoint;
import cz.agents.alite.trajectorytools.planner.GoalPenaltyFunction;
import cz.agents.alite.trajectorytools.planner.PathPlanner;
import cz.agents.alite.trajectorytools.planner.PlannedPath;
import cz.agents.alite.trajectorytools.planner.SingleVertexPlannedPath;
import cz.agents.alite.trajectorytools.trajectorymetrics.DifferentStateMetric;
import cz.agents.alite.trajectorytools.trajectorymetrics.ManeuverTrajectoryMetric;

public class DifferentStateMetricPlanner implements AlternativePathPlanner {

    private static final int ALPHA = 1;
    
    private final PathPlanner<SpatialWaypoint, Maneuver> planner;

    private final int pathSolutionLimit;

    ManeuverTrajectoryMetric metric;
        
    public DifferentStateMetricPlanner(PathPlanner<SpatialWaypoint, Maneuver> planner, int pathSolutionLimit, int maxDistance) {
        this.planner = planner;
        this.pathSolutionLimit = pathSolutionLimit;

        metric = new DifferentStateMetric(maxDistance);
    }

    @Override
    public Collection<PlannedPath<SpatialWaypoint, Maneuver>> planPath(final ManeuverGraphWithObstacles graph, SpatialWaypoint startVertex, SpatialWaypoint endVertex) {
        final List<PlannedPath<SpatialWaypoint, Maneuver>> paths = new ArrayList<PlannedPath<SpatialWaypoint, Maneuver>>(pathSolutionLimit);

        planner.setGoalPenaltyFunction(new GoalPenaltyFunction<SpatialWaypoint>() {
            @Override
            public double getGoalPenalty(SpatialWaypoint vertex) {
                return ALPHA * metric.getTrajectoryValue(new SingleVertexPlannedPath(graph, vertex), paths);
            }
        });

        for (int i = 0; i < pathSolutionLimit; i++) {
            paths.add( planner.planPath(graph, startVertex, endVertex) );
        }
        
        return paths;
    }

    @Override
    public String getName() {
        return "Different States Metric";
    }
}
