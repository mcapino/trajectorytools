package tt.euclid2i.discretization;

import org.jgrapht.EdgeFactory;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import tt.euclid2i.Point;
import tt.euclid2i.Line;
import tt.euclid2i.Region;
import tt.euclid2i.region.Polygon;
import tt.euclid2i.util.Util;
import tt.util.NotImplementedException;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

public class VisibilityGraph {

	public static WeightedGraph<Point, Line> createVisibilityGraph(Collection<Region> obstacles, int agentRadius, Collection<Point> additionalSignificantPoints) {

		Collection<Region> inflatedObstaclesForCollisionChecking = Util.inflateRegions(obstacles, agentRadius);
		Collection<Region> inflatedObstaclesForGraph = Util.inflateRegions(obstacles, agentRadius+1);

		return createVisibilityGraph(inflatedObstaclesForCollisionChecking, inflatedObstaclesForGraph, additionalSignificantPoints);
	}

    public static WeightedGraph<Point, Line> createVisibilityGraph(Collection<Region> inflatedObstaclesForCollisionChecking,
    		Collection<Region> inflatedObstaclesForGraph, Collection<Point> additionalSignificantPoints) {

        @SuppressWarnings("serial")
		WeightedGraph<Point, Line> visibilityGraph = new SimpleWeightedGraph<Point, Line>(
		new EdgeFactory<Point, Line>() {

			@Override
			public Line createEdge(Point from, Point to) {
				return new Line(from, to);
			}

		}) {

			@Override
			public double getEdgeWeight(Line line) {
				return line.getDistance();
			}

			@Override
			@Deprecated
			public void setEdgeWeight(Line arg0, double arg1) {
				throw new NotImplementedException();
			}
        };

        Collection<Point> significantPoints = new LinkedList<Point>();
        for (Region inflatedObstacle : inflatedObstaclesForGraph) {
            Polygon polygon = (Polygon) inflatedObstacle;
            significantPoints.addAll(Arrays.asList(polygon.getPoints()));
        }

        significantPoints.addAll(additionalSignificantPoints);

        // add points
        for (Point signPoint : significantPoints) {
        	 if (!conflicting(inflatedObstaclesForCollisionChecking, signPoint)) {
        		 visibilityGraph.addVertex(signPoint);
             }
        }

        Point[] vertices = visibilityGraph.vertexSet().toArray(new Point[0]);

        for (int i=0; i < vertices.length; i++) {
        	for (int j=i+1; j < vertices.length; j++) {
        		if (!conflicting(inflatedObstaclesForCollisionChecking, vertices[i], vertices[j])) {

        			 if (!vertices[i].equals(vertices[j]) && visibilityGraph.containsVertex(vertices[i])
        		                && visibilityGraph.containsVertex(vertices[j])) {

        				    visibilityGraph.addEdge(vertices[i], vertices[j]);
        		        }
                }
            }
        }
        return visibilityGraph;
    }

    private static boolean conflicting(Collection<Region> inflatedObstacles, Point p1m, Point p2m) {

        for (Region region : inflatedObstacles) {
            if (region.intersectsLine(p1m, p2m)) {
                return true;
            }
        }
        return false;
    }

    private static boolean conflicting(Collection<Region> inflatedObstacles, Point p1) {
        for (Region region : inflatedObstacles) {
            if (region.isInside(p1)) {
                return true;
            }
        }
        return false;
    }
}
