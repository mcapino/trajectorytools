package cz.agents.alite.trajectorytools.trajectorymetrics;

import java.util.Collection;

import cz.agents.alite.trajectorytools.graph.maneuver.Maneuver;
import cz.agents.alite.trajectorytools.graph.spatialwaypoint.SpatialWaypoint;
import cz.agents.alite.trajectorytools.planner.PlannedPath;

public class TrajectoryDistanceMetric implements ManeuverTrajectoryMetric {

    private final double maxDistance;
    public TrajectoryDistanceMetric(double maxDistance) {
        this.maxDistance = maxDistance;
    }

    @Override
    public double getTrajectoryValue(PlannedPath<SpatialWaypoint, Maneuver> path,
            Collection<PlannedPath<SpatialWaypoint, Maneuver>> otherPaths) {
        double penalty = 0;

        for (PlannedPath<SpatialWaypoint, Maneuver> other : otherPaths) {
            for (Maneuver edge : path.getEdgeList()) {
                double minDist = Double.MAX_VALUE; 
                for (Maneuver otherEdge : other.getEdgeList()) {
                    double distance = edge.getTarget().distance(otherEdge.getTarget());
                    if (distance < minDist) {
                        minDist = distance;
                    }
                }
                if (minDist < maxDistance) {
                    penalty += maxDistance - minDist; 
                }
            }

            double distance = path.getStartVertex().distance(other.getStartVertex());

            if (distance < maxDistance) {
                penalty += maxDistance - distance; 
            }
        }
        return penalty;
    }

    @Override
    public String getName() {
        return "Trajectory Distance";
    }
}
