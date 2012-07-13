package cz.agents.alite.trajectorytools.trajectorymetrics;

import java.util.Collection;

import cz.agents.alite.trajectorytools.graph.maneuver.Maneuver;
import cz.agents.alite.trajectorytools.graph.spatialwaypoint.SpatialWaypoint;
import cz.agents.alite.trajectorytools.planner.PlannedPath;

public class DifferentStateMetric implements ManeuverTrajectoryMetric {

    private final double maxDistance;
    public DifferentStateMetric(double maxDistance) {
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
                    if ( edge.getTarget().equals(otherEdge.getTarget()) ) {
                        penalty++;
                        break;
                    }                            
                }
                if (minDist < maxDistance) {
                    penalty += maxDistance - minDist; 
                }
            }

            if (path.getStartVertex().equals(other.getStartVertex())) {
                penalty ++; 
            }
        }
        return penalty;
    }

    @Override
    public String getName() {
        return "Different States";
    }
}
