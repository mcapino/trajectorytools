package cz.agents.alite.trajectorytools.graph.maneuver;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.jgrapht.graph.GraphDelegator;

import cz.agents.alite.tactical.universe.world.map.UrbanMap;
import cz.agents.alite.tactical.vis.VisualInteractionLayer;
import cz.agents.alite.tactical.vis.VisualInteractionLayer.VisualInteractionProvidingEntity;
import cz.agents.alite.trajectorytools.graph.spatial.SpatialManeuverGraph;
import cz.agents.alite.trajectorytools.graph.spatial.SpatialManeuverGraphs;
import cz.agents.alite.trajectorytools.graph.spatial.SpatialWaypoint;
import cz.agents.alite.trajectorytools.graph.spatial.maneuvers.SpatialManeuver;
import cz.agents.alite.trajectorytools.util.Point;
import cz.agents.alite.trajectorytools.vis.GraphLayer;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.element.FilledStyledCircle;
import cz.agents.alite.vis.element.aggregation.FilledStyledCircleElements;
import cz.agents.alite.vis.element.implemetation.FilledStyledCircleImpl;
import cz.agents.alite.vis.layer.terminal.FilledStyledCircleLayer;

public class ObstacleGraphView extends GraphDelegator<SpatialWaypoint, SpatialManeuver> {
	private static final long serialVersionUID = 3428956208593195747L;

	private static final Color VERTEX_COLOR = new Color(240, 240, 240);
	private static final Color VERTEX_COLOR_INACTIVE = new Color(250, 250, 250);
	private static final Color EDGE_COLOR = new Color(220, 220, 220);
	private static final Color EDGE_COLOR_INACTIVE = new Color(240, 240, 240);

	private static final Color OBSTACLE_COLOR = Color.ORANGE;
	private static final double OBSTACLE_RADIUS = 0.3;


    private final SpatialManeuverGraph originalGraph;
    
    private final Set<Point> obstacles = new HashSet<Point>();

    private final ChangeListener changeListener;

    public interface ChangeListener {
        void graphChanged();
    }

	public ObstacleGraphView(SpatialManeuverGraph originalGraph, ChangeListener changeListener) {
		super(originalGraph);
        this.changeListener = changeListener;
        this.originalGraph = SpatialManeuverGraphs.clone(originalGraph);      
	}
	
	public void createVisualization() {
        VisManager.registerLayer(GraphLayer.create(originalGraph, EDGE_COLOR_INACTIVE, VERTEX_COLOR_INACTIVE, 1, 4));
        VisManager.registerLayer(GraphLayer.create(this, EDGE_COLOR, VERTEX_COLOR, 1, 4));
        
        // clickable obstacles

        // interaction
        VisManager.registerLayer(VisualInteractionLayer.create(new VisualInteractionProvidingEntity() {
            
            @Override
            public void interactVisually(double x, double y, MouseEvent e) {
                // find the closest point of the Graph
                SpatialWaypoint point = SpatialManeuverGraphs.getNearestWaypoint(originalGraph, new Point(x, y, 0));

                if (e.getButton() == MouseEvent.BUTTON1) {
                    removeVertex( point );
                    obstacles.add(point);
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    if ( obstacles.contains(point) ) {
                        addVertex(point);
                        for (SpatialManeuver edge : originalGraph.edgesOf(point)) {
                            SpatialWaypoint source = originalGraph.getEdgeSource(edge);
                            SpatialWaypoint target = originalGraph.getEdgeTarget(edge);
                            if (containsVertex(source) && containsVertex(target)) {
                                addEdge(source, target, edge);
                            }
                        }
                        
                        obstacles.remove(point);                    
                    }
                } else {
                    return;
                }

                if ( changeListener != null ) {
                    changeListener.graphChanged();
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
	}

    public Set<Point> getObstacles() {
        return obstacles;
    }
}
