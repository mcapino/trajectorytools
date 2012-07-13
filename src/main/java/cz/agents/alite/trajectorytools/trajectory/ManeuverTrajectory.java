package cz.agents.alite.trajectorytools.trajectory;

import java.text.DecimalFormat;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.SingleEdgeGraphPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;

import cz.agents.alite.trajectorytools.graph.spatial.SpatialWaypoint;
import cz.agents.alite.trajectorytools.graph.spatial.maneuvers.SpatialManeuver;
import cz.agents.alite.trajectorytools.util.OrientedPoint;
import cz.agents.alite.trajectorytools.util.Point;
import cz.agents.alite.trajectorytools.util.Vector;

/**
 * A wrapper that interprets a graph path as a trajectory.
 * Start time must be be given. Then, the trajectory parameters should 
 * be interpreted as follows:
 *
 *                      duration
 *       | --------------------------------> |
 *  start time                             max time                                             
 *
 */

public class ManeuverTrajectory<V extends SpatialWaypoint, E extends SpatialManeuver> implements Trajectory {

    private List<E> maneuvers = null;

    private SpatialWaypoint startWaypoint;
    private SpatialWaypoint endWaypoint;
    
    Graph<V,E> graph;

    private double startTime;
    private double duration = Double.POSITIVE_INFINITY;
    
    boolean stayAtLastPoint;

    public ManeuverTrajectory(double startTime, GraphPath<V,E> graphPath, boolean stayAtLastPoint) {
        this.startWaypoint = graphPath.getStartVertex();
        this.endWaypoint = graphPath.getEndVertex();
        this.maneuvers = graphPath.getEdgeList();

        this.startTime = startTime;
        this.duration = graphPath.getWeight();
        this.graph = graphPath.getGraph();
        this.stayAtLastPoint = stayAtLastPoint;
    }

    @Override
    public OrientedPoint getPosition(double t) {
        SpatialWaypoint currentWaypoint = startWaypoint;
        double currentWaypointTime = startTime;
        Vector currentDirection = new Vector(1,0,0);

        if (t < startTime) {
            return null;
        }


        if (maneuvers != null)  {
            for (E maneuver: maneuvers) {
                SpatialWaypoint nextWaypoint = graph.getEdgeTarget(maneuver);
                double duration  = maneuver.getDuration(); 
                double nextWaypointTime = currentWaypointTime + duration;


                if ( currentWaypointTime <= t && t <= nextWaypointTime) {
                    // linear approximation
                	OrientedPoint pos = maneuver.getTrajectory(currentWaypointTime).getPosition(t);
                    return pos;
                }
                currentWaypoint = nextWaypoint;
                currentWaypointTime = nextWaypointTime;
            }
        }
        if (t >= currentWaypointTime) {
            if (stayAtLastPoint) {
            	return new OrientedPoint(currentWaypoint, currentDirection);
            } else {
            	return null;
            }
        }

        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ManeuverTrajectory){
        	ManeuverTrajectory other = (ManeuverTrajectory) obj;
            if (startWaypoint.equals(other.startWaypoint) &&
                    endWaypoint.equals(other.endWaypoint) &&
                    maneuvers.equals(other.maneuvers) &&
                    Math.abs(startTime - other.startTime) < 0.0001 ) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public int hashCode() {
        int hashCode = startWaypoint.hashCode();

        for (SpatialManeuver edge: maneuvers) {
            hashCode = hashCode ^ edge.hashCode();
        }

        return hashCode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MT(@");
        DecimalFormat f = new DecimalFormat("#0.00");
        sb.append(f.format(startTime));
       
        if (!maneuvers.isEmpty()) {
        	sb.append(" " + graph.getEdgeSource(maneuvers.get(0)));
        }
        
        for (E maneuver: maneuvers) {
            sb.append(" " + graph.getEdgeTarget(maneuver));
        }
        sb.append(" )");
        return sb.toString();
    }

	@Override
	public double getMinTime() {
		return startTime;
	}

	@Override
	public double getMaxTime() {
		if (stayAtLastPoint) {
			return Double.POSITIVE_INFINITY;
		} else {
			return startTime + duration;
		}
	}
}
