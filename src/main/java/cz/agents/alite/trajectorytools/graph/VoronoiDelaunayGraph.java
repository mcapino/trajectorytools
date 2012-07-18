package cz.agents.alite.trajectorytools.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;

import cz.agents.alite.trajectorytools.graph.delaunay.Pnt;
import cz.agents.alite.trajectorytools.graph.delaunay.Triangle;
import cz.agents.alite.trajectorytools.graph.delaunay.Triangulation;
import cz.agents.alite.trajectorytools.util.Point;

public class VoronoiDelaunayGraph {
  
    private static int INITIAL_SIZE = 10000;     // Size of initial triangle

    /**
     * there are two cases each of them is solved better with different value 
     * (this is grid case problem only - use true for non grid cases)
     */
    private static boolean USE_STRAIGHT_DELAUNAY_EDGES_ONLY = true;

    private Triangle initialTriangle = new Triangle(
            new Pnt(-INITIAL_SIZE, -INITIAL_SIZE),
            new Pnt( INITIAL_SIZE, -INITIAL_SIZE),
            new Pnt(           0,  INITIAL_SIZE));  // Initial triangle
    private Triangulation dt;                      // Delaunay triangulation

    private static int order = 1000;

    private Set<Point> obstacles;

    private Graph<Point, DefaultWeightedEdge> voronoiGraph = null;
    private Map<DefaultWeightedEdge, VoronoiEdge> voronoiEdges = new HashMap<DefaultWeightedEdge, VoronoiEdge>();

    private List<Point> voronoiBorder = new ArrayList<Point>();

    private Map<Pnt, Point> delaunayVertexes = new HashMap<Pnt, Point>();

    private Map<DefaultWeightedEdge, List<DefaultWeightedEdge>> dualEdges = new HashMap<DefaultWeightedEdge, List<DefaultWeightedEdge>>();

    public VoronoiDelaunayGraph() {       
        dt = new Triangulation(initialTriangle);
        obstacles = new HashSet<Point>();
    }
    

    public void addObstacle(Point obstacle) {
        obstacles.add(obstacle);
        dt.delaunayPlace( new Pnt(obstacle.x, obstacle.y) );
    }

    public void setObstacles(Set<Point> obstacles) {
        this.obstacles = obstacles;
        dt = new Triangulation(initialTriangle);

        for (Point point : obstacles) {
            dt.delaunayPlace( new Pnt(point.x, point.y) );
        }
    }

    public Graph<Point, DefaultWeightedEdge> getVoronoiGraph(List<Point> border) {
        Graph<Point, DefaultWeightedEdge> graph = new SimpleGraph<Point, DefaultWeightedEdge>(DefaultWeightedEdge.class);

        order = 0;

        Map<Triangle, Point> vertexes = new HashMap<Triangle, Point>(); 
        
        for (Triangle triangle : dt) {
            Pnt circumcenter = triangle.getCircumcenter();
            
            Point vertex = new Point(order++, circumcenter.coord(0), circumcenter.coord(1));
            vertexes.put(triangle, vertex);
            graph.addVertex( vertex );
        }
        
        voronoiEdges.clear();
        
        for (Triangle triangle : dt) {
            for (Triangle tri: dt.neighbors(triangle)) {
                Point sourceVertex = vertexes.get(triangle);
                Point targetVertex = vertexes.get(tri);

                if (!sourceVertex.equals(targetVertex)) {
                	DefaultWeightedEdge edge = graph.addEdge(sourceVertex, targetVertex);
                    voronoiEdges.put(edge, new VoronoiEdge(triangle, tri));
                }
            }
        }       

        voronoiBorder.clear();
        clipVoronoiGraph(graph, border);
        
        // remove edges of Voronoi graph crossing any obstacle
        removeObstaclesFromGraph(graph);
        
        voronoiGraph = graph;
        return voronoiGraph;
    }

    private void removeObstaclesFromGraph(Graph<Point, DefaultWeightedEdge> graph) {
        List<DefaultWeightedEdge> toRemove = new LinkedList<DefaultWeightedEdge>();
        for (Point obstacle : obstacles) {
            for (DefaultWeightedEdge edge : graph.edgeSet()) {
                // is the edge crossing the obstacle?
                Point point = getClosestIntersection(obstacle, graph.getEdgeSource(edge), graph.getEdgeTarget(edge));
                if (point.distance(obstacle) < 0.001) {
                    toRemove.add(edge);
                }
            }
        }
        graph.removeAllEdges(toRemove);
    }

