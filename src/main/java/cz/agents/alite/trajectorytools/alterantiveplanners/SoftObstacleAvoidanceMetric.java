package cz.agents.alite.trajectorytools.alterantiveplanners;

import java.util.Collection;

import org.jgrapht.Graph;

import cz.agents.alite.trajectorytools.graph.maneuver.DefaultManeuver;
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
public class SoftObstacleAvoidanceMetric<V, E extends DefaultManeuver> implements AlternativePathPlanner<V, E> {

    public SoftObstacleAvoidanceMetric() {
    }
    
    @Override
    public Collection<PlannedPath<V, E>> planPath(Graph<V, E> graph, V startVertex, V endVertex) {
        throw new UnsupportedOperationException("cannot be planned by AStar type of planner!");
    }
}
