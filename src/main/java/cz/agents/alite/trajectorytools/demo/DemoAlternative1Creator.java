package cz.agents.alite.trajectorytools.demo;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import cz.agents.alite.creator.Creator;
import cz.agents.alite.trajectorytools.alterantiveplanners.AlternativePathPlanner;
import cz.agents.alite.trajectorytools.alterantiveplanners.VoronoiDelaunayPlanner;
import cz.agents.alite.trajectorytools.graph.maneuver.FourWayConstantSpeedGridGraph;
import cz.agents.alite.trajectorytools.graph.maneuver.Maneuver;
import cz.agents.alite.trajectorytools.graph.maneuver.ManeuverGraphInterface;
import cz.agents.alite.trajectorytools.graph.maneuver.ObstacleGraphView;
import cz.agents.alite.trajectorytools.graph.maneuver.ObstacleGraphView.ChangeListener;
import cz.agents.alite.trajectorytools.graph.spatialwaypoint.SpatialWaypoint;
import cz.agents.alite.trajectorytools.planner.AStarPlanner;
import cz.agents.alite.trajectorytools.planner.HeuristicFunction;
import cz.agents.alite.trajectorytools.planner.PlannedPath;
import cz.agents.alite.trajectorytools.util.Point;
import cz.agents.alite.trajectorytools.vis.GraphPathLayer;
import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;

public class DemoAlternative1Creator implements Creator {

    private ObstacleGraphView graph;
    private List<PlannedPath<SpatialWaypoint, Maneuver>> paths = new ArrayList<PlannedPath<SpatialWaypoint,Maneuver>>();

    private static final AStarPlanner<SpatialWaypoint, Maneuver> planner = new AStarPlanner<SpatialWaypoint, Maneuver>();
    {
        planner.setHeuristicFunction(new HeuristicFunction<SpatialWaypoint>() {
        @Override
            public double getHeuristicEstimate(SpatialWaypoint current, SpatialWaypoint goal) {
                return current.distance(goal) + ( current.x > current.y ? 0.1 : -0.1 );
            }
        });
    }

//    private static final AlternativePathPlanner alternativePlanner = new ObstacleExtensions(planner);
//    private static final AlternativePathPlanner alternativePlanner = new TrajectoryDistanceMetric( planner );
//    private static final AlternativePathPlanner alternativePlanner = new DifferentStateMetric( planner );
    private static final AlternativePathPlanner alternativePlanner = new VoronoiDelaunayPlanner( planner );


    @Override
    public void init(String[] args) {
    }

    @Override
    public void create() {
        ManeuverGraphInterface originalGraph = FourWayConstantSpeedGridGraph.create(10, 10, 10, 10, 1.0); 

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
        
        // draw the shortest path
        VisManager.registerLayer(GraphPathLayer.create(graph, paths, 2, 4));

        // Overlay
        VisManager.registerLayer(VisInfoLayer.create());
    }

	protected void replan() {
	       try {
	            paths.clear();
	            paths.addAll(
	                alternativePlanner.planPath(
	                    graph, 
	                    graph.getNearestWaypoint(new Point(0, 0, 0)),
	                    graph.getNearestWaypoint(new Point(10, 10, 0))
	                ) );
                System.out.println("paths: " + paths.size());
	        } catch (Exception e) {
	            System.out.println("Error: " + e.getMessage());
	            e.printStackTrace();
	            paths.clear();
	        }
	}
}
