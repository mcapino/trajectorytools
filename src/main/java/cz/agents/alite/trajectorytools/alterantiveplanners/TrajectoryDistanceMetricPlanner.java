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
import cz.agents.alite.trajectorytools.trajectorymetrics.ManeuverTrajectoryMetric;
import cz.agents.alite.trajectorytools.trajectorymetrics.TrajectoryDistanceMetric;

public class TrajectoryDistanceMetricPlanner implements AlternativePathPlanner {

    private static final int ALPHA = 1;
    
    private final PathPlanner<SpatialWaypoint, Maneuver> planner;
    private final int pathSolutionLimit;

    private final ManeuverTrajectoryMetric metric;

    private final double maxDistance;
    
    public TrajectoryDistanceMetricPlanner(PathPlanner<SpatialWaypoint, Maneuver> planner, int pathSolutionLimit, double maxDistance) {
        this.planner = planner;
        this.pathSolutionLimit = pathSolutionLimit;
        this.maxDistance = maxDistance;
        
        metric = new TrajectoryDistanceMetric();
    }
    
    @Override
    public Collection<PlannedPath<SpatialWaypoint, Maneuver>> planPath(final ManeuverGraphWithObstacles graph, SpatialWaypoint startVertex, SpatialWaypoint endVertex) {
        
        final List<PlannedPath<SpatialWaypoint, Maneuver>> paths = new ArrayList<PlannedPath<SpatialWaypoint, Maneuver>>(pathSolutionLimit);
        for (int i = 0; i < pathSolutionLimit; i++) {
            paths.add( planner.planPath(
                    graph, startVertex, endVertex, 
                    new GoalPenaltyFunction<SpatialWaypoint>() {
                        @Override
                        public double getGoalPenalty(SpatialWaypoint vertex) {
                            double distance = metric.getTrajectoryValue(new SingleVertexPlannedPath(graph, vertex), paths);
                            if (distance < maxDistance) {
                                return ALPHA * ( maxDistance - distance );
                            } else {
                                return 0;
                            }
                        }
                    }, 
                    planner.getHeuristicFunction()
                    ) );
        }
        
        return paths;
    }

    @Override
    public String getName() {
        return "Trajectory Distance Metric";
    }
}