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
import cz.agents.alite.trajectorytools.trajectorymetrics.TrajectorySetMetrics;

public class DifferentStateMetricPlanner implements AlternativePathPlanner {

    private static final int ALPHA = 5;
    
    private final PathPlanner<SpatialWaypoint, Maneuver> planner;

    private final int pathSolutionLimit;

    private final ManeuverTrajectoryMetric metric;
        
    public DifferentStateMetricPlanner(PathPlanner<SpatialWaypoint, Maneuver> planner, int pathSolutionLimit) {
        this.planner = planner;
        this.pathSolutionLimit = pathSolutionLimit;

        metric = new DifferentStateMetric();
    }

    @Override
    public Collection<PlannedPath<SpatialWaypoint, Maneuver>> planPath(final ManeuverGraphWithObstacles graph, SpatialWaypoint startVertex, SpatialWaypoint endVertex) {
        final List<PlannedPath<SpatialWaypoint, Maneuver>> paths = new ArrayList<PlannedPath<SpatialWaypoint, Maneuver>>(pathSolutionLimit);

        for (int i = 0; i < pathSolutionLimit; i++) {
            PlannedPath<SpatialWaypoint, Maneuver> path = planner.planPath(
                    graph, startVertex, endVertex, 
                    new GoalPenaltyFunction<SpatialWaypoint>() {
                        @Override
                        public double getGoalPenalty(SpatialWaypoint vertex) {
                            double value = TrajectorySetMetrics.getRelativePlanSetAvgDiversity(
                                    new SingleVertexPlannedPath(graph, vertex), 
                                    paths,
                                    metric
                                    );
                            return ALPHA * (1 - value);
                        }
                    }, 
                    planner.getHeuristicFunction()
                    );
            if (path == null) {
                System.out.println("!!!!!! NULL !!!!!!!");
            }
            paths.add( path );
        }
        
        return paths;
    }

    @Override
    public String getName() {
        return "Different States Metric Planner";
    }
}
