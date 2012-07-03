package cz.agents.alite.trajectorytools.graph.maneuver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;

import cz.agents.alite.trajectorytools.graph.spatialwaypoint.SpatialWaypoint;
import cz.agents.alite.trajectorytools.util.Point;
import delaunay.Pnt;
import delaunay.Triangle;
import delaunay.Triangulation;

public class VoronoiDelaunayGraph {
    
    private static int INITIAL_SIZE = 10000;     // Size of initial triangle

    private Triangle initialTriangle = new Triangle(
            new Pnt(-INITIAL_SIZE, -INITIAL_SIZE),
            new Pnt( INITIAL_SIZE, -INITIAL_SIZE),
            new Pnt(           0,  INITIAL_SIZE));           // Initial triangle
    private Triangulation dt;                   // Delaunay triangulation

    private static int order = 1000;


    public VoronoiDelaunayGraph() {       
    }
    
    public void setObstacles(Set<Point> obstacles) {
        dt = new Triangulation(initialTriangle);

        for (Point point : obstacles) {
            dt.delaunayPlace( new Pnt(point.x, point.y) );
        }
    }

    public Graph<SpatialWaypoint, Maneuver> getVoronoiGraph(List<SpatialWaypoint> border) {
        Graph<SpatialWaypoint, Maneuver> graph = new SimpleGraph<SpatialWaypoint, Maneuver>(new ManeuverEdgeFactory(1.0, 1.0));

        order = 0;

        Map<Triangle, SpatialWaypoint> vertexes = new HashMap<Triangle, SpatialWaypoint>(); 
        
        for (Triangle triangle : dt) {
            Pnt circumcenter = triangle.getCircumcenter();
            
            // is vertex inside the border?
            SpatialWaypoint vertex = new SpatialWaypoint(order++, circumcenter.coord(0), circumcenter.coord(1));
            vertexes.put(triangle, vertex);
            graph.addVertex( vertex );
        }
        
        for (Triangle triangle : dt) {
            for (Triangle tri: dt.neighbors(triangle)) {
                SpatialWaypoint sourceVertex = vertexes.get(triangle);
                SpatialWaypoint targetVertex = vertexes.get(tri);

                graph.addEdge(sourceVertex, targetVertex);
            }
        }       

        clipVoronoiGraph(graph, border);
        
        return graph;
    }

    public Graph<SpatialWaypoint, Maneuver> getDelaunayGraph(List<SpatialWaypoint> border) {
        Graph<SpatialWaypoint, Maneuver> graph = new SimpleGraph<SpatialWaypoint, Maneuver>(new ManeuverEdgeFactory(1.0, 1.0));

        order = 0;
        Map<Pnt, SpatialWaypoint> vertexes = new HashMap<Pnt, SpatialWaypoint>(); 
        for (Triangle triangle : dt) {
            for (Pnt pnt : triangle) {
                SpatialWaypoint vertex = new SpatialWaypoint(order++, pnt.coord(0), pnt.coord(1));
                graph.addVertex(vertex);
                vertexes.put(pnt, vertex);
            }
        }
        for (Triangle triangle : dt) {
            SpatialWaypoint v0 = vertexes.get(triangle.get(0));
            SpatialWaypoint v1 = vertexes.get(triangle.get(1));
            SpatialWaypoint v2 = vertexes.get(triangle.get(2));
            
            graph.addEdge(v0, v1);
            graph.addEdge(v1, v2);
            graph.addEdge(v2, v0);
        }
        clipDelaunayGraph(graph, border);

        return graph;
    }

    static public void clipDelaunayGraph(Graph<SpatialWaypoint, Maneuver> graph, List<SpatialWaypoint> originalBorder) {
        List<SpatialWaypoint> border = new LinkedList<SpatialWaypoint>(originalBorder);
        List<SpatialWaypoint> outsideVertices = getOutsideVertices(graph, border);
        
        Set<SpatialWaypoint> insideVertices = new HashSet<SpatialWaypoint>();        
        for (SpatialWaypoint outsideVertex : outsideVertices) {
            for (Maneuver edge : graph.edgesOf(outsideVertex)) {
                if (edge.source.equals(outsideVertex)) {
                    if (!outsideVertices.contains(edge.target)) {
                        insideVertices.add(edge.target);
                    }
                } else if (edge.target.equals(outsideVertex)) {
                    if (!outsideVertices.contains(edge.source)) {
                        insideVertices.add(edge.source);
                    }
                } else {
                    throw new Error("Should not happen!!!");
                }
            }
        }

        for (SpatialWaypoint insideVertex : insideVertices) {
            // find the closest border line, split it and add an edge to it
            System.out.println("insideVertex: " + insideVertex);
            SpatialWaypoint intersection = addClosestBorderIntersection(insideVertex, border);
            System.out.println("intersection: " + intersection);
            if (!graph.containsVertex(intersection)) {
                graph.addVertex(intersection);
            }
            graph.addEdge(insideVertex, intersection);
        }

        graph.removeAllVertices(outsideVertices);

        System.out.println("border: " + border);
        
        addBorderToGraph(graph, border);
    }

