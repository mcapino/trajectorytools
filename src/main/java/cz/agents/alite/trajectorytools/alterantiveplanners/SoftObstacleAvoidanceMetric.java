package cz.agents.alite.trajectorytools.alterantiveplanners;

import java.util.Collection;

import cz.agents.alite.trajectorytools.graph.maneuver.Maneuver;
import cz.agents.alite.trajectorytools.graph.maneuver.ManeuverGraphWithObstacles;
import cz.agents.alite.trajectorytools.graph.spatialwaypoint.SpatialWaypoint;
import cz.agents.alite.trajectorytools.planner.PlannedPath;

/**
 * Not working - cannot be planned by AStar type of planner!
 * 
 * Can propose 3 trajectories at maximum...
 * 
 * Price of the edge depends on the previous trajectory
 * 
 * @author honza
 *
 */
public class SoftObstacleAvoidanceMetric implements AlternativePathPlanner {

    public SoftObstacleAvoidanceMetric() {
    }
    
    @Override
    public Collection<PlannedPath<SpatialWaypoint, Maneuver>> planPath(ManeuverGraphWithObstacles graph, SpatialWaypoint startVertex, SpatialWaypoint endVertex) {
        throw new UnsupportedOperationException("cannot be planned by AStar type of planner!");
    }
}