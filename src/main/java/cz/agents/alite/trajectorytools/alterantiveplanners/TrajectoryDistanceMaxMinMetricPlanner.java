package cz.agents.alite.trajectorytools.alterantiveplanners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cz.agents.alite.trajectorytools.graph.spatial.GraphWithObstacles;
import cz.agents.alite.trajectorytools.planner.GoalPenaltyFunction;
import cz.agents.alite.trajectorytools.planner.PathPlanner;
import cz.agents.alite.trajectorytools.planner.PlannedPath;
import cz.agents.alite.trajectorytools.planner.SingleVertexPlannedPath;
import cz.agents.alite.trajectorytools.trajectorymetrics.TrajectoryDistanceMetric;
import cz.agents.alite.trajectorytools.trajectorymetrics.TrajectoryMetric;
import cz.agents.alite.trajectorytools.trajectorymetrics.TrajectorySetMetrics;
import cz.agents.alite.trajectorytools.util.Point;

public class TrajectoryDistanceMaxMinMetricPlanner<V extends Point, E> implements AlternativePathPlanner<V,E> {

    private final PathPlanner<V, E> planner;
    private final int pathSolutionLimit;

    private final TrajectoryMetric<V,E> metric;

    private final double maxDistance;

    public TrajectoryDistanceMaxMinMetricPlanner(PathPlanner<V, E> planner, int pathSolutionLimit, double maxDistance) {
        this.planner = planner;
        this.pathSolutionLimit = pathSolutionLimit;
        this.maxDistance = maxDistance;

        metric = new TrajectoryDistanceMetric<V, E>();
    }

    @Override
    public Collection<PlannedPath<V, E>> planPath(final GraphWithObstacles<V,E> graph, V startVertex, V endVertex) {

        final List<PlannedPath<V, E>> paths = new ArrayList<PlannedPath<V, E>>(pathSolutionLimit);
        for (int i = 0; i < pathSolutionLimit; i++) {
            paths.add( planner.planPath(
                    graph, startVertex, endVertex,
                    new GoalPenaltyFunction<V>() {
                        @Override
                        public double getGoalPenalty(V vertex) {
                            double distance = TrajectorySetMetrics.getRelativePlanSetMinDiversity(
                                    new SingleVertexPlannedPath<V, E>(graph, vertex),
                                    paths,
                                    metric
                                    );
                            if (distance < maxDistance) {
                                return maxDistance - distance ;
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
        return "Trajectory MaxMin Distance Metric Planner";
    }
}