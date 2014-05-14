package tt.euclid2i.discretization;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Tuple2i;

import org.apache.commons.lang3.ArrayUtils;
import org.jgrapht.DirectedGraph;
import org.jgrapht.DummyEdgeFactory;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.Union;
import tt.euclid2i.region.Polygon;
import tt.euclid2i.region.Rectangle;
import tt.euclid2i.util.Util;
import ags.utils.dataStructures.KdTree;
import ags.utils.dataStructures.NearestNeighborIterator;
import ags.utils.dataStructures.SquareEuclideanDistanceFunction;


public class GridBasedRoadmap extends DirectedWeightedMultigraph<Point, Line> {

    private static final long serialVersionUID = 7461735648599585309L;

    private int radius;
    private Point[] customPoints;

    private Collection<Region> boundaryRegions;
    private Collection<Region> obstacles;

    private KdTree<Point> knnTree;
    
    public GridBasedRoadmap(int dispersion, int connectionRadius, Point[] customPoints, Collection<Region> boundaryRegions, Collection<Region> obstacles) {
        super(new DummyEdgeFactory<Point, Line>());
        this.radius = connectionRadius;
        this.customPoints = customPoints;
        this.boundaryRegions = boundaryRegions;
        this.obstacles = obstacles;
        this.knnTree = new KdTree<Point>(2);

        generateVerticesWithDispersion(dispersion);
        generateEdges();
    }

    private void generateEdges() {
        for (Point point : vertexSet()) {
            NearestNeighborIterator<Point> iterator = knnTree.getNearestNeighborIterator(key(point), vertexSet().size(), new SquareEuclideanDistanceFunction());

            while (iterator.hasNext()) {
                Point next = iterator.next();

                if (point.equals(next))
                    continue;

                if (point.distance(next) > radius)
                    break;

                if (Util.isVisible(point, next, obstacles) && Util.isVisible(point, next, boundaryRegions))
                    addEdge(point, next, new Line(point, next));
            }
        }
    }

    private void generateVerticesWithDispersion(int dispersion) {

    	Collection<Region> boundaryRegionsFlipped = new LinkedList<Region>();
        for (Region r : boundaryRegions) {
        	Polygon p = (Polygon)r;
        	Point[] points = Arrays.copyOf(p.getPoints(), p.getPoints().length);
        	if (!Polygon.isClockwise(points))
        		ArrayUtils.reverse(points);
			boundaryRegionsFlipped.add(new Polygon(points));
        }

    	Union samplingRegion = new Union(boundaryRegionsFlipped);
    	Rectangle bounds = samplingRegion.getBoundingBox();
        int width  = bounds.getCorner2().x - bounds.getCorner1().x;
        int height = bounds.getCorner2().y - bounds.getCorner1().y;
        int cellSize = (int) (dispersion/Math.sqrt(2));

        int columns = width / cellSize + 1;
        int rows = height / cellSize + 1;

        for (int row=0; row<rows; row++) {
            for (int col=0; col<columns; col++) {
                int x = bounds.getCorner1().x + col*cellSize + cellSize/2;
                int y = bounds.getCorner1().y + row*cellSize + cellSize/2;
                Point point = new Point(x, y);
                if (samplingRegion.isInside(point)) {
                	if (Util.isInFreeSpace(point, obstacles)) {
		                addVertex(point);
		                knnTree.addPoint(key(point), point);
                	}
                }
            }
        }

        for (int i = 0; i < customPoints.length; i++) {
        	if (bounds.isInside(customPoints[i])) {
	            knnTree.addPoint(key(customPoints[i]), customPoints[i]);
	            addVertex(customPoints[i]);
        	}
        }
    }

//    private void generateNVertices(int nVertices) {
//
//        int width  = samplingRegion.getCorner2().x - samplingRegion.getCorner1().x;
//        int height = samplingRegion.getCorner2().y - samplingRegion.getCorner1().y;
//        int cellSize = (int) Math.sqrt(width*height/nVertices);
//
//        int columns = width / cellSize;
//        int rows = height / cellSize;
//
//        for (int row=0; row<rows; row++) {
//            for (int col=0; col<columns; col++) {
//                int x = samplingRegion.getCorner1().x + col*cellSize + cellSize/2;
//                int y = samplingRegion.getCorner1().y + row*cellSize + cellSize/2;
//                Point point = new Point(x, y);
//                addVertex(point);
//                knnTree.addPoint(key(point), point);
//            }
//        }
//
//        for (int i = 0; i < customPoints.length; i++) {
//            knnTree.addPoint(key(customPoints[i]), customPoints[i]);
//            addVertex(customPoints[i]);
//        }
//    }

    private static double[] key(Point point) {
        return new double[]{point.getX(), point.getY()};
    }

    @Override
    public double getEdgeWeight(Line edge) {
        return edge.getDistance();
    }

}