    static public <E> void clipVoronoiGraph(Graph<SpatialWaypoint, E> graph, List<SpatialWaypoint> border) {
        Graph<SpatialWaypoint, E> planarGraph = new PlanarGraph<E>(graph);
        
        SpatialWaypoint last = null;
        for (SpatialWaypoint vertex : border) {
            if (last != null) {
                planarGraph.addEdge(last, vertex);
            }
            last = vertex;
        }
        planarGraph.addEdge(last, border.get(0));
            
//        List<SpatialWaypoint> border = new LinkedList<SpatialWaypoint>(originalBorder);
////        List<SpatialWaypoint> border = new LinkedList<SpatialWaypoint>(Arrays.asList(borderPoints));
//        
//        order  = 10000;
//        
//        //
//        // Firstly find intersections of edges with the border,
//        // add there new points and split the edges. Then remove
//        // all the vertexes outside the border.
//        //
//        // Note, that one edge can cross the border several times 
//        // (max. twice for convex borders, which we assume).  
//        //
//        
//        List<Maneuver> toRemove = new ArrayList<Maneuver>();
//        List<Maneuver> toAdd = new ArrayList<Maneuver>();
//        for (Maneuver edge : graph.edgeSet()) {
//            
//            Point intersectionPoint = addBorderIntersection(edge.getSource(), edge.getTarget(), border);
//            if (intersectionPoint != null) {
//                toRemove.add(edge);
//                
//                SpatialWaypoint intersection = new SpatialWaypoint(order ++, intersectionPoint.x, intersectionPoint.y);               
//                graph.addVertex(intersection);
//
//                Point intersectionPoint2 = addBorderIntersection(edge.source, intersection, border);
//                if (intersectionPoint2 != null) {
//                    SpatialWaypoint intersection2 = new SpatialWaypoint(order ++, intersectionPoint2.x, intersectionPoint2.y);
//                    graph.addVertex(intersection2);
//                    toAdd.add( graph.getEdgeFactory().createEdge(edge.source, intersection2) );
//                    toAdd.add( graph.getEdgeFactory().createEdge(intersection2, intersection) );
//                } else {
//                    toAdd.add( graph.getEdgeFactory().createEdge(edge.source, intersection) );
//                }
//
//                intersectionPoint2 = addBorderIntersection(intersection, edge.target, border);
//                if (intersectionPoint2 != null) {
//                    SpatialWaypoint intersection2 = new SpatialWaypoint(order ++, intersectionPoint2.x, intersectionPoint2.y);
//                    graph.addVertex(intersection2);
//                    toAdd.add( graph.getEdgeFactory().createEdge(intersection, intersection2) );
//                    toAdd.add( graph.getEdgeFactory().createEdge(intersection2, edge.target) );
//                } else {
//                    toAdd.add( graph.getEdgeFactory().createEdge(intersection, edge.target) );
//                }
//            }
//        }
//        graph.removeAllEdges(toRemove);
//        for (Maneuver maneuver : toAdd) {
//            graph.addEdge(maneuver.source, maneuver.target);
//        }
//System.out.println("borderV: " + border);
//        addBorderToGraph(graph, border);

        removeOutsideVertices(graph, border);
    }

    private static void addBorderToGraph(Graph<SpatialWaypoint, Maneuver> graph,
            List<SpatialWaypoint> border) {
        SpatialWaypoint last = null;
        for (SpatialWaypoint vertex : border) {
            if (!graph.containsVertex(vertex)) {
                graph.addVertex(vertex);
            } 
            if (last != null) {
                graph.addEdge(last, vertex);
            }
            last = vertex;
        }
        graph.addEdge(last, border.get(0));
    }

    private static <E> void removeOutsideVertices(Graph<SpatialWaypoint, E> graph,
            List<SpatialWaypoint> border) {
        
        List<SpatialWaypoint> toRemove = getOutsideVertices(graph, border);
        graph.removeAllVertices(toRemove);
    }

