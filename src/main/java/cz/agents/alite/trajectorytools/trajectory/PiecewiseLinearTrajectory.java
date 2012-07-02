package cz.agents.alite.trajectorytools.trajectory;

import java.text.DecimalFormat;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.SingleEdgeGraphPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;

import cz.agents.alite.trajectorytools.graph.maneuver.DefaultManeuver;
import cz.agents.alite.trajectorytools.graph.spatial.SpatialWaypoint;
import cz.agents.alite.trajectorytools.util.OrientedPoint;
import cz.agents.alite.trajectorytools.util.Point;
import cz.agents.alite.trajectorytools.util.Vector;

/**
 * Makes a trajectory from a graph path.
 * Start time can be given. Thus, the trajectory parameters should be interpreted as follows:
 *
 *                      duration
 *       | --------------------------------> |
 *  start time                             max time                                                 maxTime
 *
 */

public class ManeuverPathTrajectory<V extends SpatialWaypoint, E> implements Trajectory {

    private List<E> pathEdges = null;

    private SpatialWaypoint startWaypoint;
    private SpatialWaypoint endWaypoint;
    Graph<V,E> graph;

    private double startTime;
    private double duration = Double.POSITIVE_INFINITY;

    public PiecewiseLinearTrajectory(double startTime, GraphPath<V,E> graphPath) {
        this.startWaypoint = graphPath.getStartVertex();
        this.endWaypoint = graphPath.getEndVertex();
        this.pathEdges = graphPath.getEdgeList();

        this.startTime = startTime;
        this.duration = graphPath.getWeight();
        this.graph = graphPath.getGraph();
    }

    @Override
    public OrientedPoint getPosition(double t) {
        SpatialWaypoint currentWaypoint = startWaypoint;
        double currentWaypointTime = startTime;
        Vector currentDirection = new Vector(1,0,0);

        if (t < startTime) {
            return null;
        }


        if (pathEdges != null)  {
            for (E edge: pathEdges) {
                SpatialWaypoint nextWaypoint = graph.getEdgeTarget(edge);
                double duration  = graph.getEdgeWeight(edge); 
                double nextWaypointTime = currentWaypointTime + duration;


                if ( currentWaypointTime <= t && t <= nextWaypointTime) {
                    // linear approximation
                    Point pos = Point.interpolate(currentWaypoint, nextWaypoint, (t-currentWaypointTime) / duration);

                    if (!currentWaypoint.equals(nextWaypoint)) {
                        currentDirection = Vector.subtract(nextWaypoint, currentWaypoint);
                        currentDirection.normalize();
                    } else {
                        currentDirection = new Vector(1,0,0); // wait move
                    }

                    return new OrientedPoint(pos, currentDirection);
                }
                currentWaypoint = nextWaypoint;
                currentWaypointTime = nextWaypointTime;
            }
        }
        if (t >= currentWaypointTime) {
            return new OrientedPoint(currentWaypoint, currentDirection);
        }

        return null;
    }

    @Override
    public double getStartTime() {
        return startTime;
    }

    @Override
    public double getDuration() {
        return duration;
    }
    


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PiecewiseLinearTrajectory){
            PiecewiseLinearTrajectory other = (PiecewiseLinearTrajectory) obj;
            if (startWaypoint.equals(other.startWaypoint) &&
                    endWaypoint.equals(other.endWaypoint) &&
                    pathEdges.equals(other.pathEdges) &&
                    Math.abs(startTime - other.startTime) < 0.01 ) {
                return true;
            }
        }

        if (obj instanceof Trajectory){
            Trajectory other = (Trajectory) obj;
            if (approximation.equals(other)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hashCode = startWaypoint.hashCode();

        for (DefaultWeightedEdge edge: pathEdges) {
            hashCode = hashCode ^ edge.hashCode();
        }

        return hashCode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Waypoint currentWaypoint = startWaypoint;
        sb.append("PLT(@");


        DecimalFormat f = new DecimalFormat("#0.00");
        sb.append(f.format(startTime));
        for (DefaultManeuver maneuver: pathEdges) {
            sb.append(" " + currentWaypoint + " ");
            Waypoint nextWaypoint = maneuver.getOtherWaypoint(currentWaypoint);
            currentWaypoint = nextWaypoint;
        }
        sb.append(currentWaypoint);
        sb.append(" )");
        return sb.toString();
    }
}
