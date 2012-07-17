package cz.agents.alite.trajectorytools.alterantiveplanners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jgrapht.alg.AllPathsIterator;

import cz.agents.alite.trajectorytools.graph.PlanarGraph;
import cz.agents.alite.trajectorytools.graph.VoronoiDelaunayGraph;
import cz.agents.alite.trajectorytools.graph.spatial.GraphWithObstacles;
import cz.agents.alite.trajectorytools.graph.spatial.SpatialGraphs;
import cz.agents.alite.trajectorytools.planner.AStarPlanner;
import cz.agents.alite.trajectorytools.planner.PlannedPath;
import cz.agents.alite.trajectorytools.util.Point;
import cz.agents.alite.trajectorytools.vis.GraphHolder;

public class VoronoiDelaunayPlanner<V extends Point, E> implements AlternativePathPlanner<V, E> {
    private static int MAX_WORLD_SIZE = 10000;

    private final AStarPlanner<V, E> planner;

    public VoronoiDelaunayPlanner( AStarPlanner<V, E> planner ) {
        this.planner = planner;
    }

    @Override
    public Collection<PlannedPath<V, E>> planPath(
            GraphWithObstacles<V,E> graph, V startVertex,
            V endVertex) {

        VoronoiDelaunayGraph voronoiGraphAlg = new VoronoiDelaunayGraph();

        List<Point> border = Arrays.asList(new Point[] {
                SpatialGraphs.getNearestVertex(graph, new Point( -MAX_WORLD_SIZE,  -MAX_WORLD_SIZE, 0)),
                SpatialGraphs.getNearestVertex(graph,new Point( MAX_WORLD_SIZE,  -MAX_WORLD_SIZE, 0)),
                SpatialGraphs.getNearestVertex(graph,new Point( MAX_WORLD_SIZE,  MAX_WORLD_SIZE, 0)),
                SpatialGraphs.getNearestVertex(graph,new Point( -MAX_WORLD_SIZE,  MAX_WORLD_SIZE, 0))
        });

        PlanarGraph<V,E> planarGraph = new PlanarGraph<V,E>(graph);

        GraphHolder<V, E> voronoiGraph = new GraphHolder<V, E>();
        GraphHolder<V, E> delaunayGraph = new GraphHolder<V, E>();

        voronoiGraphAlg.setObstacles(graph.getObstacles());

        voronoiGraph.graph = voronoiGraphAlg.getVoronoiGraph(border);
        delaunayGraph.graph = voronoiGraphAlg.getDelaunayGraph(border);

        List<PlannedPath<V, E>> paths = new ArrayList<PlannedPath<V,E>>();

        AllPathsIterator<V, E> pathsIt = new AllPathsIterator<V, E>(voronoiGraph.graph,
                startVertex,
                endVertex
                );

        while (pathsIt.hasNext()) {

            PlannedPath<V, E> planPath = pathsIt.next();

            delaunayGraph.graph = voronoiGraphAlg.getDelaunayGraph(border);

            voronoiGraphAlg.removeDualEdges(delaunayGraph.graph, planPath.getEdgeList());

//            System.out.println("delaunayGraph.graph.vertexSet(): " + delaunayGraph.graph.vertexSet());

//            PlanarGraph<E> planarGraphDelaunay = new PlanarGraph<E>(delaunayGraph.graph);
//
//            delaunayGraph.graph.removeVertex(startVertex);
//            delaunayGraph.graph.removeVertex(targetVertex);
//
//            for (E voronoiEdge: planPath.getEdgeList()) {
//                planarGraphDelaunay.removeCrossingEdges(voronoiEdge.getSource(), voronoiEdge.getTarget());
//            }
//
//            delaunayGraph.graph = planarGraphDelaunay;

            graph.refresh();

            for (E edge : delaunayGraph.graph.edgeSet()) {
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
