package cz.agents.alite.trajectorytools.trajectorymetrics;

import java.util.Collection;

import cz.agents.alite.trajectorytools.graph.maneuver.Maneuver;
import cz.agents.alite.trajectorytools.graph.spatialwaypoint.SpatialWaypoint;
import cz.agents.alite.trajectorytools.planner.PlannedPath;

public class DifferentStateMetric implements ManeuverTrajectoryMetric {

    public DifferentStateMetric() {
    }

    @Override
    public double getTrajectoryValue(PlannedPath<SpatialWaypoint, Maneuver> path,
            Collection<PlannedPath<SpatialWaypoint, Maneuver>> otherPaths) {
        double value = 0;

        for (PlannedPath<SpatialWaypoint, Maneuver> other : otherPaths) {
            double penalty = 0;
            if (pathContainsVertex(path.getStartVertex(), other)) {
                penalty += 0.5;
            }

            if (pathContainsVertex(path.getEndVertex(), other)) {
                penalty += 0.5;
            }

            for (Maneuver edge : path.getEdgeList()) {
                if (pathContainsVertex(edge.getSource(), other)) {
                    penalty += 0.5;
                }
                if (pathContainsVertex(edge.getTarget(), other)) {
                    penalty += 0.5;
                }
            }
            
            value += 1 - penalty / (path.getEdgeList().size() + 1);
        }
        return value;
    }

    private boolean pathContainsVertex(SpatialWaypoint vertex, PlannedPath<SpatialWaypoint, Maneuver> path) {
        for (Maneuver edge : path.getEdgeList()) {
            if ( vertex.equals(edge.getSource()) || vertex.equals(edge.getTarget()) ) {
                return true;
            }                            
        }
        return false;
    }

    @Override
    public String getName() {
        return "Different States";
    }
}
