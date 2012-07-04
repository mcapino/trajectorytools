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

    private ObstacleGraphView graph;
    private List<PlannedPath<SpatialWaypoint, Maneuver>> paths = new ArrayList<PlannedPath<SpatialWaypoint,Maneuver>>();

    VoronoiDelaunayGraph voronoiGraphAlg = new VoronoiDelaunayGraph();
    GraphHolder<SpatialWaypoint, Maneuver> voronoiGraph = new GraphHolder<SpatialWaypoint, Maneuver>();
    GraphHolder<SpatialWaypoint, Maneuver> delaunayGraph = new GraphHolder<SpatialWaypoint, Maneuver>();
    
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
        ManeuverGraphInterface originalGraph = FourWayConstantSpeedGridGraph.create(10, 10, 10, 10, 1.0); 

        border = Arrays.asList(new SpatialWaypoint[] {
                originalGraph.getNearestWaypoint(new Point( 0,  0, 0)),
                originalGraph.getNearestWaypoint(new Point( 0, 10, 0)),
                originalGraph.getNearestWaypoint(new Point(10, 10, 0)),
                originalGraph.getNearestWaypoint(new Point(10,  0, 0))
        });

        
        graph = new ObstacleGraphView( originalGraph, new ChangeListener() {
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
        

        VisManager.registerLayer(GraphLayer.create(voronoiGraph, Color.GREEN, Color.GREEN, 1, 4));
        VisManager.registerLayer(GraphLayer.create(delaunayGraph, Color.BLUE, Color.BLUE, 1, 4, 0.02));

        // draw the shortest path
        VisManager.registerLayer(GraphPathLayer.create(voronoiGraph, paths, 2, 4));

        // Overlay
        VisManager.registerLayer(VisInfoLayer.create());
    }

	protected void replan() {
	    voronoiGraphAlg.setObstacles(graph.getObstacles());
	    
	    delaunayGraph.graph = null;
//        delaunayGraph.graph = voronoiGraphAlg.getDelaunayGraph(border);
        
        voronoiGraph.graph = voronoiGraphAlg.getVoronoiGraph(border);

        AllPathsIterator<SpatialWaypoint, Maneuver> pathsIt = new AllPathsIterator<SpatialWaypoint, Maneuver>(voronoiGraph.graph,
                graph.getNearestWaypoint(new Point(0, 0, 0)),
                graph.getNearestWaypoint(new Point(10, 10, 0))
                );

        paths.clear();
        
        while (pathsIt.hasNext()) {
            paths.add(pathsIt.next());
        }

        System.out.println("paths.size(): " + paths.size());
        
//        PlannedPath<SpatialWaypoint, Maneuver> planPath = planner.planPath(voronoiGraph.graph,
//                    graph.getNearestWaypoint(new Point(0, 0, 0)),
//                    graph.getNearestWaypoint(new Point(10, 10, 0))
//                    );
//        if (planPath != null) {
//            System.out.println("planPath: " + planPath.getWeight());
//            paths.add(planPath);
//        } else {
//            System.out.println("Path not found!!!");
//        }
	}
}
