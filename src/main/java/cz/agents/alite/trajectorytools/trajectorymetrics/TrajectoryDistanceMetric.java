package cz.agents.alite.trajectorytools.trajectorymetrics;

import java.util.Collection;

import cz.agents.alite.trajectorytools.graph.maneuver.Maneuver;
import cz.agents.alite.trajectorytools.graph.spatialwaypoint.SpatialWaypoint;
import cz.agents.alite.trajectorytools.planner.PlannedPath;

public class TrajectoryDistanceMetric implements ManeuverTrajectoryMetric {

    public TrajectoryDistanceMetric() {
    }
    
    @Override
    public double getTrajectoryValue(PlannedPath<SpatialWaypoint, Maneuver> path,
            Collection<PlannedPath<SpatialWaypoint, Maneuver>> otherPaths) {

        double minDistance = Double.MAX_VALUE;
        
        for (PlannedPath<SpatialWaypoint, Maneuver> other : otherPaths) {
            double distance = 0;
            distance += 0.5 * getVertexToPathDistance(path.getStartVertex(), other);
            distance += 0.5 * getVertexToPathDistance(path.getEndVertex(), other);

            for (Maneuver edge : path.getEdgeList()) {
                distance += 0.5 * getVertexToPathDistance(edge.getSource(), other);
                distance += 0.5 * getVertexToPathDistance(edge.getTarget(), other);
            }
            
            minDistance = Math.min( minDistance, distance );
        }
        return minDistance;
    }

    private double getVertexToPathDistance(SpatialWaypoint vertex, PlannedPath<SpatialWaypoint, Maneuver> path) {
        double minDist = Double.MAX_VALUE; 
        for (Maneuver edge : path.getEdgeList()) {
            double distance = Math.min( vertex.distance(edge.getSource()), vertex.distance( edge.getTarget() ));
            if (distance < minDist) {
                minDist = distance;
            }
        }
        
        return minDist;
    }

    @Override
    public String getName() {
        return "Trajectory Distance";
    }
}
