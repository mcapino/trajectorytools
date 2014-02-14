package tt.euclidtime3i.discretization;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.AbstractDirectedGraphWrapper;
import tt.euclidtime3i.Point;

import java.util.HashSet;
import java.util.Set;

public class WaitToMaxTimeExtension extends AbstractDirectedGraphWrapper<Point, Straight> {

    private DirectedGraph<Point, Straight> graph;
    private Point goal;

    private int maxTime;
    private tt.euclid2i.Point spatialGoal;

    public WaitToMaxTimeExtension(DirectedGraph<Point, Straight> graph, tt.euclid2i.Point spatialGoal, int maxTime) {
        this.graph = graph;
        this.spatialGoal = spatialGoal;
        this.maxTime = maxTime;
        this.goal = new Point(spatialGoal, maxTime);
    }

    public Point getSpatialGoal() {
        return (Point) goal.clone();
    }

    @Override
    public double getEdgeWeight(Straight straight) {
        Point start = straight.getStart();
        Point end = straight.getEnd();

        if (isInTarget(start) && isInTarget(end) && endsInMaxTime(end))
            return 0;
        else
            return graph.getEdgeWeight(straight);
    }

    private boolean endsInMaxTime(Point end) {
        return end.getTime() == maxTime;
    }

    private boolean isInTarget(Point point) {
        tt.euclid2i.Point position = point.getPosition();
        return position.equals(spatialGoal);
    }

    @Override
    public Set<Straight> outgoingEdgesOf(Point vertex) {
        Set<Straight> underlyingEdges = graph.outgoingEdgesOf(vertex);

        if (!isInTarget(vertex))
            return underlyingEdges;
        else
            return addWaitToMaxTimeEdge(vertex, underlyingEdges);
    }

    private Set<Straight> addWaitToMaxTimeEdge(Point vertex, Set<Straight> underlyingEdges) {
        Set<Straight> edges = new HashSet<Straight>(underlyingEdges);
        edges.add(new Straight(vertex, goal));
        return edges;
    }

    @Override
    public int outDegreeOf(Point vertex) {
        return outgoingEdgesOf(vertex).size();
    }

    @Override
    public Set<Straight> edgesOf(Point vertex) {
        Set<Straight> edges = new HashSet<Straight>();
        edges.addAll(outgoingEdgesOf(vertex));
        edges.addAll(incomingEdgesOf(vertex));
        return edges;
    }


}
