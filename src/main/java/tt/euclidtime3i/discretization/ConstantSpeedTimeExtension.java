package tt.euclidtime3i.discretization;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.AbstractDirectedGraphWrapper;

import tt.euclid2i.Line;
import tt.euclidtime3i.Point;
import tt.euclidtime3i.Region;

public class ConstantSpeedTimeExtension extends AbstractDirectedGraphWrapper<Point, Straight> {

    private DirectedGraph<tt.euclid2i.Point, tt.euclid2i.Line> spatialGraph;
    private int maxTime;
    private int[] speeds;
    private Collection<Region> dynamicObstacles;

    public final static int DISABLE_WAIT_MOVE = 0;
    private int waitMoveDuration;

    public ConstantSpeedTimeExtension(
            DirectedGraph<tt.euclid2i.Point, Line> spatialGraph, int maxTime,
            int[] speeds, Collection<Region> dynamicObstacles, int waitMoveDuration) {
        super();
        this.spatialGraph = spatialGraph;
        this.maxTime = maxTime;
        this.speeds = speeds;
        this.dynamicObstacles = dynamicObstacles;
        this.waitMoveDuration = waitMoveDuration;
    }

    public ConstantSpeedTimeExtension(
            DirectedGraph<tt.euclid2i.Point, Line> spatialGraph, int maxTime,
            int[] speeds, int waitMoveDuration) {
       this(spatialGraph, maxTime, speeds, new LinkedList<Region>(), waitMoveDuration);
    }

    public ConstantSpeedTimeExtension(
            DirectedGraph<tt.euclid2i.Point, Line> spatialGraph, int maxTime,
            int[] speeds) {
       this(spatialGraph, maxTime, speeds, new LinkedList<Region>(), DISABLE_WAIT_MOVE);
    }

    @Override
    public boolean containsVertex(Point p) {
        return spatialGraph.containsVertex(p.getPosition()) &&
                p.getTime() <= maxTime;
    }

    @Override
    public Set<Straight> edgesOf(Point vertex) {
        Set<Straight> edges = new LinkedHashSet<Straight>();
        //edges.addAll(incomingEdgesOf(vertex));
        edges.addAll(outgoingEdgesOf(vertex));
        return edges;
    }

    @Override
    public Set<Straight> getAllEdges(Point start, Point end) {
        Set<Straight> edges = new LinkedHashSet<Straight>();
        edges.add(new Straight(start, end));
        edges.add(new Straight(end, start));
        return edges;
    }

    @Override
    public Straight getEdge(Point start, Point end) {
        return new Straight(start, end);
    }

    @Override
    public EdgeFactory<Point, Straight> getEdgeFactory() {
        return null;
    }

    @Override
    public Point getEdgeSource(Straight edge) {
        return edge.getStart();
    }

    @Override
    public Point getEdgeTarget(Straight edge) {
        return edge.getEnd();
    }

    @Override
    public double getEdgeWeight(Straight edge) {
        return edge.getEnd().getTime() - edge.getStart().getTime();
    }


    @Override
    public int outDegreeOf(Point vertex) {
        return outgoingEdgesOf(vertex).size();
    }

    @Override
    public Set<Straight> outgoingEdgesOf(Point vertex) {
	    if (vertex.getTime() < maxTime) {
	    	Set<Point> children = new HashSet<Point>();

	        Set<Line> spatialEdges = spatialGraph.outgoingEdgesOf(new tt.euclid2i.Point(vertex.x, vertex.y));
	        for (Line spatialEdge : spatialEdges) {
	            for (int speed : speeds) {
	                Point child = new Point(spatialEdge.getEnd().x, spatialEdge.getEnd().y, vertex.getTime() + (int) Math.round(spatialEdge.getDistance() / speed));
	                if (child.getTime() <= maxTime && isVisible(vertex, child, dynamicObstacles)) {
	                    children.add(child);
	                }
	            }
	        }

	        Set<Straight> edges = new HashSet<Straight>();

	        if (waitMoveDuration != DISABLE_WAIT_MOVE) {
	        	int endTime = vertex.getTime() + waitMoveDuration;

	        	if (endTime > maxTime) {
	        		endTime = maxTime;
	        	}

	            Point endPoint = new tt.euclidtime3i.Point(vertex.x, vertex.y, endTime);
	            if (isVisible(vertex, endPoint, dynamicObstacles)) {
	                edges.add(new Straight(vertex, endPoint));
	            }
	        }

	        for (Point child : children) {
	            edges.add(new Straight(vertex, child));
	        }

	        return edges;
        } else {
        	return new HashSet<Straight>();
        }
    }

    private boolean isVisible(Point start, Point end, Collection<Region> obstacles) {
        for (Region obstacle : obstacles) {
            if (obstacle.intersectsLine(start, end)) {
                return false;
            }
        }
        return true;
    }
}
