package tt.euclid2i.discretization;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.util.GraphBuilder;

import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.util.NotImplementedException;

public class ToGoalEdgeExtension implements DirectedGraph<Point, Line> {

    DirectedGraph<Point, Line> graph;
    Point goalPoint;
    int radius;

    public ToGoalEdgeExtension(DirectedGraph<Point, Line> graph,
            Point goalPoint, int radius) {
        super();
        this.graph = graph;
        this.goalPoint = goalPoint;
        this.radius = radius;
    }

    @Override
    public Line addEdge(Point arg0, Point arg1) {
       throw new NotImplementedException();
    }

    @Override
    public boolean addEdge(Point arg0, Point arg1, Line arg2) {
        throw new NotImplementedException();
    }

    @Override
    public boolean addVertex(Point arg0) {
        throw new NotImplementedException();
    }

    @Override
    public boolean containsEdge(Line arg0) {
        throw new NotImplementedException();
    }

    @Override
    public boolean containsEdge(Point arg0, Point arg1) {
        throw new NotImplementedException();
    }

    @Override
    public boolean containsVertex(Point p) {
        return graph.containsVertex(p) || goalPoint.equals(p);
    }

    @Override
    public Set<Line> edgeSet() {
        throw new NotImplementedException();
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
        Set<Line> edges = new HashSet<Line>();
        edges.add(new Line(start, end) );
        edges.add(new Line(end, start) );
        return edges;
    }

    @Override
    public Line getEdge(Point start, Point end) {
        return new Line(start, end);
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
    public Set<Line> removeAllEdges(Point arg0, Point arg1) {
        throw new NotImplementedException();
    }

    @Override
    public boolean removeEdge(Line arg0) {
        throw new NotImplementedException();
    }

    @Override
    public Line removeEdge(Point arg0, Point arg1) {
        throw new NotImplementedException();
    }

    @Override
    public boolean removeVertex(Point arg0) {
        throw new NotImplementedException();
    }

    @Override
    public Set<Point> vertexSet() {
        throw new NotImplementedException();
    }

    @Override
    public int inDegreeOf(Point vertex) {
        return incomingEdgesOf(vertex).size();
    }

    @Override
    public Set<Line> incomingEdgesOf(Point vertex) {
        if (vertex.equals(goalPoint)) {
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

        if (vertex.distance(goalPoint) <= radius) {
            Set<Line> edges = new HashSet<Line>();
            edges.addAll(graph.outgoingEdgesOf(vertex));
            edges.add(new Line(vertex, goalPoint));
            return edges;
        } else {
            return graph.outgoingEdgesOf(vertex);
        }
    }

    @Override
    public boolean removeAllEdges(Collection<? extends Line> arg0) {
        throw new NotImplementedException();
    }

    @Override
    public boolean removeAllVertices(Collection<? extends Point> arg0) {
        throw new NotImplementedException();
    }

    public DirectedGraph<Point, Line> generateFullGraph(Point initialPoint) {
        DirectedGraph<Point, Line> fullGraph
            = new DefaultDirectedGraph<Point, Line>(Line.class);

        return GraphBuilder.build(this, fullGraph, initialPoint);
    }
}
