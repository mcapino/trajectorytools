package cz.agents.alite.trajectorytools.demo;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;

import cz.agents.alite.creator.Creator;
import cz.agents.alite.trajectorytools.alterantiveplanners.ObstacleExtensions;
import cz.agents.alite.trajectorytools.graph.ObstacleGraphView;
import cz.agents.alite.trajectorytools.graph.ObstacleGraphView.ChangeListener;
import cz.agents.alite.trajectorytools.graph.spatial.SpatialGridFactory;
import cz.agents.alite.trajectorytools.graph.spatial.SpatialGraphs;
import cz.agents.alite.trajectorytools.graph.spatial.maneuvers.SpatialManeuver;
import cz.agents.alite.trajectorytools.planner.AStarPlanner;
import cz.agents.alite.trajectorytools.planner.HeuristicFunction;
import cz.agents.alite.trajectorytools.planner.PlannedPath;
import cz.agents.alite.trajectorytools.util.Point;
import cz.agents.alite.trajectorytools.util.Waypoint;
import cz.agents.alite.trajectorytools.vis.GraphPathLayer;
import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;

public class DemoAlternative1Creator implements Creator {

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

    private static final ObstacleExtensions alternativePlanner = new ObstacleExtensions(planner);

//    private static final AlternativePathPlanner<SpatialWaypoint, Maneuver> alternativePlanner = new TrajectoryDistanceMetric<SpatialWaypoint, Maneuver>( planner );
//    private static final AlternativePathPlanner<SpatialWaypoint, Maneuver> alternativePlanner = new DifferentStateMetric<SpatialWaypoint, Maneuver>( planner );


    @Override
    public void init(String[] args) {
    }

    @Override
    public void create() {
        Graph<Waypoint, SpatialManeuver> originalGraph = SpatialGridFactory.create4WayGrid(10, 10, 10, 10, 1.0);

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
                        SpatialGraphs.getNearestWaypoint(graph, new Point(0, 0, 0)),
                        SpatialGraphs.getNearestWaypoint(graph, new Point(10, 10, 0))
                    ) );
                System.out.println("paths: " + paths.size());
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
                paths.clear();
            }
    }
}
