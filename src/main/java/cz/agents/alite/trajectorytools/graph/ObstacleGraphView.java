package cz.agents.alite.trajectorytools.graph;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import cz.agents.alite.tactical.universe.world.map.UrbanMap;
import cz.agents.alite.tactical.vis.VisualInteractionLayer;
import cz.agents.alite.tactical.vis.VisualInteractionLayer.VisualInteractionProvidingEntity;
import cz.agents.alite.trajectorytools.graph.spatial.GraphWithObstacles;
import cz.agents.alite.trajectorytools.graph.spatial.SpatialGraphs;
import cz.agents.alite.trajectorytools.util.Point;
import cz.agents.alite.trajectorytools.vis.GraphLayer;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.element.FilledStyledCircle;
import cz.agents.alite.vis.element.aggregation.FilledStyledCircleElements;
import cz.agents.alite.vis.element.implemetation.FilledStyledCircleImpl;
import cz.agents.alite.vis.layer.terminal.FilledStyledCircleLayer;

public class ObstacleGraphView extends PlanarGraph implements GraphWithObstacles<Point, DefaultWeightedEdge> {
    private static final long serialVersionUID = 3428956208593195747L;

    private static final Color VERTEX_COLOR = new Color(240, 240, 240);
    private static final Color VERTEX_COLOR_INACTIVE = new Color(250, 250, 250);
    private static final Color EDGE_COLOR = new Color(220, 220, 220);
    private static final Color EDGE_COLOR_INACTIVE = new Color(240, 240, 240);

    private static final Color OBSTACLE_COLOR = Color.ORANGE;
    private static final double OBSTACLE_RADIUS = 0.3;


    private final Graph<Point, DefaultWeightedEdge> originalGraph;

    private final Set<Point> obstacles = new HashSet<Point>();

    private final ChangeListener changeListener;

    public interface ChangeListener {
        void graphChanged();
    }

    public ObstacleGraphView(Graph<Point, DefaultWeightedEdge> originalGraph, ChangeListener changeListener) {
        super(originalGraph);
        this.changeListener = changeListener;

        this.originalGraph = SpatialGraphs.clone(originalGraph);
    }
    
    public static <V extends Point, E> ObstacleGraphView createFromGraph(Graph<V, E> graph, ChangeListener changeListener) {
        Graph<Point, DefaultWeightedEdge> planarGraph = new DirectedWeightedMultigraph<Point, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        for (V vertex : graph.vertexSet()) {
            planarGraph.addVertex(vertex);
        }
        
        for (E edge : graph.edgeSet()) {
            planarGraph.addEdge(graph.getEdgeSource(edge), graph.getEdgeTarget(edge));
        }
        
        return new ObstacleGraphView(planarGraph, changeListener);
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
                Point point = SpatialGraphs.getNearestVertex(originalGraph, new Point(x, y, 0));

                if (e.getButton() == MouseEvent.BUTTON1) {
                    addObstacle(point);
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    removeObstacle(point);
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

    @Override
    public void refresh() {
        removeAllVertices(new ArrayList<Point>(vertexSet()));

        for (Point vertex : originalGraph.vertexSet()) {
            addVertex(vertex);
        }

        for (DefaultWeightedEdge edge : originalGraph.edgeSet()) {
            addEdge(originalGraph.getEdgeSource(edge), originalGraph.getEdgeTarget(edge));
        }

        for (Point obstacle : obstacles) {
            removeVertex( obstacle );
        }
    }

    @Override
    public Set<Point> getObstacles() {
        return obstacles;
    }

    @Override
    public void addObstacle(Point point) {
        removeVertex( point );
        obstacles.add(point);
    }

    public void removeObstacle(Point point) {
        if ( obstacles.contains(point) ) {
            addVertex(point);
            for (DefaultWeightedEdge edge : originalGraph.edgesOf(point)) {
                Point source = originalGraph.getEdgeSource(edge);
                Point target = originalGraph.getEdgeTarget(edge);
                if (containsVertex(source) && containsVertex(target)) {
                    addEdge(source, target, edge);
                }
            }

            obstacles.remove(point);
        }
    }

}
