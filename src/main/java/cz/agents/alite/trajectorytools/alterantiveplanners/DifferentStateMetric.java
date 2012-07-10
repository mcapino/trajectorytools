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

public class DifferentStateMetric implements AlternativePathPlanner {

    private static final int SOLUTION_COUNT = 5;
    private static final int ALPHA = 1;
    
    private final PathPlanner<SpatialWaypoint, Maneuver> planner;
    
    public DifferentStateMetric(PathPlanner<SpatialWaypoint, Maneuver> planner) {
        this.planner = planner;
    }

    @Override
    public Collection<PlannedPath<SpatialWaypoint, Maneuver>> planPath(ManeuverGraphWithObstacles graph, SpatialWaypoint startVertex, SpatialWaypoint endVertex) {
        final List<PlannedPath<SpatialWaypoint, Maneuver>> paths = new ArrayList<PlannedPath<SpatialWaypoint, Maneuver>>(SOLUTION_COUNT);

        planner.setGoalPenaltyFunction(new GoalPenaltyFunction<SpatialWaypoint>() {
            @Override
            public double getGoalPenalty(SpatialWaypoint vertex) {
                int penalty = 0;

                for (PlannedPath<SpatialWaypoint, Maneuver> path : paths) {
                    for (Maneuver edge : path.getEdgeList()) {
                        if ( vertex.equals(edge.getTarget()) ) {
                            penalty++;
                            break;
                        }                            
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
