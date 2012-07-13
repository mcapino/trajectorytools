package cz.agents.alite.trajectorytools.demo;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jgrapht.alg.AllPathsIterator;

import cz.agents.alite.creator.Creator;
import cz.agents.alite.trajectorytools.graph.maneuver.FourWayConstantSpeedGridGraph;
import cz.agents.alite.trajectorytools.graph.maneuver.Maneuver;
import cz.agents.alite.trajectorytools.graph.maneuver.ManeuverGraphInterface;
import cz.agents.alite.trajectorytools.graph.maneuver.ObstacleGraphView;
import cz.agents.alite.trajectorytools.graph.maneuver.ObstacleGraphView.ChangeListener;
import cz.agents.alite.trajectorytools.graph.maneuver.PlanarGraph;
import cz.agents.alite.trajectorytools.graph.maneuver.VoronoiDelaunayGraph;
import cz.agents.alite.trajectorytools.graph.spatialwaypoint.SpatialWaypoint;
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

    private static final int NUM_OF_RANDOM_OBSTACLES = 8;

    private static final int WORLD_SIZE = 10;

    // shows one trajectory with voronoi and delaunay graphs
    private static final boolean DEBUG_VIEW = false;

    private ObstacleGraphView graph;
    private List<PlannedPath<SpatialWaypoint, Maneuver>> paths = new ArrayList<PlannedPath<SpatialWaypoint,Maneuver>>();

    VoronoiDelaunayGraph voronoiGraphAlg = new VoronoiDelaunayGraph();
    GraphHolder<SpatialWaypoint, Maneuver> voronoiGraph = new GraphHolder<SpatialWaypoint, Maneuver>();
    GraphHolder<SpatialWaypoint, Maneuver> delaunayGraph = new GraphHolder<SpatialWaypoint, Maneuver>();
    GraphHolder<SpatialWaypoint, Maneuver> otherGraph = new GraphHolder<SpatialWaypoint, Maneuver>();
    
    private static final AStarPlanner<SpatialWaypoint, Maneuver> planner = new AStarPlanner<SpatialWaypoint, Maneuver>();
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

//    private static final AlternativePathPlanner<SpatialWaypoint, Maneuver> alternativePlanner = new TrajectoryDistanceMetric<SpatialWaypoint, Maneuver>( planner );
//    private static final AlternativePathPlanner<SpatialWaypoint, Maneuver> alternativePlanner = new DifferentStateMetric<SpatialWaypoint, Maneuver>( planner );


    @Override
    public void init(String[] args) {
    }

    @Override
    public void create() {
        ManeuverGraphInterface originalGraph = FourWayConstantSpeedGridGraph.create(WORLD_SIZE, WORLD_SIZE, WORLD_SIZE, WORLD_SIZE, 1.0); 

        border = Arrays.asList(new SpatialWaypoint[] {
                originalGraph.getNearestWaypoint(new Point( 0,  0, 0)),
                originalGraph.getNearestWaypoint(new Point( 0, WORLD_SIZE, 0)),
                originalGraph.getNearestWaypoint(new Point(WORLD_SIZE, WORLD_SIZE, 0)),
                originalGraph.getNearestWaypoint(new Point(WORLD_SIZE,  0, 0))
        });

        
        graph = new ObstacleGraphView( originalGraph, new ChangeListener() {
            @Override
            public void graphChanged() {
                replan();
            }
        } );
               
        createVisualization();
        
        List<Point> obstacles = generateRandomObstacles(NUM_OF_RANDOM_OBSTACLES);
        for (Point obstacle : obstacles) {
            graph.addObstacle(obstacle);
        }
    }

    private List<Point> generateRandomObstacles(int number) {
        List<Point> obstacles = new ArrayList<Point>(number);
        for (int i=0; i<number; i++) {
            obstacles.add(new Point(Math.random() * WORLD_SIZE, Math.random() * WORLD_SIZE, 0.0 ));
        }
                
        return obstacles;
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
        
        SpatialWaypoint startVertex = graph.getNearestWaypoint(new Point(0, 0, 0));
        SpatialWaypoint targetVertex = graph.getNearestWaypoint(new Point(10, 10, 0));

        AllPathsIterator<SpatialWaypoint, Maneuver> pathsIt = new AllPathsIterator<SpatialWaypoint, Maneuver>(voronoiGraph.graph,
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
            PlannedPath<SpatialWaypoint, Maneuver> planPath = pathsIt.next();
            if (DEBUG_VIEW) {
                paths.add(planPath);
            }

            delaunayGraph.graph = voronoiGraphAlg.getDelaunayGraph(border);
            
            voronoiGraphAlg.removeDualEdges(delaunayGraph.graph, planPath.getEdgeList());

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
        
            PlanarGraph<Maneuver> planarGraph = new PlanarGraph<Maneuver>(graph);

            for (Maneuver edge : delaunayGraph.graph.edgeSet()) {
                planarGraph.removeCrossingEdges(edge.getSource(), edge.getTarget());
            }
    
            planPath = planner.planPath(graph,
                    startVertex,
                    targetVertex
                    );

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
