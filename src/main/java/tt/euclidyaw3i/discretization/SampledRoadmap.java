package tt.euclidyaw3i.discretization;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import tt.euclid2i.region.Polygon;
import tt.euclid2i.region.Rectangle;
import tt.euclidyaw3i.Point;

import java.util.Collection;

public class SampledRoadmap {
    public static DirectedGraph<Point, PathSegment>
    buildLattice(Rectangle boundingBox, int cols, int rows, int angles,
                 Collection<Point> specialPoints, Distance distance, double connectionDistance,
                 Steering steering, Polygon footprint, Collection<Polygon> obstacles) {

        DirectedWeightedMultigraph<Point, PathSegment> graph = new DirectedWeightedMultigraph<Point, PathSegment>(PathSegment.class) {
            @Override
            public double getEdgeWeight(PathSegment pathSegment) {
                return pathSegment.getLength();
            }
        };

        // generate vertices
        double hSpacing = (boundingBox.getCorner2().x - boundingBox.getCorner1().x) / (cols);
        double vSpacing = (boundingBox.getCorner2().y - boundingBox.getCorner1().y) / (rows);

        for (int row=0; row < rows; row++) {
            for (int col=0; col < cols; col++) {
                int x = boundingBox.getCorner1().x + (int) Math.round(hSpacing/2 + row * hSpacing);
                int y = boundingBox.getCorner1().y + (int) Math.round(vSpacing/2 + col * vSpacing);

                double angleStep = (2 * Math.PI) / (angles);
                for (double angle= -Math.PI; angle < 0.9999*Math.PI; angle += angleStep) {
                    graph.addVertex(new Point(x, y, (float) angle));
                }
            }
        }

        for(Point point : specialPoints) {
            graph.addVertex(point);
        }

        // generate edges
        for (Point vertex : graph.vertexSet()) {
            for (Point other : graph.vertexSet()) {
                double d = distance.getDistance(vertex, other);
                if (d < connectionDistance && !vertex.equals(other)) {
                    PathSegment path = steering.getSteering(vertex, other);
                    if (path != null && CollisionCheck.collisionFree(path, footprint, obstacles)) {
                        graph.addEdge(vertex, other, path);
                    }
                }
            }

        }

        return graph;
    }
}
