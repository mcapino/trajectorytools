package cz.agents.alite.trajectorytools.alterantiveplanners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.jme3.scene.Spatial;

import cz.agents.alite.planner.spatialmaneuver.maneuver.Maneuver;
import cz.agents.alite.trajectorytools.planner.GoalPenaltyFunction;
import cz.agents.alite.trajectorytools.planner.PathPlanner;
import cz.agents.alite.trajectorytools.planner.PlannedPath;
import cz.agents.alite.trajectorytools.planner.SingleVertexPlannedPath;
import cz.agents.alite.trajectorytools.trajectorymetrics.ManeuverTrajectoryMetric;
import cz.agents.alite.trajectorytools.trajectorymetrics.TrajectoryDistanceMetric;
import cz.agents.alite.trajectorytools.util.Waypoint;

public class TrajectoryDistanceMetricPlanner implements AlternativePathPlanner<Waypoint, Spatial> {

    private static final int ALPHA = 1;

    private final PathPlanner<Waypoint, Maneuver> planner;
    private final int pathSolutionLimit;

    ManeuverTrajectoryMetric metric;

    private final double maxDistance;

    public TrajectoryDistanceMetricPlanner(PathPlanner<Waypoint, Maneuver> planner, int pathSolutionLimit, double maxDistance) {
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