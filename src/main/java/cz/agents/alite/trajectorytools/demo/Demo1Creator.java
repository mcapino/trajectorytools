package cz.agents.alite.trajectorytools.demo;

import java.awt.Color;
import java.awt.Rectangle;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.AStarShortestPath;
import org.jgrapht.alg.PathPlanner;
import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;

import cz.agents.alite.creator.Creator;
import cz.agents.alite.trajectorytools.graph.maneuver.FourWayConstantSpeedGridGraph;
import cz.agents.alite.trajectorytools.graph.maneuver.ListenableManeuverGraph;
import cz.agents.alite.trajectorytools.graph.maneuver.Maneuver;
import cz.agents.alite.trajectorytools.graph.maneuver.ObstacleGraphView;
import cz.agents.alite.trajectorytools.graph.spatialwaypoint.SpatialWaypoint;
import cz.agents.alite.trajectorytools.util.Point;
import cz.agents.alite.trajectorytools.vis.GraphPathLayer;
import cz.agents.alite.trajectorytools.vis.PathHolder;
import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;


public class Demo1Creator implements Creator {

	private ObstacleGraphView graph;
	private PathHolder<SpatialWaypoint, Maneuver> path = new PathHolder<SpatialWaypoint, Maneuver>();
	
    @Override
    public void init(String[] args) {
    }

    @Override
    public void create() {
        ListenableManeuverGraph listenableGraph = new ListenableManeuverGraph(
            FourWayConstantSpeedGridGraph.create(10, 10, 10, 10, 1.0) 
            );

        listenableGraph.addGraphListener(new GraphListener<SpatialWaypoint, Maneuver>() {
            
            @Override
            public void vertexRemoved(GraphVertexChangeEvent<SpatialWaypoint> e) {
                replan();
            }
            
            @Override
            public void vertexAdded(GraphVertexChangeEvent<SpatialWaypoint> e) {
            }
            
            @Override
            public void edgeRemoved(GraphEdgeChangeEvent<SpatialWaypoint, Maneuver> e) {
            }
            
            @Override
            public void edgeAdded(GraphEdgeChangeEvent<SpatialWaypoint, Maneuver> e) {
                replan();
            }
        });

        graph = new ObstacleGraphView( listenableGraph );
               
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
		    PathPlanner<SpatialWaypoint, Maneuver> aStar = new AStarShortestPath<SpatialWaypoint, Maneuver>();
			aStar.planPath(
					graph, 
					graph.getNearestWaypoint(new Point(0, 0, 0)),
					graph.getNearestWaypoint(new Point(10, 10, 0)),
	                new AStarShortestPath.Heuristic<SpatialWaypoint>() {
						@Override
						public double getHeuristicEstimate(SpatialWaypoint current, SpatialWaypoint goal) {
							return current.distance(goal);
						}
					});
			GraphPath<SpatialWaypoint, Maneuver> path2 = aStar.getPath();
			path.graphPath = path2;
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
			path.graphPath = null;
		}
	}
}