    public Graph<Point, DefaultWeightedEdge> getDelaunayGraph(List<Point> border) {
        
        if (voronoiGraph == null) {
            getVoronoiGraph(border);
        }
        
        Graph<Point, DefaultWeightedEdge> graph = new SimpleGraph<Point, DefaultWeightedEdge>(DefaultWeightedEdge.class);

        order = 0;
        delaunayVertexes.clear(); 
        for (Triangle triangle : dt) {
            for (Pnt pnt : triangle) {
                Point vertex = new Point(order++, pnt.coord(0), pnt.coord(1));
                graph.addVertex(vertex);
                delaunayVertexes.put(pnt, vertex);
            }
        }

        PlanarGraph voronoiPlanarGraph = PlanarGraph.createPlanarGraphView(voronoiGraph);

        for (DefaultWeightedEdge edge : voronoiGraph.edgeSet()) {
            List<DefaultWeightedEdge> edges = addDelaunayEdges(graph, voronoiPlanarGraph, edge);
            dualEdges.put(edge, edges);
        }

        return graph;
    }

    public void removeDualEdges(Graph<Point, DefaultWeightedEdge> graph, List<DefaultWeightedEdge> edgeList) {
        for (DefaultWeightedEdge maneuver : edgeList) {
            for (DefaultWeightedEdge edge : dualEdges.get(maneuver)) {
                graph.removeEdge(edge);
            }
        }
    }
    
    private List<DefaultWeightedEdge> addDelaunayEdges(Graph<Point, DefaultWeightedEdge> graph, PlanarGraph voronoiPlanarGraph, DefaultWeightedEdge edge) {

        List<DefaultWeightedEdge> newEdges = new ArrayList<DefaultWeightedEdge>();
        
        TemporaryEdge delaunayEdge = getDualEdgeToVoronoi( edge );

        if (USE_STRAIGHT_DELAUNAY_EDGES_ONLY && delaunayEdge != null) {
            newEdges.add( graph.addEdge(delaunayEdge.source, delaunayEdge.target) );
        } else {
            // it's a border edge
            Point center = new Point(
                    (graph.getEdgeSource(edge).x + graph.getEdgeTarget(edge).x / 2.0), 
                    (graph.getEdgeSource(edge).y + graph.getEdgeTarget(edge).y / 2.0),
                    0
                    );
            graph.addVertex(center);

            for (Point obstacle : obstacles) {
                if (voronoiPlanarGraph.countCrossingEdges(center, obstacle) <= 1) {
                    newEdges.add( graph.addEdge(center, obstacle) );
                }
            }
        }
        
        return newEdges;
    }
    
    private class TemporaryEdge {
        Point source;
        Point target;
    }
    
    private TemporaryEdge getDualEdgeToVoronoi(DefaultWeightedEdge edge) {
        VoronoiEdge voronoiEdge = voronoiEdges.get(edge);
        if (voronoiEdge == null) {
            return null;
        } else {
        
            ArrayList<Pnt> tmpCol = new ArrayList<Pnt>(voronoiEdge.source);
            tmpCol.retainAll(voronoiEdge.target);
            
            if (tmpCol.size() != 2) {
                throw new Error(voronoiEdge.source + " x " + voronoiEdge.target + " = " + tmpCol);
            }
            
            TemporaryEdge tempEdge = new TemporaryEdge();
            tempEdge.source = delaunayVertexes.get(tmpCol.get(0));
            tempEdge.target = delaunayVertexes.get(tmpCol.get(1));         
    
            return tempEdge;
        }
    }


    private void clipVoronoiGraph(Graph<Point, DefaultWeightedEdge> graph, List<Point> border) {
        PlanarGraph planarGraph = PlanarGraph.createPlanarGraphView(graph);
        
        Point last = null;
        for (Point vertex : border) {
            if (last != null) {
                voronoiBorder.addAll(
                    planarGraph.addLine(last, vertex, voronoiEdges)
                    );
            }
            last = vertex;
        }
        voronoiBorder.addAll(
                planarGraph.addLine(last, border.get(0), voronoiEdges)
                );
            
        removeOutsideVertices(graph, border);
    }

