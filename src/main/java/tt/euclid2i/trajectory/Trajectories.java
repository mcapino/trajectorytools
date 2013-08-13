package tt.euclid2i.trajectory;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import tt.euclid2i.Point;
import tt.euclidtime3i.discretization.Straight;

import java.util.ArrayList;
import java.util.List;

public class Trajectories {

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

    public static <V extends Point, E> BasicSegmentedTrajectory createConstantSpeedTrajectory(GraphPath<V, E> graphPath, int startTime, int speed, int duration, double cost) {

        List<Straight> segments = new ArrayList<Straight>();

        List<E> edgeList = graphPath.getEdgeList();
        Graph<V, E> graph = graphPath.getGraph();

        V current = graphPath.getStartVertex();
        double oppositeTime, currentTime = startTime;

        for (E edge : edgeList) {
            V opposite = Graphs.getOppositeVertex(graph, edge, current);
            oppositeTime = currentTime + current.distance(opposite) / speed;

            segments.add(new Straight(new tt.euclidtime3i.Point(current, (int) currentTime),
                    new tt.euclidtime3i.Point(opposite, (int) oppositeTime)));

            current = opposite;
            currentTime = oppositeTime;
        }

        return new BasicSegmentedTrajectory(segments, duration, cost);
    }

}
