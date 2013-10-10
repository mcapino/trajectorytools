package tt.euclidtime3i.trajectory;

import java.util.Arrays;

import tt.euclid2i.trajectory.BasicSegmentedTrajectory;
import tt.euclidtime3i.Point;
import tt.euclidtime3i.discretization.Straight;

public class LinearTrajectory extends BasicSegmentedTrajectory {

    public LinearTrajectory(Point start, Point end, double cost) {
        super(Arrays.asList(new Straight(start, end)), end.getTime() - start.getTime(), cost);
    }

}
