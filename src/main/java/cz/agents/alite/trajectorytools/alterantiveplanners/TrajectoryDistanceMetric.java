package cz.agents.alite.trajectorytools.alterantiveplanners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jgrapht.Graph;

import cz.agents.alite.trajectorytools.graph.maneuver.DefaultManeuver;
import cz.agents.alite.trajectorytools.graph.spatial.SpatialWaypoint;
import cz.agents.alite.trajectorytools.planner.GoalPenaltyFunction;
import cz.agents.alite.trajectorytools.planner.PathPlanner;
import cz.agents.alite.trajectorytools.planner.PlannedPath;

public class TrajectoryDistanceMetric<V extends SpatialWaypoint, E extends DefaultManeuver> implements AlternativePathPlanner<V, E> {

    private static final int SOLUTION_COUNT = 5;
    private static final int ALPHA = 1;
    private static final int MAX_DIST = 2;
    
    private final PathPlanner<V, E> planner;
    
    public TrajectoryDistanceMetric(PathPlanner<V, E> planner) {
        this.planner = planner;
    }
    
    @Override
    public Collection<PlannedPath<V, E>> planPath(Graph<V, E> graph, V startVertex, V endVertex) {
        
        final List<PlannedPath<V, E>> paths = new ArrayList<PlannedPath<V, E>>(SOLUTION_COUNT);

        planner.setGoalPenaltyFunction(new GoalPenaltyFunction<V>() {
            @Override
            public double getGoalPenalty(V vertex) {
                double penalty = 0;

                for (PlannedPath<V, E> path : paths) {
                    double minDist = Double.MAX_VALUE; 
                    for (E edge : path.getEdgeList()) {
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