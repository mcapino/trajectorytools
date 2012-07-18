package cz.agents.alite.trajectorytools.demo;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.alg.AllPathsIterator;
import org.jgrapht.graph.DefaultWeightedEdge;

import cz.agents.alite.creator.Creator;
import cz.agents.alite.trajectorytools.graph.ObstacleGraphView;
import cz.agents.alite.trajectorytools.graph.ObstacleGraphView.ChangeListener;
import cz.agents.alite.trajectorytools.graph.VoronoiDelaunayGraph;
import cz.agents.alite.trajectorytools.graph.spatial.SpatialGraphs;
import cz.agents.alite.trajectorytools.graph.spatial.SpatialGridFactory;
import cz.agents.alite.trajectorytools.graph.spatial.maneuvers.SpatialManeuver;
import cz.agents.alite.trajectorytools.planner.AStarPlanner;
import cz.agents.alite.trajectorytools.planner.HeuristicFunction;
import cz.agents.alite.trajectorytools.planner.PlannedPath;
import cz.agents.alite.trajectorytools.util.Point;
import cz.agents.alite.trajectorytools.util.Waypoint;
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
    private List<PlannedPath<Point, DefaultWeightedEdge>> paths = new ArrayList<PlannedPath<Point, DefaultWeightedEdge>>();
    private List<PlannedPath<Point, DefaultWeightedEdge>> maneuverPaths = new ArrayList<PlannedPath<Point, DefaultWeightedEdge>>();


    VoronoiDelaunayGraph voronoiGraphAlg = new VoronoiDelaunayGraph();
    GraphHolder<Point, DefaultWeightedEdge> voronoiGraph = new GraphHolder<Point, DefaultWeightedEdge>();
    GraphHolder<Point, DefaultWeightedEdge> delaunayGraph = new GraphHolder<Point, DefaultWeightedEdge>();
    GraphHolder<Point, DefaultWeightedEdge> otherGraph = new GraphHolder<Point, DefaultWeightedEdge>();

    private static final AStarPlanner<Waypoint, DefaultWeightedEdge> planner = new AStarPlanner<Waypoint, DefaultWeightedEdge>();
    {
        planner.setHeuristicFunction(new HeuristicFunction<Waypoint>() {
        @Override
            public double getHeuristicEstimate(Waypoint current, Waypoint goal) {
                return current.distance(goal) + ( current.x > current.y ? 0.1 : -0.1 );
            }
        });
    }

    private static final AStarPlanner<Point, DefaultWeightedEdge> maneuverPlanner = new AStarPlanner<Point, DefaultWeightedEdge>();
    {
        planner.setHeuristicFunction(new HeuristicFunction<Waypoint>() {
        @Override
            public double getHeuristicEstimate(Waypoint current, Waypoint goal) {
                return current.distance(goal) + ( current.x > current.y ? 0.1 : -0.1 );
            }
        });
    }

//    private static final ObstacleExtensions alternativePlanner = new ObstacleExtensions(planner);
    private List<Point> border;

//    private static final AlternativePathPlanner<SpatialWaypoint, SpatialManeuver> alternativePlanner = new TrajectoryDistanceMetric<SpatialWaypoint, SpatialManeuver>( planner );
//    private static final AlternativePathPlanner<SpatialWaypoint, SpatialManeuver> alternativePlanner = new DifferentStateMetric<SpatialWaypoint, SpatialManeuver>( planner );


    @Override
    public void init(String[] args) {
    }

    @Override
    public void create() {
        Graph<Waypoint, SpatialManeuver> originalGraph = SpatialGridFactory.create4WayGrid(WORLD_SIZE, WORLD_SIZE, WORLD_SIZE, WORLD_SIZE, 1.0);

        border = Arrays.asList(new Point[] {
                SpatialGraphs.getNearestVertex(originalGraph, new Point( 0,  0, 0)),
                SpatialGraphs.getNearestVertex(originalGraph, new Point( 0, WORLD_SIZE, 0)),
                SpatialGraphs.getNearestVertex(originalGraph, new Point(WORLD_SIZE,  WORLD_SIZE, 0)),
                SpatialGraphs.getNearestVertex(originalGraph, new Point(WORLD_SIZE,  0, 0))
        });


        graph = ObstacleGraphView.createFromGraph(originalGraph, new ChangeListener() {
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

        Point startVertex = SpatialGraphs.getNearestVertex(graph, new Point(0, 0, 0));
        Point targetVertex =  SpatialGraphs.getNearestVertex(graph, new Point(10, 10, 0));

        AllPathsIterator<Point, DefaultWeightedEdge> pathsIt
            = new AllPathsIterator<Point, DefaultWeightedEdge>(voronoiGraph.graph,
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

            PlannedPath<Point, DefaultWeightedEdge> planPath = pathsIt.next();
            if (DEBUG_VIEW) {
                paths.add(planPath);
            }

            delaunayGraph.graph = voronoiGraphAlg.getDelaunayGraph(border);

            voronoiGraphAlg.removeDualEdges(delaunayGraph.graph, planPath.getEdgeList());

//            PlanarGraph<DefaultWeightedEdge> planarGraphDelaunay = new PlanarGraph<DefaultWeightedEdge>(delaunayGraph.graph);
//
//            delaunayGraph.graph.removeVertex(startVertex);
//            delaunayGraph.graph.removeVertex(targetVertex);
//
//            for (DefaultWeightedEdge voronoiEdge: planPath.getEdgeList()) {
//                planarGraphDelaunay.removeCrossingEdges(planarGraphDelaunay.getEdgeSource(voronoiEdge),planarGraphDelaunay.getEdgeTarget(voronoiEdge));
//            }
//
//            delaunayGraph.graph = planarGraphDelaunay;

            graph.refresh();

//            PlanarGraph planarGraph = PlanarGraph.createPlanarGraphCopy(graph);

            for (DefaultWeightedEdge edge : delaunayGraph.graph.edgeSet()) {
                graph.removeCrossingEdges(delaunayGraph.graph.getEdgeSource(edge), delaunayGraph.graph.getEdgeTarget(edge));
            }

            PlannedPath<Point, DefaultWeightedEdge> result = maneuverPlanner.planPath(graph,
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
