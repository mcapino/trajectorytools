package cz.agents.alite.trajectorytools.alterantiveplanners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jgrapht.Graph;

import cz.agents.alite.trajectorytools.planner.GoalPenaltyFunction;
import cz.agents.alite.trajectorytools.planner.PathPlanner;
import cz.agents.alite.trajectorytools.planner.PlannedPath;

public class DifferentStateMetric<V, E> implements AlternativePathPlanner<V, E> {

    private static final int SOLUTION_COUNT = 5;
    private static final int ALPHA = 1;
    
    private final PathPlanner<V, E> planner;
    
    public DifferentStateMetric(PathPlanner<V, E> planner) {
        this.planner = planner;
    }
    
    @Override
    public Collection<PlannedPath<V, E>> planPath(Graph<V, E> graph, V startVertex, V endVertex) {
        
        final List<PlannedPath<V, E>> paths = new ArrayList<PlannedPath<V, E>>(SOLUTION_COUNT);

        planner.setGoalPenaltyFunction(new GoalPenaltyFunction<V>() {
            @Override
            public double getGoalPenalty(V vertex) {
                int penalty = 0;

                for (PlannedPath<V, E> path : paths) {
                    for (E edge : path.getEdgeList()) {
                        if ( vertex.equals(path.getGraph().getEdgeTarget(edge))) {
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
