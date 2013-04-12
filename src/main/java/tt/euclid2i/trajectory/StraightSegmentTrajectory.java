package tt.euclid2i.trajectory;

import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import tt.euclid2i.EvaluatedTrajectory;
import tt.euclid2i.Point;
import tt.euclidtime3i.discretization.Straight;


/**
 * A wrapper that interprets a graph path in euclidean 2i + time graph as a trajectory.
 */

public class StraightSegmentTrajectory<V extends tt.euclidtime3i.Point, E extends Straight> implements EvaluatedTrajectory {

    private List<E> maneuvers = null;

    private tt.euclidtime3i.Point startWaypoint;

    Graph<V,E> graph;

    private int duration = Integer.MAX_VALUE;
    private double cost;


    public StraightSegmentTrajectory(GraphPath<V,E> graphPath, int duration) {
        this.startWaypoint = graphPath.getStartVertex();
        this.maneuvers = graphPath.getEdgeList();

        this.duration = duration;
        this.graph = graphPath.getGraph();
        this.cost = graphPath.getWeight();
    }

    @Override
    public Point get(int t) {
        tt.euclidtime3i.Point currentWaypoint = startWaypoint;

        if (t < startWaypoint.getTime() && t > startWaypoint.getTime() + duration) {
            return null;
        }


        if (maneuvers != null)  {
            for (E maneuver: maneuvers) {
                tt.euclidtime3i.Point nextWaypoint = graph.getEdgeTarget(maneuver);
                double maneuverDuration  = nextWaypoint.getTime() - currentWaypoint.getTime();

                if (currentWaypoint.getTime() <= t && t <= nextWaypoint.getTime()) {
                    // linear approximation

                    double alpha = (t - currentWaypoint.getTime()) / maneuverDuration;
                    assert(alpha >= -0.00001 && alpha <= 1.00001);

                    tt.euclid2d.Point pos = tt.euclid2d.Point.interpolate(
                            new tt.euclid2d.Point(currentWaypoint.x,
                                    currentWaypoint.y), new tt.euclid2d.Point(
                                    nextWaypoint.x, nextWaypoint.y), alpha);
                    return new Point((int) Math.round(pos.x),
                            (int) Math.round(pos.y));
                }
                currentWaypoint = nextWaypoint;
            }
        }
        if (t >= currentWaypoint.getTime()) {
                   return currentWaypoint.getPosition();
        }

        return null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((maneuvers == null) ? 0 : maneuvers.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StraightSegmentTrajectory other = (StraightSegmentTrajectory) obj;
        if (maneuvers == null) {
            if (other.maneuvers != null)
                return false;
        } else if (!maneuvers.equals(other.maneuvers))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("");

        if (!maneuvers.isEmpty()) {
            sb.append(" " + graph.getEdgeSource(maneuvers.get(0)));
        }

        for (E maneuver: maneuvers) {
            sb.append(" " + graph.getEdgeTarget(maneuver));
        }
        sb.append("");
        return sb.toString();
    }

    @Override
    public int getMinTime() {
        return startWaypoint.getTime();
    }

    @Override
    public int getMaxTime() {
        return startWaypoint.getTime() + duration;
    }

    @Override
    public double getCost() {
        return cost;
    }


}
