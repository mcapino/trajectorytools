package tt.euclid2i.trajectory;

import java.util.Arrays;

import tt.euclid2i.Point;
import tt.euclidtime3i.discretization.Straight;

public class LinearTrajectory extends BasicSegmentedTrajectory {

    public LinearTrajectory(int startTime, Point startWaypoint, Point endWaypoint, int speed, int duration, double cost) {

        super(Arrays.asList(new Straight(new tt.euclidtime3i.Point(startWaypoint, startTime),
                new tt.euclidtime3i.Point(endWaypoint, startTime + (int) startWaypoint.distance(endWaypoint)/speed))),
                duration,
                cost);
    }
}
