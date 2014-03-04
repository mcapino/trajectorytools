package tt.euclid2i.util;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.Trajectory;
import tt.euclid2i.discretization.GridBasedRoadmap;
import tt.euclid2i.region.Polygon;
import tt.euclid2i.region.Rectangle;

public class Util {

    public static boolean isVisible(Point start, Point end, Collection<? extends Region> obstacles) {
        // check obstacles
        for (Region obstacle : obstacles) {
            if (obstacle.intersectsLine(start, end)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isInFreeSpace(Point point, Region boundary, Collection<? extends Region> obstacles) {

    	if (!boundary.isInside(point)) {
    		return false;
    	}

    	for (Region obstacle : obstacles) {
            if (obstacle.isInside(point)) {
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

    public static Point sampleFreeSpace(Region boundary, Collection<? extends Region> obstacles, Random random) {

        Point point;
        do {
            point = sampleSpace(boundary.getBoundingBox(), random);
        } while (!isInFreeSpace(point, boundary, obstacles));

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
                inflatedRegions.addAll(((Polygon) region).inflate(agentBodyRadius, 3));
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

    public static Point[] breakLineToSegments(Line line, double maxLengthOfSegment) {
        int nPoints = (int) Math.floor(line.getDistance()/maxLengthOfSegment) + 2;
        double lambdaStep = maxLengthOfSegment/line.getDistance();
        Point[] points = new Point[nPoints];
        points[0] = line.getStart();
        for (int i=1; i<nPoints-1; i++) {
            points[i] = line.interpolate(lambdaStep * i);
        }
        points[nPoints-1] = line.getEnd();
        return points;
    }

    public static DirectedGraph<tt.euclid2i.Point, Line> buildGridBasedRoadmap(
            final Collection<Region> lessInflatedObstacles,
            final Collection<Region> moreInflatedObstacles,
            final Collection<Region> boundaryRegions,
            int dispersion,
            int connectionRadius, Collection<Point> additionalPoints) {

        DirectedGraph<tt.euclid2i.Point, Line> spatialGraph;

        List<Point> customPoints = new LinkedList<Point>();
        customPoints.addAll(additionalPoints);

        for (Region region : moreInflatedObstacles) {
            assert region instanceof Polygon;
            Polygon polygon = (Polygon) region;
            Point[] points = polygon.getPoints();
            // break each line segment of the polygon into small segments shorter than connection radius
            for (int j=0; j<points.length; j++) {
                Line line;
                if (j < points.length-1) {
                    line = new Line(points[j], points[j+1]);
                } else /* j is the last point, close the polygon */ {
                    line = new Line(points[j], points[0]);
                }
                customPoints.addAll(Arrays.asList(Util.breakLineToSegments(line, connectionRadius-1)));
            }
        }

        spatialGraph = new GridBasedRoadmap(
                dispersion,
                connectionRadius,
                customPoints.toArray(new Point[customPoints.size()]),
                boundaryRegions, lessInflatedObstacles);

        return spatialGraph;
    }

    public static void addVertexAndConnectToNeighbors(DirectedGraph<Point, Line> graph, Point vertexToAdd, int n, Collection<Region> obstacles) {

        Set<Point> otherVertices = new HashSet<Point>(graph.vertexSet());
        graph.addVertex(vertexToAdd);

        for (int i=0; i < n; i++) {
            Point bestVertex = null;

            for (Point vertex : otherVertices) {
                if (bestVertex == null  ||
                        (
                        	bestVertex.distance(vertexToAdd) > vertex.distance(vertexToAdd) &&
                        	Util.isVisible(vertex, vertexToAdd, obstacles)
                        )

                   )
                {
                    bestVertex = vertex;
                }
            }

            graph.addVertex(bestVertex);
            graph.addEdge(vertexToAdd, bestVertex);
            graph.addEdge(bestVertex, vertexToAdd);
            otherVertices.remove(bestVertex);
        }
    }
}