    private static <E> List<SpatialWaypoint> getOutsideVertices(
            Graph<SpatialWaypoint, E> graph, List<SpatialWaypoint> border) {
        Point center = new Point();
        for (SpatialWaypoint point : border) {
            center.x += point.x;
            center.y += point.y;
        }
        
        center.x /= border.size();
        center.y /= border.size();
        

        List<SpatialWaypoint> toRemove = new ArrayList<SpatialWaypoint>();
        for (SpatialWaypoint vertex : graph.vertexSet()) {
            Point intersection = getBorderIntersection(center, vertex, border);
            if (intersection != null) {
                if (!intersection.epsilonEquals(vertex, 0.01)) {
                    toRemove.add( vertex );
                }
            }
        }
        return toRemove;
    }
    
    static Point getBorderIntersection(Point point1, Point point2, List<SpatialWaypoint> border) {
        Point last = null;
        for (Point vertex : border) {
            if (last != null) {
                Point intersection = getIntersection(point1, point2, last, vertex);
                if (intersection != null && !intersection.epsilonEquals(point1, 0.001) && !intersection.epsilonEquals(point2, 0.001)) {
//                    System.out.println(point1 + ", " + point2 + " x " + last + ", " + vertex);
//                    System.out.println("intersection: " + intersection);
                    return intersection;
                }
            }
            last = vertex;
        }
        return getIntersection(point1, point2, last, border.get(0));
    }

    static Point addBorderIntersection(Point point1, Point point2, List<SpatialWaypoint> border) {
        Point last = null;
        int index = 0;
        for (Point vertex : border) {
            if (last != null) {
                SpatialWaypoint intersection = getIntersection(point1, point2, last, vertex);
                if (intersection != null && !intersection.epsilonEquals(point1, 0.001) && !intersection.epsilonEquals(point2, 0.001)) {
//                    System.out.println(point1 + ", " + point2 + " x " + last + ", " + vertex);
//                    System.out.println("intersection: " + intersection);
                    border.add(index, intersection);
                    return intersection;
                }
            }
            last = vertex;
            index++;
        }
        SpatialWaypoint intersection = getIntersection(point1, point2, last, border.get(0));
        if (intersection != null && !intersection.epsilonEquals(point1, 0.001) && !intersection.epsilonEquals(point2, 0.001)) {
            return intersection;
        } else {
            return null;
        }
    }

    static SpatialWaypoint addClosestBorderIntersection(SpatialWaypoint point, List<SpatialWaypoint> border) {
        SpatialWaypoint last = null;
        int index = 0;
        SpatialWaypoint minPoint = null;
        double minDist = Double.MAX_VALUE;
        int minIndex = 0;
        boolean addMin = false;
        for (SpatialWaypoint vertex : border) {
            if (last != null) {
                SpatialWaypoint intersection = getClosestIntersection(point, last, vertex);
                double dist = intersection.distance(point);

                if (dist < minDist) {
                    minDist = dist;
                    minIndex = index;
                    minPoint = intersection;

                    addMin = !intersection.equals(last) && !intersection.equals(vertex);
                }
            }
            last = vertex;
            index++;
        }

        SpatialWaypoint intersection = getClosestIntersection(point, last, border.get(0));
        double dist = intersection.distance(point);
        if (dist < minDist) {
            minDist = dist;
            minPoint = intersection;
            minIndex = index;
            addMin = !intersection.equals(last) && !intersection.equals(border.get(0));
        }
        
        if (addMin) {
            border.add(minIndex, minPoint);
        }
        return minPoint;
    }

    private static SpatialWaypoint getClosestIntersection(SpatialWaypoint point, SpatialWaypoint point1, SpatialWaypoint point2) {
        double u = ((point.x - point1.x) * (point2.x - point1.x) + (point.y - point1.y) * (point2.y - point1.y)) / point1.distanceSquared(point2);
        System.out.println("u: " + u);
        if (u < 0) {
            return point1;
        } else if (u > 1) {
            return point2;
        } else {
            double x = point1.x + u * ( point2.x - point1.x );
            double y = point1.y + u * ( point2.y - point1.y );

            return new SpatialWaypoint(order++, x, y);
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
    static SpatialWaypoint getIntersection(Point point1, Point point2, Point point3, Point point4){
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

        return new SpatialWaypoint(order++, ((b1 * c2) - (b2 * c1)) / denom, ((a2 * c1) - (a1 * c2)) / denom);
      }

      static boolean same_sign(double a, double b){
        return (( a * b) >= 0);
      }
      
      public static void main(String[] args) {
        System.out.println(getIntersection(
                new Point(0, 0, 0),
                new Point(0, 10, 0),
                new Point(0, 0, 0),
                new Point(0, 10, 0)
                ));
    }
      
}
