package tt.euclid2d.discretization;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DefaultDirectedGraph;

import tt.euclid2d.Point;
import tt.euclid2d.region.Rectangle;
import tt.euclid2d.region.Region;
import cz.agents.alite.trajectorytools.util.NotImplementedException;

public class LazyGrid implements DirectedGraph<Point, Straight2d> {

    private static final double SPEED = 1.0;
    private Point initialPoint;
    private Rectangle bounds;
    private double step;
    private double[][] pattern;
    private Collection<Region> obstacles;

    public LazyGrid(Point initialPoint, Collection<Region> obstacles, Rectangle bounds, double step) {
        this.initialPoint = initialPoint;
        this.bounds = bounds;
        this.obstacles = obstacles;
        this.step = step;


        this.pattern = new double[][] {           {0,-step},
                                        {-step, 0},         { step, 0},
                                                  {0, step},          };
    }

    @Override
    public Straight2d addEdge(Point arg0, Point arg1) {
       throw new NotImplementedException();
    }

    @Override
    public boolean addEdge(Point arg0, Point arg1, Straight2d arg2) {
        throw new NotImplementedException();
    }

    @Override
    public boolean addVertex(Point arg0) {
        throw new NotImplementedException();
    }

    @Override
    public boolean containsEdge(Straight2d arg0) {
        throw new NotImplementedException();
    }

    @Override
    public boolean containsEdge(Point arg0, Point arg1) {
        throw new NotImplementedException();
    }

    @Override
    public boolean containsVertex(Point arg0) {
        throw new NotImplementedException();
    }

    @Override
    public Set<Straight2d> edgeSet() {
        throw new NotImplementedException();
    }

    @Override
    public Set<Straight2d> edgesOf(Point vertex) {
        Set<Straight2d> edges = new HashSet<Straight2d>();
        edges.addAll(incomingEdgesOf(vertex));
        edges.addAll(outgoingEdgesOf(vertex));
        return edges;
    }

    @Override
    public Set<Straight2d> getAllEdges(Point start, Point end) {
        Set<Straight2d> edges = new HashSet<Straight2d>();
        edges.add(new Straight2d(start, end, SPEED) );
        edges.add(new Straight2d(end, start, SPEED) );
        return edges;
    }

    @Override
    public Straight2d getEdge(Point start, Point end) {
        return new Straight2d(start, end, SPEED);
    }

    @Override
    public EdgeFactory<Point, Straight2d> getEdgeFactory() {
        return null;
    }

    @Override
    public Point getEdgeSource(Straight2d edge) {
        return edge.getStart();
    }

    @Override
    public Point getEdgeTarget(Straight2d edge) {
        return edge.getEnd();
    }

    @Override
    public double getEdgeWeight(Straight2d edge) {
        return edge.getDuration();
    }


    @Override
    public Set<Straight2d> removeAllEdges(Point arg0, Point arg1) {
        throw new NotImplementedException();
    }

    @Override
    public boolean removeEdge(Straight2d arg0) {
        throw new NotImplementedException();
    }

    @Override
    public Straight2d removeEdge(Point arg0, Point arg1) {
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
    public Set<Straight2d> incomingEdgesOf(Point vertex) {
        Set<Point> children = new HashSet<Point>();

        for (double[] offset : pattern) {
            Point child = new Point(vertex.x + offset[0], vertex.y + offset[1]);
            if (bounds.isInside(child) && isVisible(vertex, child)) {
                children.add(child);
            }
        }

        Set<Straight2d> edges = new HashSet<Straight2d>();
        for (Point child : children) {
            edges.add(new Straight2d(child, vertex, SPEED));
        }

        return edges;
    }

    @Override
    public int outDegreeOf(Point vertex) {
        return outgoingEdgesOf(vertex).size();
    }

    @Override
    public Set<Straight2d> outgoingEdgesOf(Point vertex) {
        Set<Point> children = new HashSet<Point>();

        for (double[] offset : pattern) {
            Point child = new Point(vertex.x + offset[0], vertex.y + offset[1]);
            if (bounds.isInside(child) && isVisible(vertex, child)) {
                children.add(child);
            }
        }

        Set<Straight2d> edges = new HashSet<Straight2d>();
        for (Point child : children) {
            edges.add(new Straight2d(vertex, child, SPEED));
        }

        return edges;
    }

    protected boolean isVisible(Point start, Point end) {
        // check obstacles
        for (Region obstacle : obstacles) {
            if (obstacle.intersectsLine(start, end)) {
                return false;
            }
        }
        return true;
    }

    protected boolean isInFreeSpace(Point point) {
        for (Region obstacle : obstacles) {
            if (obstacle.isInside(point)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean removeAllEdges(Collection<? extends Straight2d> arg0) {
        throw new NotImplementedException();
    }

    @Override
    public boolean removeAllVertices(Collection<? extends Point> arg0) {
        throw new NotImplementedException();
    }

    public DirectedGraph<Point, Straight2d> generateFullGraph() {
        DefaultDirectedGraph<Point, Straight2d> fullGraph
            = new DefaultDirectedGraph<Point, Straight2d>( new EdgeFactory<Point, Straight2d>() {

                @Override
                public Straight2d createEdge(Point sourceVertex,
                        Point targetVertex) {
                    return new Straight2d(sourceVertex, targetVertex, SPEED);
                }
            });


        Queue<Point> open = new LinkedList<Point>();
        open.offer(initialPoint);
        Set<Point> closed = new HashSet<Point>();
        int iterations = 0;

        while (!open.isEmpty()) {
            iterations++;
            Point current = open.poll();
            fullGraph.addVertex(current);

            Set<Straight2d> outEdges = outgoingEdgesOf(current);
            for (Straight2d edge : outEdges) {
                Point target = getEdgeTarget(edge);
                fullGraph.addVertex(target);
                fullGraph.addEdge(current, target);

                if (!closed.contains(target)) { // it must mean that this closed.contains(target) is always true
                    closed.add(target);
                    open.offer(target);
                }
            }
        }

        System.out.println("iterations:" + iterations);
        System.out.println("closed:" + closed.size());
        return fullGraph;
    }
}
