package cz.agents.alite.trajectorytools.demo;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graph;

import cz.agents.alite.creator.Creator;
import cz.agents.alite.trajectorytools.alterantiveplanners.AlternativePathPlanner;
import cz.agents.alite.trajectorytools.alterantiveplanners.AlternativePlannerSelector;
import cz.agents.alite.trajectorytools.alterantiveplanners.DifferentStateMetricPlanner;
import cz.agents.alite.trajectorytools.alterantiveplanners.ObstacleExtensions;
import cz.agents.alite.trajectorytools.alterantiveplanners.TrajectoryDistanceMetricPlanner;
import cz.agents.alite.trajectorytools.alterantiveplanners.VoronoiDelaunayPlanner;
import cz.agents.alite.trajectorytools.alterantiveplanners.TrajectoryDistanceMetricPlanner;
import cz.agents.alite.trajectorytools.alterantiveplanners.VoronoiDelaunayPlanner;
import cz.agents.alite.trajectorytools.graph.ObstacleGraphView;
import cz.agents.alite.trajectorytools.graph.ObstacleGraphView.ChangeListener;
import cz.agents.alite.trajectorytools.graph.spatial.SpatialGridFactory;
import cz.agents.alite.trajectorytools.graph.spatial.SpatialGraphs;
import cz.agents.alite.trajectorytools.graph.spatial.maneuvers.SpatialManeuver;
import cz.agents.alite.trajectorytools.planner.AStarPlanner;
import cz.agents.alite.trajectorytools.planner.HeuristicFunction;
import cz.agents.alite.trajectorytools.planner.PlannedPath;
import cz.agents.alite.trajectorytools.trajectorymetrics.DifferentStateMetric;
import cz.agents.alite.trajectorytools.trajectorymetrics.ManeuverTrajectoryMetric;
import cz.agents.alite.trajectorytools.trajectorymetrics.TrajectoryDistanceMetric;
import cz.agents.alite.trajectorytools.util.Point;
import cz.agents.alite.trajectorytools.util.Waypoint;
import cz.agents.alite.trajectorytools.vis.GraphPathLayer;
import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;

public class DemoAlternative1Creator implements Creator {

    private static final int WORLD_SIZE = 10;

    private static final int PATH_SOLUTION_LIMIT = 5;

    private ObstacleGraphView graph;
    private List<PlannedPath<Waypoint, SpatialManeuver>> paths = new ArrayList<PlannedPath<Waypoint,SpatialManeuver>>();

    private static final AStarPlanner<Waypoint, SpatialManeuver> planner = new AStarPlanner<Waypoint, SpatialManeuver>();
    {
        planner.setHeuristicFunction(new HeuristicFunction<Waypoint>() {
        @Override
            public double getHeuristicEstimate(Waypoint current, Waypoint goal) {
                return current.distance(goal) + ( current.x > current.y ? 0.1 : -0.1 );
            }
        });
    }

    private static final AlternativePathPlanner[] alternativePlanners = new AlternativePathPlanner[] {
        new DifferentStateMetricPlanner( planner, PATH_SOLUTION_LIMIT ),
        new TrajectoryDistanceMetricPlanner( planner, PATH_SOLUTION_LIMIT, WORLD_SIZE ),
        new ObstacleExtensions(planner),
        new VoronoiDelaunayPlanner( planner ),
        new AlternativePlannerSelector( new ObstacleExtensions(planner), PATH_SOLUTION_LIMIT),
        new AlternativePlannerSelector( new VoronoiDelaunayPlanner(planner), PATH_SOLUTION_LIMIT),
    };

    private static final ManeuverTrajectoryMetric[] trajectoryMetrics = new ManeuverTrajectoryMetric [] {
        new DifferentStateMetric(),
        new TrajectoryDistanceMetric()
    };

    private static final int CURRENT_PLANNER = 1;
    @Override
    public void init(String[] args) {
    }

    @Override
    public void create() {
        Graph<Waypoint, SpatialManeuver> originalGraph = SpatialGridFactory.create4WayGrid(WORLD_SIZE, WORLD_SIZE, WORLD_SIZE, WORLD_SIZE, 1.0);

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
                		alternativePlanners[CURRENT_PLANNER].planPath(
                        graph,
                        SpatialGraphs.getNearestWaypoint(graph, new Point(0, 0, 0)),
                        SpatialGraphs.getNearestWaypoint(graph, new Point(WORLD_SIZE, WORLD_SIZE, 0))
                    ) );
                System.out.println("paths: " + paths.size());
                for (PlannedPath<Waypoint, SpatialManeuver> path : paths) {
                    System.out.println("path.getWeight(): " + path.getPathLength());
                }
                
                for (ManeuverTrajectoryMetric metric : trajectoryMetrics) {
                    System.out.println(metric.getName() + ": " + evaluateTrajectories(paths, metric));
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
                paths.clear();
            }
    }
	
	   protected double evaluateTrajectories(
	            Collection<PlannedPath<Waypoint, SpatialManeuver>> paths,
	            ManeuverTrajectoryMetric metric) {
	        double value = 0;
	        for (PlannedPath<Waypoint, SpatialManeuver> path : paths) {
	            Collection<PlannedPath<Waypoint, SpatialManeuver>> tmpPaths = new LinkedList<PlannedPath<Waypoint, SpatialManeuver>>(paths);
	            tmpPaths.remove(path);
	            
	            value += metric.getTrajectoryValue(path, tmpPaths);
	        }
	        return value;
	    }
}
