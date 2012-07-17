package cz.agents.alite.trajectorytools.alterantiveplanners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jgrapht.alg.AllPathsIterator;

import cz.agents.alite.trajectorytools.graph.maneuver.Maneuver;
import cz.agents.alite.trajectorytools.graph.maneuver.ManeuverGraphWithObstacles;
import cz.agents.alite.trajectorytools.graph.maneuver.PlanarGraph;
import cz.agents.alite.trajectorytools.graph.maneuver.VoronoiDelaunayGraph;
import cz.agents.alite.trajectorytools.graph.spatialwaypoint.SpatialWaypoint;
import cz.agents.alite.trajectorytools.planner.AStarPlanner;
import cz.agents.alite.trajectorytools.planner.PlannedPath;
import cz.agents.alite.trajectorytools.util.Point;
import cz.agents.alite.trajectorytools.vis.GraphHolder;

public class VoronoiDelaunayPlanner implements AlternativePathPlanner {
    private static int MAX_WORLD_SIZE = 10000;

    VoronoiDelaunayGraph voronoiGraphAlg = new VoronoiDelaunayGraph();
    private final AStarPlanner<SpatialWaypoint, Maneuver> planner;

    public VoronoiDelaunayPlanner( AStarPlanner<SpatialWaypoint, Maneuver> planner ) {
        this.planner = planner;
    }

    @Override
    public Collection<PlannedPath<SpatialWaypoint, Maneuver>> planPath(
            ManeuverGraphWithObstacles graph, SpatialWaypoint startVertex,
            SpatialWaypoint endVertex) {

        List<SpatialWaypoint> border = Arrays.asList(new SpatialWaypoint[] {
                graph.getNearestWaypoint(new Point( -MAX_WORLD_SIZE,  -MAX_WORLD_SIZE, 0)),
                graph.getNearestWaypoint(new Point( MAX_WORLD_SIZE,  -MAX_WORLD_SIZE, 0)),
                graph.getNearestWaypoint(new Point( MAX_WORLD_SIZE,  MAX_WORLD_SIZE, 0)),
                graph.getNearestWaypoint(new Point( -MAX_WORLD_SIZE,  MAX_WORLD_SIZE, 0))
        });

        PlanarGraph<Maneuver> planarGraph = new PlanarGraph<Maneuver>(graph);

        GraphHolder<SpatialWaypoint, Maneuver> voronoiGraph = new GraphHolder<SpatialWaypoint, Maneuver>();
        GraphHolder<SpatialWaypoint, Maneuver> delaunayGraph = new GraphHolder<SpatialWaypoint, Maneuver>();
        
        voronoiGraphAlg.setObstacles(graph.getObstacles());
        
        voronoiGraph.graph = voronoiGraphAlg.getVoronoiGraph(border);
        delaunayGraph.graph = voronoiGraphAlg.getDelaunayGraph(border);

        List<PlannedPath<SpatialWaypoint, Maneuver>> paths = new ArrayList<PlannedPath<SpatialWaypoint,Maneuver>>();

        AllPathsIterator<SpatialWaypoint, Maneuver> pathsIt = new AllPathsIterator<SpatialWaypoint, Maneuver>(voronoiGraph.graph,
                startVertex,
                endVertex
                );

        while (pathsIt.hasNext()) {

            PlannedPath<SpatialWaypoint, Maneuver> planPath = pathsIt.next();

            delaunayGraph.graph = voronoiGraphAlg.getDelaunayGraph(border);
            
            voronoiGraphAlg.removeDualEdges(delaunayGraph.graph, planPath.getEdgeList());
            
//            System.out.println("delaunayGraph.graph.vertexSet(): " + delaunayGraph.graph.vertexSet());

//            PlanarGraph<Maneuver> planarGraphDelaunay = new PlanarGraph<Maneuver>(delaunayGraph.graph);
//
//            delaunayGraph.graph.removeVertex(startVertex);
//            delaunayGraph.graph.removeVertex(targetVertex);
//            
//            for (Maneuver voronoiEdge: planPath.getEdgeList()) {
//                planarGraphDelaunay.removeCrossingEdges(voronoiEdge.getSource(), voronoiEdge.getTarget());
//            }
//    
//            delaunayGraph.graph = planarGraphDelaunay;
        
            graph.refresh();
        
            for (Maneuver edge : delaunayGraph.graph.edgeSet()) {
                planarGraph.removeCrossingEdges(edge.getSource(), edge.getTarget());
            }
    
            planPath = planner.planPath(graph,
                    startVertex,
                    endVertex
                    );

            if ( planPath != null ) {
                if ( !paths.contains(planPath) ) {
                    paths.add(planPath);
                }
            }
        }
        
        return paths;
    }

    @Override
    public String getName() {
        return "Voronoi-Delaunay";
    }

}
