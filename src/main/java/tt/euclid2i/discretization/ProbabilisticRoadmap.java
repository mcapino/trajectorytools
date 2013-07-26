package tt.euclid2i.discretization;

import java.util.Collection;
import java.util.Random;

import org.jgrapht.DummyEdgeFactory;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.region.Rectangle;
import tt.euclid2i.util.Util;


public class ProbabilisticRoadmap extends DirectedWeightedMultigraph<Point, Line>{
    private static final long serialVersionUID = 7461735648599585309L;

    public ProbabilisticRoadmap(int n, int radius, Point[] customPoints, Rectangle bounds, Collection<Region> obstacles, Random random) {
        super(new DummyEdgeFactory<Point, Line>());

        Point[] points = new Point[n+customPoints.length];

        for (int i=0; i<n; i++) {
            int x = random.nextInt(bounds.getCorner2().x);
            int y = random.nextInt(bounds.getCorner2().y);
            Point point = new Point(x,y);
            addVertex(point);
            points[i] = point;
        }

        for (int i=0; i<customPoints.length; i++) {
            addVertex(customPoints[i]);
            points[n+i] = customPoints[i];
        }

        for (int i=0; i<points.length; i++) {
            for (int j=0; j<points.length; j++) {
                if (i != j && points[i].distance(points[j]) < radius && Util.isVisible(points[i], points[j], obstacles)) {
                    Line edge = new Line(points[i], points[j]);
                    addEdge(points[i], points[j], edge);
                }
            }
        }
    }

    @Override
    public double getEdgeWeight(Line edge) {
        return edge.getDistance();
    }

}
