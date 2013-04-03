package tt.euclidtime3i.region;

import tt.euclid2i.Trajectory;
import tt.euclidtime3i.Point;


public class MovingCircle implements Region {
    double samplingInterval;
    tt.euclid2i.Trajectory trajectory;
    double radius;

    public MovingCircle(tt.euclid2i.Trajectory trajectory, int radius, int samplingInterval) {
        super();
        this.trajectory = trajectory;
        this.radius = radius;
        this.samplingInterval = samplingInterval;
    }

    @Override
    public boolean intersectsLine(Point p1, Point p2) {
        Point start;
        Point end;

        if (p1.getTime() < p2.getTime()) {
            start = p1;
            end = p2;
        } else {
            start = p2;
            end = p1;
        }

        int tmin = Math.max((int) trajectory.getMinTime(), start.getTime());
        int tmax = Math.min((int) trajectory.getMaxTime(), end.getTime());

        for (int t = tmin; t <= tmax; t += samplingInterval) {
            double alpha = (t - start.getTime())
                    / (end.getTime() - start.getTime());
            assert (alpha >= -0.00001 && alpha <= 1.00001);

            tt.euclid2d.Point pos2d = tt.euclid2d.Point.interpolate(
                    new tt.euclid2d.Point(start.x, start.y),
                    new tt.euclid2d.Point(end.x, end.y), alpha);

            if (trajectory.get((double) t).distance(new tt.euclid2i.Point((int) pos2d.x, (int) pos2d.y)) <= radius) {
                return true;
            }
        }

        return false;

    }

    @Override
    public boolean isInside(Point p) {
        if (p.getTime() >= trajectory.getMinTime() && p.getTime() <= trajectory.getMaxTime()) {
            return (p.getPosition().distance(trajectory.get((double) p.getTime())) <= radius);
        } else {
            return false;
        }

    }

    public Trajectory getTrajectory() {
        return trajectory;
    }

    public double getRadius() {
        return radius;
    }

}
