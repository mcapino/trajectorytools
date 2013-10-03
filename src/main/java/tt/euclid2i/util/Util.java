package tt.euclid2i.util;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.Trajectory;
import tt.euclid2i.region.Polygon;
import tt.euclid2i.region.Rectangle;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

public class Util {

    public static boolean isVisible(Point start, Point end, Collection<? extends Region> obstacles) {
        // check obstacles
        for (Region obstacle : obstacles) {
            if (obstacle.isInside(start) || obstacle.isInside(end) || obstacle.intersectsLine(start, end)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isInFreeSpace(Point point, Collection<? extends Region> obstacles) {
        for (Region obstacle : obstacles) {
            if (obstacle.isInside(point)) {
                return false;
            }
        }

        return true;
    }

    public static Point sampleFreeSpace(Rectangle bounds, Collection<? extends Region> obstacles, Random random) {

        Point point;
        do {
            point = sampleSpace(bounds, random);
        } while (!isInFreeSpace(point, obstacles));

        return point;
    }

    public static Point sampleFreeSpace(Rectangle bounds, Collection<? extends Region> obstacles, Random random, int maxTrials) {

        Point point = null;
        int trials = 0;
        do {
            point = sampleSpace(bounds, random);
            trials++;
        } while (!isInFreeSpace(point, obstacles) && trials < maxTrials);

        return point;
    }

    public static Point sampleSpace(Rectangle bounds, Random random) {
        int x = bounds.getCorner1().x + random.nextInt(bounds.getCorner2().x - bounds.getCorner1().x);
        int y = bounds.getCorner1().y + random.nextInt(bounds.getCorner2().y - bounds.getCorner1().y);
        return new Point(x, y);
    }

    public static Point sampleObstacle(Region region, Random random, int maxTrials) {
        Rectangle bounds = region.getBoundingBox();

        Point point = null;
        do {
            point = sampleSpace(bounds, random);
        } while (!region.isInside(point) && maxTrials-- > 0);

        if (point == null)
            throw new RuntimeException(String.format("Could not sample obstacle in %d trials", maxTrials));

        return point;
    }

    public static DirectedGraph<Point, Line> getVisibilityGraph(Point start, Point goal, Collection<? extends Rectangle> polygons) {
        @SuppressWarnings("serial")
        DirectedGraph<Point, Line> graph = new DefaultDirectedWeightedGraph<Point, Line>(new EdgeFactory<Point, Line>() {

            @Override
            public Line createEdge(Point start,
                                   Point end) {
                return new Line(start, end);
            }
        }) {

            @Override
            public double getEdgeWeight(Line e) {
                return e.getDistance();
            }

        };

        graph.addVertex(start);
        graph.addVertex(goal);

        Collection<Region> obstacles = new LinkedList<Region>();

        for (Rectangle rect : polygons) {

            obstacles.add(rect);

            Rectangle inflated = rect.inflate(1);

            graph.addVertex(inflated.getCorner1());
            graph.addVertex(inflated.getCorner2());
            graph.addVertex(new Point(inflated.getCorner1().x, inflated.getCorner2().y));
            graph.addVertex(new Point(inflated.getCorner2().x, inflated.getCorner1().y));
        }

        Point[] points = graph.vertexSet().toArray(new Point[0]);

        for (int i = 0; i < points.length; i++) {
            for (int j = i + 1; j < points.length; j++) {
                if (Util.isVisible(points[i], points[j], obstacles)) {
                    Line edge1 = graph.addEdge(points[i], points[j]);
                    Line edge2 = graph.addEdge(points[j], points[i]);
                }
            }
        }

        return graph;
    }

    public static Collection<Region> inflateRegions(Collection<? extends Region> obstacles, int agentBodyRadius) {
        Collection<Region> inflatedRegions = new LinkedList<Region>();
        for (Region region : obstacles) {
            if (region instanceof Polygon) {
                inflatedRegions.add(((Polygon) region).inflate(agentBodyRadius, 3));
            } else {
                throw new RuntimeException("not supported region type for inflation");
            }
        }
        return inflatedRegions;
    }

    @SuppressWarnings("unchecked")
    public static Collection<Region>[] inflateRegions(Collection<? extends Region> obstacles, int agentBodyRadiuses[]) {
        int agents = agentBodyRadiuses.length;
        Collection<Region>[] collections = new Collection[agents];

        for (int i = 0; i < agents; i++) {
            collections[i] = Util.inflateRegions(obstacles, agentBodyRadiuses[i]);
        }

        return collections;
    }

    public static void exportTrajectory(Trajectory trajectory, PrintWriter writer, int samplingInterval, int maxTime) {
        for (int t = trajectory.getMinTime(); t < trajectory.getMaxTime() && t < maxTime; t += samplingInterval) {
            Point pos = trajectory.get(t);
            writer.print(t + " " + pos.x + " " + pos.y + ", ");
        }
        writer.flush();
    }


}
