package cz.agents.alite.trajectorytools.demo;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jgrapht.alg.AllPathsIterator;
import org.jgrapht.graph.DefaultWeightedEdge;

import cz.agents.alite.creator.Creator;
import cz.agents.alite.trajectorytools.graph.ObstacleGraphView;
import cz.agents.alite.trajectorytools.graph.PlanarGraph;
import cz.agents.alite.trajectorytools.graph.VoronoiDelaunayGraph;
import cz.agents.alite.trajectorytools.graph.ObstacleGraphView.ChangeListener;
import cz.agents.alite.trajectorytools.graph.spatial.SpatialGridFactory;
import cz.agents.alite.trajectorytools.graph.spatial.SpatialManeuverGraph;
import cz.agents.alite.trajectorytools.graph.spatial.SpatialGraphs;
import cz.agents.alite.trajectorytools.graph.spatial.SpatialWaypoint;
import cz.agents.alite.trajectorytools.graph.spatial.maneuvers.SpatialManeuver;
import cz.agents.alite.trajectorytools.planner.AStarPlanner;
import cz.agents.alite.trajectorytools.planner.HeuristicFunction;
import cz.agents.alite.trajectorytools.planner.PlannedPath;
import cz.agents.alite.trajectorytools.util.Point;
import cz.agents.alite.trajectorytools.vis.GraphHolder;
import cz.agents.alite.trajectorytools.vis.GraphLayer;
import cz.agents.alite.trajectorytools.vis.GraphPathLayer;
import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;

public class DemoAlternative2Creator implements Creator {

    // shows one trajectory with voronoi and delaunay graphs
    private static final boolean DEBUG_VIEW = true;

    private ObstacleGraphView<SpatialManeuver> graph;
    private List<PlannedPath<SpatialWaypoint, DefaultWeightedEdge>> paths = new ArrayList<PlannedPath<SpatialWaypoint, DefaultWeightedEdge>>();
    private List<PlannedPath<SpatialWaypoint, SpatialManeuver>> maneuverPaths = new ArrayList<PlannedPath<SpatialWaypoint, SpatialManeuver>>();

    
    VoronoiDelaunayGraph voronoiGraphAlg = new VoronoiDelaunayGraph();
    GraphHolder<SpatialWaypoint, DefaultWeightedEdge> voronoiGraph = new GraphHolder<SpatialWaypoint, DefaultWeightedEdge>();
    GraphHolder<SpatialWaypoint, DefaultWeightedEdge> delaunayGraph = new GraphHolder<SpatialWaypoint, DefaultWeightedEdge>();
    GraphHolder<SpatialWaypoint, DefaultWeightedEdge> otherGraph = new GraphHolder<SpatialWaypoint, DefaultWeightedEdge>();
    
    private static final AStarPlanner<SpatialWaypoint, DefaultWeightedEdge> planner = new AStarPlanner<SpatialWaypoint, DefaultWeightedEdge>();
    {
        planner.setHeuristicFunction(new HeuristicFunction<SpatialWaypoint>() {
        @Override
            public double getHeuristicEstimate(SpatialWaypoint current, SpatialWaypoint goal) {
                return current.distance(goal) + ( current.x > current.y ? 0.1 : -0.1 );
            }
        });
    }
    
    private static final AStarPlanner<SpatialWaypoint, SpatialManeuver> maneuverPlanner = new AStarPlanner<SpatialWaypoint, SpatialManeuver>();
    {
        planner.setHeuristicFunction(new HeuristicFunction<SpatialWaypoint>() {
        @Override
            public double getHeuristicEstimate(SpatialWaypoint current, SpatialWaypoint goal) {
                return current.distance(goal) + ( current.x > current.y ? 0.1 : -0.1 );
            }
        });
    }

//    private static final ObstacleExtensions alternativePlanner = new ObstacleExtensions(planner);
    private List<SpatialWaypoint> border;

//    private static final AlternativePathPlanner<SpatialWaypoint, SpatialManeuver> alternativePlanner = new TrajectoryDistanceMetric<SpatialWaypoint, SpatialManeuver>( planner );
//    private static final AlternativePathPlanner<SpatialWaypoint, SpatialManeuver> alternativePlanner = new DifferentStateMetric<SpatialWaypoint, SpatialManeuver>( planner );


    @Override
    public void init(String[] args) {
    }

    @Override
    public void create() {
        Graph<SpatialWaypoint, SpatialManeuver> originalGraph = SpatialGridFactory.create4WayGrid(10, 10, 10, 10, 1.0); 

        border = Arrays.asList(new SpatialWaypoint[] {
        		SpatialGraphs.getNearestWaypoint(originalGraph, new Point( 0,  0, 0)),
                SpatialGraphs.getNearestWaypoint(originalGraph, new Point( 0, 10, 0)),
                SpatialGraphs.getNearestWaypoint(originalGraph, new Point(10, 10, 0)),
                SpatialGraphs.getNearestWaypoint(originalGraph, new Point(10,  0, 0))
        });

        
        graph = new ObstacleGraphView<SpatialManeuver>( originalGraph, new ChangeListener() {
            @Override
            public void graphChanged() {
                replan();
            }
        } );
               
        createVisualization();
    }

