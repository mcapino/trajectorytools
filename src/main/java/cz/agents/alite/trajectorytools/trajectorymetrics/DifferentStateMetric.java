package cz.agents.alite.trajectorytools.trajectorymetrics;

import cz.agents.alite.trajectorytools.graph.maneuver.Maneuver;
import cz.agents.alite.trajectorytools.graph.spatialwaypoint.SpatialWaypoint;
import cz.agents.alite.trajectorytools.planner.PlannedPath;

public class DifferentStateMetric implements ManeuverTrajectoryMetric {

    public DifferentStateMetric() {
    }

    @Override
    public double getTrajectoryDistance( PlannedPath<SpatialWaypoint, Maneuver> path, PlannedPath<SpatialWaypoint, Maneuver> otherPath) {
        double penalty = 0;
        if (pathContainsVertex(path.getStartVertex(), otherPath)) {
            penalty += 0.5;
        }

        if (pathContainsVertex(path.getEndVertex(), otherPath)) {
            penalty += 0.5;
        }

        for (Maneuver edge : path.getEdgeList()) {
            if (pathContainsVertex(edge.getSource(), otherPath)) {
                penalty += 0.5;
            }
            if (pathContainsVertex(edge.getTarget(), otherPath)) {
                penalty += 0.5;
            }
        }
            
        return 1 - penalty / (path.getEdgeList().size() + 1);
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
