package tt.euclid2i.discretization;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.AbstractDirectedGraphWrapper;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.util.GraphBuilder;
import tt.euclid2i.Line;
import tt.euclid2i.Point;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class AdditionalPointsExtension extends AbstractDirectedGraphWrapper<Point, Line> {

    DirectedGraph<Point, Line> graph;
    Set<Point> points;
    int radius;

    HashMap<Point, Set<Line>> newIncomingEdges;
    HashMap<Point, Set<Line>> newOutgoingEdges;

    public AdditionalPointsExtension(DirectedGraph<Point, Line> graph, Point goal, int radius) {
        this(graph, Collections.singleton(goal), radius);
    }

    public AdditionalPointsExtension(DirectedGraph<Point, Line> graph, Set<Point> points, int radius) {
        super();
        this.graph = graph;
        this.points = points;
        this.radius = radius;
        this.newIncomingEdges = new HashMap<Point, Set<Line>>();
        this.newOutgoingEdges = new HashMap<Point, Set<Line>>();
        prepareNewEdges();
    }

    private void prepareNewEdges() {
        Set<Point> vertexSet = graph.vertexSet();
        for (Point newPoint : points) {
            Set<Line> incoming = new HashSet<Line>();
            Set<Line> outgoing = new HashSet<Line>();

            for (Point graphPoint : vertexSet) {
                if (newPoint.distance(graphPoint) > radius)
                    continue;

                incoming.add(new Line(graphPoint, newPoint));
                outgoing.add(new Line(newPoint, graphPoint));
            }

            newIncomingEdges.put(newPoint, incoming);
            newOutgoingEdges.put(newPoint, outgoing);
        }
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
        if (graph.containsVertex(vertex)) {
            Set<Line> edges = new HashSet<Line>();
            edges.addAll(graph.incomingEdgesOf(vertex));

            for (Point point : points) {
                if (vertex.distance(point) <= radius)
                    edges.add(new Line(point, vertex));
            }

            return edges;

        } else if (points.contains(vertex)) {
            return newIncomingEdges.get(vertex);

        } else {
            throw new RuntimeException("Decorated graph and decorator itself do not contain vertex" + vertex);
        }
    }

    @Override
    public int outDegreeOf(Point vertex) {
        return outgoingEdgesOf(vertex).size();
    }

    @Override
    public Set<Line> outgoingEdgesOf(Point vertex) {
        if (graph.containsVertex(vertex)) {
            Set<Line> edges = new HashSet<Line>();
            edges.addAll(graph.outgoingEdgesOf(vertex));

            for (Point point : points) {
                if (vertex.distance(point) <= radius)
                    edges.add(new Line(vertex, point));
            }

            return edges;
        } else if (points.contains(vertex)) {
            return newOutgoingEdges.get(vertex);

        } else {
            throw new RuntimeException("Decorated graph and decorator itself do not contain vertex" + vertex);
        }
    }

    public DirectedGraph<Point, Line> generateFullGraph(Point initialPoint) {
        DirectedGraph<Point, Line> fullGraph
                = new DefaultDirectedGraph<Point, Line>(Line.class);

        return GraphBuilder.build(this, fullGraph, initialPoint);
    }
}
