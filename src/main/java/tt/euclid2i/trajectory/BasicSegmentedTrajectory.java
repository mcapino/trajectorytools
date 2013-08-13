package tt.euclid2i.trajectory;

import tt.euclid2i.EvaluatedTrajectory;
import tt.euclid2i.Point;
import tt.euclid2i.SegmentedTrajectory;
import tt.euclidtime3i.discretization.Straight;

import java.util.List;

public class BasicSegmentedTrajectory implements SegmentedTrajectory, EvaluatedTrajectory {

    private int startTime;
    private int maxTime;
    private double cost;
    private List<Straight> segments;


    public BasicSegmentedTrajectory(List<Straight> segments, int duration, double cost) {
        if (segments.isEmpty())
            throw new RuntimeException("Trajectory can not be created from empty list");

        this.startTime = segments.get(0).getStart().getTime();
        this.maxTime = startTime + duration;
        this.segments = segments;
        this.cost = cost;
    }

    @Override
    public Point get(int t) {
        if (t < startTime || t > maxTime) {
            return null;
        }

        Straight segment = findSegment(t);

        return segment.interpolate(t).getPosition();
    }

    private Straight findSegment(int t) {
        int iMin = 0;
        int iMax = segments.size() - 1;

        while (iMax >= iMin) {
            int iMid = (iMin + iMax) / 2;

            Straight segment = segments.get(iMid);
            int tStart = segment.getStart().getTime();
            int tEnd = segment.getEnd().getTime();

            if (t < tStart) {
                iMax = iMid - 1;
            } else if (tEnd < t) {
                iMin = iMid + 1;
            } else {
                return segment;
            }
        }

        throw new RuntimeException("Straight in time " + t + " not found");
    }

    @Override
    public List<Straight> getSegments() {
        return segments;
    }

    @Override
    public int getMinTime() {
        return startTime;
    }


    @Override
    public int getMaxTime() {
        return maxTime;
    }

    @Override
    public double getCost() {
        return cost;
    }
}
