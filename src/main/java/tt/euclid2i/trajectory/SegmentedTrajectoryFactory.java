package tt.euclid2i.trajectory;

import org.jgrapht.GraphPath;
import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclidtime3i.discretization.Straight;

import java.util.ArrayList;
import java.util.List;

public class SegmentedTrajectoryFactory {

    @SuppressWarnings("unchecked")
    public static <V, E extends Straight> BasicSegmentedTrajectory createTrajectory(GraphPath<V, E> graphPath, int duration, double cost) {
        return new BasicSegmentedTrajectory((List<Straight>) graphPath.getEdgeList(), duration, cost);
    }

    public static BasicSegmentedTrajectory createSingleLineTrajectory(Point start, Point end, int startTime, int duration, int speed, double cost) {
        int endTime = startTime + (int) Math.round(start.distance(end) / speed);

        List<Straight> segments = new ArrayList<Straight>();
        segments.add(new Straight(new tt.euclidtime3i.Point(start, startTime), new tt.euclidtime3i.Point(end, endTime)));

        return new BasicSegmentedTrajectory(segments, duration, cost);
    }

    public static BasicSegmentedTrajectory createConstantSpeedTrajectory(List<Line> edgeList, int startTime, int speed, int duration, double cost) {
        List<Straight> segments = new ArrayList<Straight>();
        double oppositeTime, currentTime = startTime;

        for (Line edge : edgeList) {
            Point start = edge.getStart();
            Point end = edge.getEnd();

            oppositeTime = currentTime + start.distance(end) / speed;
            segments.add(new Straight(new tt.euclidtime3i.Point(start, (int) currentTime), new tt.euclidtime3i.Point(end, (int) oppositeTime)));

            currentTime = oppositeTime;
        }

        return new BasicSegmentedTrajectory(segments, duration, cost);
    }

    public static BasicSegmentedTrajectory createEdgeDurationTrajectory(List<Line> edgeList, int startTime, double edgeDuration, int duration) {
        List<Straight> segments = new ArrayList<Straight>();
        double oppositeTime, currentTime = startTime;

        for (Line edge : edgeList) {
            Point start = edge.getStart();
            Point end = edge.getEnd();

            oppositeTime = currentTime + edgeDuration;
            segments.add(new Straight(new tt.euclidtime3i.Point(start, (int) currentTime), new tt.euclidtime3i.Point(end, (int) oppositeTime)));

            currentTime = oppositeTime;
        }

        return new BasicSegmentedTrajectory(segments, duration, currentTime);
    }

    public static BasicSegmentedTrajectory createConstantSpeedTrajectory(GraphPath<Point, Line> graphPath, int startTime, int speed, int duration, double cost) {
        return createConstantSpeedTrajectory(graphPath.getEdgeList(), startTime, speed, duration, cost);
    }

}
