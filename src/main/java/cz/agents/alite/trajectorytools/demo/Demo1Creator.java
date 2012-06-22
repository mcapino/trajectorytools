package cz.agents.alite.trajectorytools.demo;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.vecmath.Point3d;

import cz.agents.alite.creator.Creator;
import cz.agents.alite.tactical.universe.world.map.UrbanMap;
import cz.agents.alite.tactical.vis.VisualInteractionLayer;
import cz.agents.alite.tactical.vis.VisualInteractionLayer.VisualInteractionProvidingEntity;
import cz.agents.alite.trajectorytools.graph.maneuver.FourWayConstantSpeedGridGraph;
import cz.agents.alite.trajectorytools.graph.maneuver.ManeuverGraph;
import cz.agents.alite.trajectorytools.util.Point;
import cz.agents.alite.trajectorytools.vis.GraphLayer;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.element.FilledStyledCircle;
import cz.agents.alite.vis.element.aggregation.FilledStyledCircleElements;
import cz.agents.alite.vis.element.implemetation.FilledStyledCircleImpl;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;
import cz.agents.alite.vis.layer.terminal.FilledStyledCircleLayer;


public class Demo1Creator implements Creator {

	private static final Color OBSTACLE_COLOR = Color.ORANGE;
    private static final double OBSTACLE_RADIUS = 0.3;
	private ManeuverGraph graph;

    @Override
    public void init(String[] args) {
    }

    @Override
    public void create() {
        graph = new FourWayConstantSpeedGridGraph(10, 10, 10, 10, 1.0);

        createVisualization();
    }

    private void createVisualization() {
        VisManager.setInitParam("Trajectory Tools Vis", 1024, 768, 20, 20);
        VisManager.setPanningBounds(new Rectangle(-500, -500, 1600, 1600));
        VisManager.init();

        // background
        VisManager.registerLayer(ColorLayer.create(Color.WHITE));

        // static
        VisManager.registerLayer(GraphLayer.create(graph, new Color(220, 220, 220), new Color(240, 240, 240), 1, 4));

        // clickable obstacles
        final Set<Point> obstacles = new HashSet<Point>();
        
        // interaction
        VisManager.registerLayer(VisualInteractionLayer.create(new VisualInteractionProvidingEntity() {
			
			@Override
			public void interactVisually(double x, double y, MouseEvent e) {
				// find the closest point of the Graph
				double minDist = Double.MAX_VALUE;
				Point minPoint = null; 
				for (Point point : graph.vertexSet()) {
					double dist = point.distance(new Point3d(x, y, 0));
					if (dist < minDist) {
						minDist = dist;
						minPoint = point;
					}
				}

				if (e.getButton() == MouseEvent.BUTTON1) {
					obstacles.add(minPoint);
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					obstacles.remove(minPoint);					
				}
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
}
