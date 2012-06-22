package cz.agents.alite.trajectorytools.demo;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.AStarShortestPath;

import cz.agents.alite.creator.Creator;
import cz.agents.alite.tactical.universe.world.map.UrbanMap;
import cz.agents.alite.tactical.vis.VisualInteractionLayer;
import cz.agents.alite.tactical.vis.VisualInteractionLayer.VisualInteractionProvidingEntity;
import cz.agents.alite.trajectorytools.graph.maneuver.FourWayConstantSpeedGridGraph;
import cz.agents.alite.trajectorytools.graph.maneuver.Maneuver;
import cz.agents.alite.trajectorytools.graph.maneuver.ManeuverGraph;
import cz.agents.alite.trajectorytools.graph.spatialwaypoint.SpatialWaypoint;
import cz.agents.alite.trajectorytools.util.Point;
import cz.agents.alite.trajectorytools.vis.GraphLayer;
import cz.agents.alite.trajectorytools.vis.GraphPathLayer;
import cz.agents.alite.trajectorytools.vis.PathHolder;
import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.element.FilledStyledCircle;
import cz.agents.alite.vis.element.aggregation.FilledStyledCircleElements;
import cz.agents.alite.vis.element.implemetation.FilledStyledCircleImpl;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;
import cz.agents.alite.vis.layer.terminal.FilledStyledCircleLayer;


public class Demo1Creator implements Creator {

	private static final Color VERTEX_COLOR = new Color(240, 240, 240);
	private static final Color VERTEX_COLOR_INACTIVE = new Color(250, 250, 250);
	private static final Color EDGE_COLOR = new Color(220, 220, 220);
	private static final Color EDGE_COLOR_INACTIVE = new Color(240, 240, 240);

	private static final Color OBSTACLE_COLOR = Color.ORANGE;
    private static final double OBSTACLE_RADIUS = 0.3;

    private ManeuverGraph originalGraph;
	private ManeuverGraph graph;
	private PathHolder<SpatialWaypoint, Maneuver> path = new PathHolder<SpatialWaypoint, Maneuver>();
	
	private Set<Point> obstacles = new HashSet<Point>();

    @Override
    public void init(String[] args) {
    }

    @Override
    public void create() {
        originalGraph = new FourWayConstantSpeedGridGraph(10, 10, 10, 10, 1.0);
        graph = new FourWayConstantSpeedGridGraph(10, 10, 10, 10, 1.0);

        createVisualization();
    }

    private void createVisualization() {
        VisManager.setInitParam("Trajectory Tools Vis", 1024, 768, 20, 20);
        VisManager.setPanningBounds(new Rectangle(-500, -500, 1600, 1600));
        VisManager.init();
        
        Vis.setPosition(50, 50, 1);

        // background
        VisManager.registerLayer(ColorLayer.create(Color.WHITE));

        // static
        VisManager.registerLayer(GraphLayer.create(originalGraph, EDGE_COLOR_INACTIVE, VERTEX_COLOR_INACTIVE, 1, 4));
        VisManager.registerLayer(GraphLayer.create(graph, EDGE_COLOR, VERTEX_COLOR, 1, 4));

        // draw the shortest path
        VisManager.registerLayer(GraphPathLayer.create(graph, path, Color.RED, Color.RED.darker(), 2, 4));

        // clickable obstacles

        // interaction
        VisManager.registerLayer(VisualInteractionLayer.create(new VisualInteractionProvidingEntity() {
			
			@Override
			public void interactVisually(double x, double y, MouseEvent e) {
				// find the closest point of the Graph
				SpatialWaypoint point = originalGraph.getNearestWaypoint(new Point(x, y, 0));

				if (e.getButton() == MouseEvent.BUTTON1) {
					graph.removeVertex( point );
					obstacles.add(point);
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					if ( obstacles.contains(point) ) {
						graph.addVertex(point);
						for (Maneuver edge : originalGraph.edgesOf(point)) {
							SpatialWaypoint source = edge.getSource();
							SpatialWaypoint target = edge.getTarget();
							if (graph.containsVertex(source) && graph.containsVertex(target)) {
								graph.addEdge(source, target, edge);
							}
						}
						
						obstacles.remove(point);					
					}
				} else {
					return;
				}

				replan();
			}			
		
			@Override
			public String getName() {
				return "Obstacles layer : ClickableObstaclesLayer";
			}
			
			@Override
			public UrbanMap getMap() {
				throw new UnsupportedOperationException("!!!!");
			}
		})); 

        // drawing obstacles
        VisManager.registerLayer(FilledStyledCircleLayer.create(new FilledStyledCircleElements() {
			
			@Override
			public int getStrokeWidth() {
				return 1;
			}
			
			@Override
			public Color getColor() {
				return OBSTACLE_COLOR.darker();
			}
			
			@Override
			public Color getFillColor() {
				return OBSTACLE_COLOR;
			}
			
			@Override
			public Iterable<? extends FilledStyledCircle> getCircles() {
				List<FilledStyledCircle> circles = new ArrayList<FilledStyledCircle>(obstacles.size());
				for (final Point point : obstacles) {
					FilledStyledCircle circle = new FilledStyledCircleImpl(point, OBSTACLE_RADIUS, OBSTACLE_COLOR, OBSTACLE_COLOR.darker());
					circles.add(circle);
				}
				return circles;
			}
		}));

        // Overlay
        VisManager.registerLayer(VisInfoLayer.create());
    }

	protected void replan() {
		try {
			AStarShortestPath<SpatialWaypoint, Maneuver> aStar = new AStarShortestPath<SpatialWaypoint, Maneuver>(
					graph, 
					originalGraph.getNearestWaypoint(new Point(0, 0, 0)),
					originalGraph.getNearestWaypoint(new Point(10, 10, 0)),
	                new AStarShortestPath.Heuristic<SpatialWaypoint>() {
						@Override
						public double getHeuristicEstimate(SpatialWaypoint current, SpatialWaypoint goal) {
							return 0;
						}
					});
			GraphPath<SpatialWaypoint, Maneuver> path2 = aStar.getPath();
			path.graphPath = path2;
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			path.graphPath = null;
		}
	}
}