    private static <E> void removeOutsideVertices(Graph<Point, E> graph,
            List<Point> border) {
        
        List<Point> toRemove = getOutsideVertices(graph, border);
        graph.removeAllVertices(toRemove);
    }

    private static <E> List<Point> getOutsideVertices(
            Graph<Point, E> graph, List<Point> border) {
        Point center = new Point();
        for (Point point : border) {
            center.x += point.x;
            center.y += point.y;
        }
        
        center.x /= border.size();
        center.y /= border.size();
        

        List<Point> toRemove = new ArrayList<Point>();
        for (Point vertex : graph.vertexSet()) {
            Point intersection = getBorderIntersection(center, vertex, border);
            if (intersection != null) {
                if (!intersection.epsilonEquals(vertex, 0.01)) {
                    toRemove.add( vertex );
                }
            }
        }
        return toRemove;
    }
    
    private static Point getBorderIntersection(Point point1, Point point2, List<Point> border) {
        Point last = null;
        for (Point vertex : border) {
            if (last != null) {
                Point intersection = getIntersection(point1, point2, last, vertex);
                if (intersection != null && !intersection.epsilonEquals(point1, 0.001) && !intersection.epsilonEquals(point2, 0.001)) {
                    return intersection;
                }
            }
            last = vertex;
        }
        return getIntersection(point1, point2, last, border.get(0));
    }

    private static Point getClosestIntersection(Point point, Point point1, Point point2) {
        double u = ((point.x - point1.x) * (point2.x - point1.x) + (point.y - point1.y) * (point2.y - point1.y)) / point1.distanceSquared(point2);
        if (u < 0) {
            return point1;
        } else if (u > 1) {
            return point2;
        } else {
            double x = point1.x + u * ( point2.x - point1.x );
            double y = point1.y + u * ( point2.y - point1.y );

            return new Point(x, y, 0);
        }
    }

    /**
     * intersection in 2D
     * 
     * @param point1
     * @param point2
     * @param point3
     * @param point4
     * @return
     */
    private static Point getIntersection(Point point1, Point point2, Point point3, Point point4){
        double a1, a2, b1, b2, c1, c2;
        double r1, r2 , r3, r4;
        double denom;

        // Compute a1, b1, c1, where line joining points 1 and 2
        // is "a1 x + b1 y + c1 = 0".
        a1 = point2.y - point1.y;
        b1 = point1.x - point2.x;
        c1 = (point2.x * point1.y) - (point1.x * point2.y);

        // Compute r3 and r4.
        r3 = ((a1 * point3.x) + (b1 * point3.y) + c1);
        r4 = ((a1 * point4.x) + (b1 * point4.y) + c1);

        // Check signs of r3 and r4. If both point 3 and point 4 lie on
        // same side of line 1, the line segments do not intersect.
        if ((r3 != 0) && (r4 != 0) && same_sign(r3, r4)){
          return null;
        }

        // Compute a2, b2, c2
        a2 = point4.y - point3.y;
        b2 = point3.x - point4.x;
        c2 = (point4.x * point3.y) - (point3.x * point4.y);

        // Compute r1 and r2
        r1 = (a2 * point1.x) + (b2 * point1.y) + c2;
        r2 = (a2 * point2.x) + (b2 * point2.y) + c2;

        // Check signs of r1 and r2. If both point 1 and point 2 lie
        // on same side of second line segment, the line segments do
        // not intersect.
        if ((r1 != 0) && (r2 != 0) && (same_sign(r1, r2))){
          return null;
        }

        //Line segments intersect: compute intersection point.
        denom = (a1 * b2) - (a2 * b1);

        if (denom == 0) {
          return null;
        }

        return new Point(order++, ((b1 * c2) - (b2 * c1)) / denom, ((a2 * c1) - (a1 * c2)) / denom);
    }

    private static boolean same_sign(double a, double b){
        return (( a * b) >= 0);
    }
    
    private static class VoronoiEdge {
        Triangle source;
        Triangle target;

        public VoronoiEdge(Triangle source, Triangle target) {
            this.source = source;
            this.target = target;
        }
    }
}