    private void createVisualization() {
        VisManager.setInitParam("Trajectory Tools Vis", 1024, 768, 20, 20);
        VisManager.setPanningBounds(new Rectangle(-500, -500, 1600, 1600));
        VisManager.init();
        
        Vis.setPosition(50, 50, 1);

        // background
        VisManager.registerLayer(ColorLayer.create(Color.WHITE));

        // graph with obstacles
        graph.createVisualization();
        
        if (DEBUG_VIEW) {
            VisManager.registerLayer(GraphLayer.create(voronoiGraph, Color.GREEN, Color.GREEN, 1, 4));
            VisManager.registerLayer(GraphLayer.create(otherGraph, Color.MAGENTA, Color.MAGENTA, 1, 4, 0.02));
            VisManager.registerLayer(GraphLayer.create(delaunayGraph, Color.BLUE, Color.BLUE, 1, 4, 0.04));
        }
            
        // draw the shortest path
        VisManager.registerLayer(GraphPathLayer.create(voronoiGraph, paths, 2, 4));
        
        // draw the shortest path
        VisManager.registerLayer(GraphPathLayer.create(graph, maneuverPaths, 2, 4));

        // Overlay
        VisManager.registerLayer(VisInfoLayer.create());
    }

	protected void replan() {
	    voronoiGraphAlg.setObstacles(graph.getObstacles());
	    
	    delaunayGraph.graph = null;
        voronoiGraph.graph = voronoiGraphAlg.getVoronoiGraph(border);
        delaunayGraph.graph = voronoiGraphAlg.getDelaunayGraph(border);

        otherGraph.graph = voronoiGraphAlg.getDelaunayGraph(border);


        paths.clear();
        
        SpatialWaypoint startVertex = SpatialGraphs.getNearestWaypoint(graph, new Point(0, 0, 0));
        SpatialWaypoint targetVertex =  SpatialGraphs.getNearestWaypoint(graph, new Point(10, 10, 0));

        AllPathsIterator<SpatialWaypoint, DefaultWeightedEdge> pathsIt 
        	= new AllPathsIterator<SpatialWaypoint, DefaultWeightedEdge>(voronoiGraph.graph,
                startVertex,
                targetVertex
                );

        while (pathsIt.hasNext()) {
            if (DEBUG_VIEW) {
                if (pathsIt.hasNext()) pathsIt.next();
                if (pathsIt.hasNext()) pathsIt.next();
                if (pathsIt.hasNext()) pathsIt.next();
                if (!pathsIt.hasNext()) {
                    break;
                }
            }
            
            PlannedPath<SpatialWaypoint, DefaultWeightedEdge> planPath = pathsIt.next();
            if (DEBUG_VIEW) {
                paths.add(planPath);
            }

            delaunayGraph.graph = voronoiGraphAlg.getDelaunayGraph(border);
            
            voronoiGraphAlg.removeDualEdges(delaunayGraph.graph, planPath.getEdgeList());

            PlanarGraph<DefaultWeightedEdge> planarGraphDelaunay = new PlanarGraph<DefaultWeightedEdge>(delaunayGraph.graph);
//
//            delaunayGraph.graph.removeVertex(startVertex);
//            delaunayGraph.graph.removeVertex(targetVertex);
//            
            for (DefaultWeightedEdge voronoiEdge: planPath.getEdgeList()) {
                planarGraphDelaunay.removeCrossingEdges(planarGraphDelaunay.getEdgeSource(voronoiEdge),planarGraphDelaunay.getEdgeTarget(voronoiEdge));
            }
//    
            delaunayGraph.graph = planarGraphDelaunay;
        
            graph.refresh();
        
            for (DefaultWeightedEdge edge : delaunayGraph.graph.edgeSet()) {
                graph.removeCrossingEdges(delaunayGraph.graph.getEdgeSource(edge), delaunayGraph.graph.getEdgeTarget(edge));
            }
    
            PlannedPath<SpatialWaypoint, SpatialManeuver> result = maneuverPlanner.planPath(graph,
                    startVertex,
                    targetVertex
                    );
            
            if (result != null) {
            	maneuverPaths.add(result);
            }
            

            if ( planPath!= null ) {
//                if ( !paths.contains(planPath) ) {
                    paths.add(planPath);
//                } else {
//                    System.out.println("Path already found: " + planPath);
//                }
            }
            if (DEBUG_VIEW) {
                break;
            }
        }
        
        System.out.println("paths.size(): " + paths.size());
	}
}
