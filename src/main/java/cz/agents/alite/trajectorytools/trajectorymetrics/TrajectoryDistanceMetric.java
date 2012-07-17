package cz.agents.alite.trajectorytools.trajectorymetrics;

import cz.agents.alite.trajectorytools.graph.maneuver.Maneuver;
import cz.agents.alite.trajectorytools.graph.spatialwaypoint.SpatialWaypoint;
import cz.agents.alite.trajectorytools.planner.PlannedPath;

/**
 * 
 * This metric is not symmetric!!!
 * 
 * @author honza
 *
 */
public class TrajectoryDistanceMetric implements ManeuverTrajectoryMetric {

    public TrajectoryDistanceMetric() {
    }
    
    @Override
    public double getTrajectoryDistance( PlannedPath<SpatialWaypoint, Maneuver> path, PlannedPath<SpatialWaypoint, Maneuver> otherPath) {

        double distance = 0;
        distance += 0.5 * getVertexToPathDistance(path.getStartVertex(), otherPath);
        distance += 0.5 * getVertexToPathDistance(path.getEndVertex(), otherPath);

        for (Maneuver edge : path.getEdgeList()) {
            distance += 0.5 * getVertexToPathDistance(edge.getSource(), otherPath);
            distance += 0.5 * getVertexToPathDistance(edge.getTarget(), otherPath);
        }
            
        return distance;
    }

    private double getVertexToPathDistance(SpatialWaypoint vertex, PlannedPath<SpatialWaypoint, Maneuver> path) {

        double minDist = Math.min( vertex.distance(path.getStartVertex()), vertex.distance( path.getEndVertex() ));

        for (Maneuver edge : path.getEdgeList()) {
            double distance = Math.min( vertex.distance(edge.getSource()), vertex.distance( edge.getTarget() ));
            
            minDist = Math.min( minDist, distance );
        }
        
        return minDist;
    }

    @Override
    public String getName() {
        return "Trajectory Distance";
    }
}
