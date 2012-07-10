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

public class TrajectoryDistanceMetric implements AlternativePathPlanner {

    private static final int SOLUTION_COUNT = 5;
    private static final int ALPHA = 1;
    private static final int MAX_DIST = 2;
    
    private final PathPlanner<SpatialWaypoint, Maneuver> planner;
    
    public TrajectoryDistanceMetric(PathPlanner<SpatialWaypoint, Maneuver> planner) {
        this.planner = planner;
    }
    
    @Override
    public Collection<PlannedPath<SpatialWaypoint, Maneuver>> planPath(ManeuverGraphWithObstacles graph, SpatialWaypoint startVertex, SpatialWaypoint endVertex) {
        
        final List<PlannedPath<SpatialWaypoint, Maneuver>> paths = new ArrayList<PlannedPath<SpatialWaypoint, Maneuver>>(SOLUTION_COUNT);

        planner.setGoalPenaltyFunction(new GoalPenaltyFunction<SpatialWaypoint>() {
            @Override
            public double getGoalPenalty(SpatialWaypoint vertex) {
                double penalty = 0;

                for (PlannedPath<SpatialWaypoint, Maneuver> path : paths) {
                    double minDist = Double.MAX_VALUE; 
                    for (Maneuver edge : path.getEdgeList()) {
                        double distance = vertex.distance(edge.getTarget());
                        if (distance < minDist) {
                            minDist = distance;
                        }
                    }
                    if (minDist < MAX_DIST) {
                        penalty += MAX_DIST - minDist; 
                    }
                }
                return ALPHA * penalty;
            }
        });

        for (int i = 0; i < SOLUTION_COUNT; i++) {
            paths.add( planner.planPath(graph, startVertex, endVertex) );
        }
        
        return paths;
    }
}