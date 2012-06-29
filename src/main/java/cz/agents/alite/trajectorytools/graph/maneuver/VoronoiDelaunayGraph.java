package cz.agents.alite.trajectorytools.graph.maneuver;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
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


    public VoronoiDelaunayGraph() {       
    }
    
    public void setObstacles(Set<Point> obstacles) {
        System.out.println("vor- init");
        dt = new Triangulation(initialTriangle);

        for (Point point : obstacles) {
            dt.delaunayPlace( new Pnt(point.x, point.y) );
        }
    }

    public Graph<SpatialWaypoint, DefaultWeightedEdge> getVoronoiGraph() {
        Graph<SpatialWaypoint, DefaultWeightedEdge> graph = new SimpleGraph<SpatialWaypoint, DefaultWeightedEdge>(DefaultWeightedEdge.class);

        Map<Triangle, SpatialWaypoint> vertexes = new HashMap<Triangle, SpatialWaypoint>(); 
        int order = 0;
        
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

        return graph;
    }

    public Graph<SpatialWaypoint, DefaultWeightedEdge> getDelaunayGraph() {
        Graph<SpatialWaypoint, DefaultWeightedEdge> graph = new SimpleGraph<SpatialWaypoint, DefaultWeightedEdge>(DefaultWeightedEdge.class);

        int order = 0;
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
        return graph;
    }
}
