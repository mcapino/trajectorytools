package cz.agents.alite.trajectorytools.demo;

import java.awt.Color;
import java.awt.Rectangle;

import cz.agents.alite.creator.Creator;
import cz.agents.alite.trajectorytools.graph.maneuver.FourWayConstantSpeedGridGraph;
import cz.agents.alite.trajectorytools.graph.maneuver.DefaultManeuver;
import cz.agents.alite.trajectorytools.graph.maneuver.ManeuverGraphInterface;
import cz.agents.alite.trajectorytools.graph.maneuver.ObstacleGraphView;
import cz.agents.alite.trajectorytools.graph.maneuver.ObstacleGraphView.ChangeListener;
import cz.agents.alite.trajectorytools.graph.spatialwaypoint.SpatialWaypoint;
import cz.agents.alite.trajectorytools.planner.AStarPlanner;
import cz.agents.alite.trajectorytools.planner.HeuristicFunction;
import cz.agents.alite.trajectorytools.planner.PathPlanner;
import cz.agents.alite.trajectorytools.util.Point;
import cz.agents.alite.trajectorytools.vis.GraphPathLayer;
import cz.agents.alite.trajectorytools.vis.PathHolder;
import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;


public class Demo1Creator implements Creator {

	private ObstacleGraphView graph;
	   private PathHolder<SpatialWaypoint, DefaultManeuver> path = new PathHolder<SpatialWaypoint, DefaultManeuver>();

    @Override
    public void init(String[] args) {
    }

    @Override
    public void create() {
        ManeuverGraphInterface listenableGraph = FourWayConstantSpeedGridGraph.create(10, 10, 10, 10, 1.0); 

        graph = new ObstacleGraphView( listenableGraph, new ChangeListener() {
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
        VisManager.registerLayer(GraphPathLayer.create(graph, path, Color.RED, Color.RED.darker(), 2, 4));

        // Overlay
        VisManager.registerLayer(VisInfoLayer.create());
    }

	protected void replan() {

        try {
            PathPlanner<SpatialWaypoint, DefaultManeuver> aStar = new AStarPlanner<SpatialWaypoint, DefaultManeuver>();

            aStar.setHeuristicFunction(new HeuristicFunction<SpatialWaypoint>() {
            @Override
                public double getHeuristicEstimate(SpatialWaypoint current, SpatialWaypoint goal) {
                    return current.distance(goal);
                }
            });
           
            path.plannedPath = aStar.planPath(
                    graph, 
                    graph.getNearestWaypoint(new Point(0, 0, 0)),
                    graph.getNearestWaypoint(new Point(10, 10, 0))
                    );
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            path.plannedPath = null;
        }
	}
}
