package tt.euclid2i.trajectory;

import java.text.DecimalFormat;

import tt.euclid2i.EvaluatedTrajectory;
import tt.euclid2i.Point;

public class LinearTrajectory implements EvaluatedTrajectory {

    private Point startWaypoint;
    private Point endWaypoint;
    private int startTime;
    private int duration = Integer.MAX_VALUE;
    private double cost;
    private int speed;

    public LinearTrajectory(int startTime, Point startWaypoint, Point endWaypoint, int speed, int duration, double cost) {
        this.startWaypoint = startWaypoint;
        this.endWaypoint = endWaypoint;

        if (startWaypoint.equals(endWaypoint)) {
            throw new IllegalArgumentException("start and end waypoint must be different");
        }

        this.startTime = startTime;
        this.duration = duration;
        this.speed = speed;
        this.cost = cost;
    }

    @Override
    public Point get(int t) {
        if (t < startTime && t > startTime + duration) {
            return null;
        }

        int endWaypointTime = startTime + (int) Math.round(startWaypoint.distance(endWaypoint))/speed;

        if ( startTime <= t && t <= endWaypointTime) {
            // linear approximation

            double alpha = (double) (t - startTime) / (double) (endWaypointTime - startTime);
            assert(alpha >= -0.00001 && alpha <= 1.00001);

            tt.euclid2d.Point pos
                = tt.euclid2d.Point.interpolate(
                        new tt.euclid2d.Point(startWaypoint.x, startWaypoint.y),
                        new tt.euclid2d.Point(endWaypoint.x, endWaypoint.y), alpha);

            return new Point((int) Math.round(pos.x), (int) Math.round(pos.y));
        }

        if (t >= endWaypointTime) {
            return endWaypoint;
        }

        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Linear(");
        DecimalFormat f = new DecimalFormat("#0.00");
        sb.append(f.format(startTime));
        sb.append(startWaypoint.toString());
        sb.append(endWaypoint.toString());
        sb.append(" )");
        return sb.toString();
    }

    @Override
    public int getMinTime() {
        return startTime;
    }

    @Override
    public int getMaxTime() {
        return startTime + duration;
    }

    @Override
    public double getCost() {
        return cost;
    }


}
