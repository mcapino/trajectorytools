package cz.agents.alite.trajectorytools.alterantiveplanners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jgrapht.Graph;

import cz.agents.alite.trajectorytools.planner.GoalPenaltyFunction;
import cz.agents.alite.trajectorytools.planner.PathPlanner;
import cz.agents.alite.trajectorytools.planner.PlannedPath;
import cz.agents.alite.trajectorytools.util.Waypoint;

public class TrajectoryDistanceMetric<V extends Waypoint, E> implements AlternativePathPlanner<V, E> {

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
                        double distance = vertex.distance(path.getGraph().getEdgeTarget(edge));
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

    ManeuverTrajectoryMetric metric;

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

        planner.setGoalPenaltyFunction(new GoalPenaltyFunction<SpatialWaypoint>() {
            @Override
            public double getGoalPenalty(final SpatialWaypoint vertex) {               
                double distance = metric.getTrajectoryValue(new SingleVertexPlannedPath(graph, vertex), paths);
                if (distance < maxDistance) {
                    return ALPHA * ( maxDistance - distance );
                } else {
                    return 0;
                }
            }
        });

        for (int i = 0; i < pathSolutionLimit; i++) {
            paths.add( planner.planPath(graph, startVertex, endVertex) );
        }
        
        return paths;
    }

    @Override
    public String getName() {
        return "Trajectory Distance Metric";
    }
}