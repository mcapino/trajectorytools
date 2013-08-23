package tt.euclid2i.discretization;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.AbstractDirectedGraphWrapper;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.util.GraphBuilder;
import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.util.NotImplementedException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ToGoalEdgeExtension extends AbstractDirectedGraphWrapper<Point, Line> {

    DirectedGraph<Point, Line> graph;
    Set<Point> points;
    int radius;

    public ToGoalEdgeExtension(DirectedGraph<Point, Line> graph, Point goal, int radius) {
        super();
        this.graph = graph;
        this.points = Collections.singleton(goal);
        this.radius = radius;
    }

    public ToGoalEdgeExtension(DirectedGraph<Point, Line> graph, Set<Point> points, int radius) {
        super();
        this.graph = graph;
        this.points = points;
        this.radius = radius;
    }

    @Override
    public boolean containsVertex(Point p) {
        return graph.containsVertex(p) || points.contains(p);
    }

    @Override
    public Set<Line> edgesOf(Point vertex) {
        Set<Line> edges = new HashSet<Line>();
        edges.addAll(incomingEdgesOf(vertex));
        edges.addAll(outgoingEdgesOf(vertex));
        return edges;
    }

    @Override
    public Set<Line> getAllEdges(Point start, Point end) {
        checkEdgeIsInGraph(start, end);

        Set<Line> edges = new HashSet<Line>();
        edges.add(new Line(start, end));
        edges.add(new Line(end, start));

        return edges;
    }

    @Override
    public Line getEdge(Point start, Point end) {
        checkEdgeIsInGraph(start, end);
        return new Line(start, end);
    }

    private void checkEdgeIsInGraph(Point start, Point end) {
        if (!containsVertex(start) || !containsVertex(end))
            throw new RuntimeException("At least one of the nodes is not present in the graph");
    }

    @Override
    public EdgeFactory<Point, Line> getEdgeFactory() {
        return null;
    }

    @Override
    public Point getEdgeSource(Line edge) {
        return edge.getStart();
    }

    @Override
    public Point getEdgeTarget(Line edge) {
        return edge.getEnd();
    }

    @Override
    public double getEdgeWeight(Line edge) {
        return edge.getDistance();
    }


    @Override
    public int inDegreeOf(Point vertex) {
        return incomingEdgesOf(vertex).size();
    }

    @Override
    public Set<Line> incomingEdgesOf(Point vertex) {
        if (points.contains(vertex)) {
            throw new NotImplementedException();
        } else {
            return graph.incomingEdgesOf(vertex);
        }
    }

    @Override
    public int outDegreeOf(Point vertex) {
        return outgoingEdgesOf(vertex).size();
    }

    @Override
    public Set<Line> outgoingEdgesOf(Point vertex) {
        Set<Line> edges = new HashSet<Line>();
        edges.addAll(graph.outgoingEdgesOf(vertex));

        for (Point point : points) {
            if (vertex.distance(point) <= radius)
                edges.add(new Line(vertex, point));
        }

        return edges;
    }

    public DirectedGraph<Point, Line> generateFullGraph(Point initialPoint) {
        DirectedGraph<Point, Line> fullGraph
                = new DefaultDirectedGraph<Point, Line>(Line.class);

        return GraphBuilder.build(this, fullGraph, initialPoint);
    }
}
