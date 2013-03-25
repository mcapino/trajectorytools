package tt.euclid2i.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import tt.euclid2i.Point;
import tt.euclid2i.discretization.Line;
import tt.euclid2i.region.Rectangle;
import tt.euclid2i.region.Region;

public class Util {

    public static boolean isVisible(Point start, Point end, Collection<Region> obstacles) {
        // check obstacles
        for (Region obstacle : obstacles) {
            if (obstacle.isInside(start) || obstacle.isInside(end) || obstacle.intersectsLine(start, end)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isInFreeSpace(Point point, Collection<Region> obstacles) {
        for (Region obstacle : obstacles) {
            if (obstacle.isInside(point)) {
                return false;
            }
        }

        return true;
    }

    public static Point sampleFreeSpace(Rectangle bounds, Collection<Region> obstacles, Random random) {

        Point point;
        do {
            point = sampleSpace(bounds, random);
        } while (!isInFreeSpace(point, obstacles));

        return point;
    }

    public static Point sampleSpace(Rectangle bounds, Random random) {
        int x = bounds.getCorner1().x + random.nextInt(bounds.getCorner2().x - bounds.getCorner1().x);
        int y = bounds.getCorner1().y + random.nextInt(bounds.getCorner2().y - bounds.getCorner1().y);
        return new Point(x, y);
    }

    public static DirectedGraph<Point, Line> getVisibilityGraph(Point start, Point goal, Collection<Rectangle> polygons) {
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

        for (int i=0; i<points.length; i++) {
            for (int j=i+1; j<points.length; j++) {
                if (Util.isVisible(points[i], points[j], obstacles)) {
                    Line edge1 = graph.addEdge(points[i], points[j]);
                    Line edge2 = graph.addEdge(points[j], points[i]);
                }
            }
        }

        return graph;
    }

}
