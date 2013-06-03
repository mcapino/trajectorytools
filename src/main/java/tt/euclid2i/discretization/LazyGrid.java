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
import tt.euclid2i.Region;
import tt.euclid2i.region.Rectangle;
import tt.euclid2i.util.Util;
import tt.util.NotImplementedException;

public class LazyGrid implements DirectedGraph<Point, Line> {

    public static int[][] PATTERN_4_WAY =  {           {0,-1},
                                             {-1, 0},         { 1, 0},
                                                      {0, 1},          };

    public static int[][] PATTERN_4_WAY_WAIT =  {      {0,-1},
                                             {-1, 0},  {0, 0}, { 1, 0},
                                                       {0, 1},          };

    public static int[][] PATTERN_8_WAY =  {  {-1,-1},   {0,-1},    {1,-1},
                                               {-1, 0},              {1, 0},
                                               {-1, 1},   {0, 1},    {1, 1}};

    public static int[][] PATTERN_8_WAY_WAIT =  {  {-1,-1},   {0,-1},    {1,-1},
                                                  {-1, 0},    {0, 0},    {1, 0},
                                                  {-1, 1},    {0, 1},    {1, 1}};

    private Point initialPoint;
    private Rectangle bounds;
    private int step;
    private int[][] pattern;
    private Collection<Region> obstacles;

    public LazyGrid(Point initialPoint, Collection<Region> obstacles, Rectangle bounds, int[][] pattern, int step) {
        this.initialPoint = initialPoint;
        this.bounds = bounds;
        this.obstacles = obstacles;
        this.step = step;

        // scale the pattern by step parameter
        this.pattern = new int[pattern.length][2];
        for (int i = 0; i < pattern.length; i++) {
            this.pattern[i][0] = pattern[i][0] * step;
            this.pattern[i][1] = pattern[i][1] * step;
        }
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
        return (p.x - initialPoint.x) % step == 0 && (p.y - initialPoint.y) % step == 0 && bounds.isInside(p);
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
        Set<Point> children = new HashSet<Point>();

        for (int[] offset : pattern) {
            Point child = new Point(vertex.x + offset[0], vertex.y + offset[1]);
            if (bounds.isInside(child) && Util.isVisible(vertex, child, obstacles)) {
                children.add(child);
            }
        }

        Set<Line> edges = new HashSet<Line>();
        for (Point child : children) {
            edges.add(new Line(child, vertex));
        }

        return edges;
    }

    @Override
    public int outDegreeOf(Point vertex) {
        return outgoingEdgesOf(vertex).size();
    }

    @Override
    public Set<Line> outgoingEdgesOf(Point vertex) {
        Set<Point> children = new HashSet<Point>();

        for (int[] offset : pattern) {
            Point child = new Point(vertex.x + offset[0], vertex.y + offset[1]);
            if (bounds.isInside(child) && Util.isVisible(vertex, child, obstacles)) {
                children.add(child);
            }
        }

        Set<Line> edges = new HashSet<Line>();
        for (Point child : children) {
            edges.add(new Line(vertex, child));
        }

        return edges;
    }

    @Override
    public boolean removeAllEdges(Collection<? extends Line> arg0) {
        throw new NotImplementedException();
    }

    @Override
    public boolean removeAllVertices(Collection<? extends Point> arg0) {
        throw new NotImplementedException();
    }

    public DirectedGraph<Point, Line> generateFullGraph() {
        return GraphBuilder.build(this, new DefaultDirectedGraph<Point, Line>(Line.class), initialPoint);
    }
}
