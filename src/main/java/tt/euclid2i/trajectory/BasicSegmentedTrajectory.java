package tt.euclid2i.trajectory;

import tt.euclid2i.EvaluatedTrajectory;
import tt.euclid2i.Point;
import tt.euclid2i.SegmentedTrajectory;
import tt.euclidtime3i.discretization.Straight;

import java.util.List;
import java.util.RandomAccess;

public class BasicSegmentedTrajectory implements SegmentedTrajectory, EvaluatedTrajectory {

    private int startTime;
    private int endTime;
    private int maxTime;
    private double cost;
    private Point endWayPoint;
    private List<Straight> segments;

    public BasicSegmentedTrajectory(List<Straight> segments, int duration, double cost) {
        if (segments.isEmpty())
            throw new RuntimeException("Trajectory can not be created from empty list");

        if (!(segments instanceof RandomAccess))
            throw new RuntimeException("List of segments must be instance of RandomAccess");

        this.startTime = segments.get(0).getStart().getTime();
        this.maxTime = startTime + duration;
        this.segments = segments;
        this.cost = cost;

        tt.euclidtime3i.Point endTimePoint = segments.get(segments.size() - 1).getEnd();
        this.endTime = endTimePoint.getTime();
        this.endWayPoint = endTimePoint.getPosition();
    }

    @Override
    public Point get(int t) {
        if (t < startTime || t > maxTime) {
            return null;
        } else if (t > endTime) {
            return endWayPoint;
        }

        Straight segment = findSegment(t);

        return segment.interpolate(t).getPosition();
    }

    //TODO: using interpolation would decrease complexity from O(log N) to O(log log N)
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((segments == null) ? 0 : segments.hashCode());
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
        BasicSegmentedTrajectory other = (BasicSegmentedTrajectory) obj;
        if (segments == null) {
            if (other.segments != null)
                return false;
        } else if (!segments.equals(other.segments))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("");

        if (!segments.isEmpty()) {
            sb.append(" " + segments.get(0).getStart());
        }

        for (Straight maneuver : segments) {
            sb.append(" " + maneuver.getEnd());
        }
        sb.append("");
        return sb.toString();
    }


}
