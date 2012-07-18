package cz.agents.alite.trajectorytools.demo;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graph;

import cz.agents.alite.creator.Creator;
import cz.agents.alite.trajectorytools.graph.spatial.SpatialGraphs;
import cz.agents.alite.trajectorytools.graph.spatial.SpatialGridFactory;
import cz.agents.alite.trajectorytools.graph.spatial.maneuvers.SpatialManeuver;
import cz.agents.alite.trajectorytools.planner.AStarPlanner;
import cz.agents.alite.trajectorytools.planner.HeuristicFunction;
import cz.agents.alite.trajectorytools.planner.NullGoalPenaltyFunction;
import cz.agents.alite.trajectorytools.planner.PlannedPath;
import cz.agents.alite.trajectorytools.trajectory.ManeuverTrajectory;
import cz.agents.alite.trajectorytools.trajectory.SampledTrajectory;
import cz.agents.alite.trajectorytools.trajectory.Trajectory;
import cz.agents.alite.trajectorytools.util.Point;
import cz.agents.alite.trajectorytools.util.SeparationDetector;
import cz.agents.alite.trajectorytools.util.TimePoint;
import cz.agents.alite.trajectorytools.util.Waypoint;
import cz.agents.alite.trajectorytools.vis.ConflictsLayer;
import cz.agents.alite.trajectorytools.vis.ConflictsLayer.ConflictsHolder;
import cz.agents.alite.trajectorytools.vis.GraphLayer;
import cz.agents.alite.trajectorytools.vis.TrajectoryLayer;
import cz.agents.alite.trajectorytools.vis.TrajectoryLayer.TrajectoryProvider;
import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;


public class SeparationDetectorDemoCreator implements Creator {

    private Graph<Waypoint, SpatialManeuver> graph;
    private Trajectory trajectory1;
    private Trajectory trajectory2;
    private List<TimePoint> conflicts;

    @Override
    public void init(String[] args) {
    }

    @Override
    public void create() {
        graph = SpatialGridFactory.create4WayGrid(10, 10, 10, 10, 1.0);

        HeuristicFunction<Waypoint> h = new HeuristicFunction<Waypoint>() {
            @Override
                public double getHeuristicEstimate(Waypoint current, Waypoint goal) {
                    return current.distance(goal);
                }
            };

        AStarPlanner<Waypoint, SpatialManeuver> astar = new AStarPlanner<Waypoint, SpatialManeuver>();

        PlannedPath<Waypoint, SpatialManeuver> path1 = astar.planPath(graph,
                    SpatialGraphs.getNearestVertex(graph, new Point(0, 0, 0)),
                    SpatialGraphs.getNearestVertex(graph, new Point(10, 10, 0)),
                    new NullGoalPenaltyFunction<Waypoint>(), h);

        PlannedPath<Waypoint, SpatialManeuver> path2 = astar.planPath(graph,
                SpatialGraphs.getNearestVertex(graph, new Point(10, 10, 0)),
                SpatialGraphs.getNearestVertex(graph, new Point(0, 0, 0)),
                new NullGoalPenaltyFunction<Waypoint>(), h);

        trajectory1 =  new SampledTrajectory(new ManeuverTrajectory<Waypoint, SpatialManeuver>(0.0, path1, path1.getWeight()), 0.1);
        trajectory2 =  new SampledTrajectory(new ManeuverTrajectory<Waypoint, SpatialManeuver>(0.0, path2, path2.getWeight()), 0.1);

        List<Trajectory> trajectories = new LinkedList<Trajectory>();
        trajectories.add(trajectory1);
        trajectories.add(trajectory2);
        conflicts = SeparationDetector.computeAllPairwiseConflicts(trajectories, 1.0);

        createVisualization();
    }

    private void createVisualization() {
        VisManager.setInitParam("Trajectory Tools Vis", 1024, 768, 20, 20);
        VisManager.setPanningBounds(new Rectangle(-500, -500, 1600, 1600));
        VisManager.init();

        Vis.setPosition(50, 50, 1);

        // background
        VisManager.registerLayer(ColorLayer.create(Color.WHITE));

        // graph
        VisManager.registerLayer(GraphLayer.create(graph, Color.GRAY, Color.GRAY, 1, 4));

        VisManager.registerLayer(TrajectoryLayer.create(
                new TrajectoryProvider() {
                    @Override
                    public Trajectory getTrajectory() {
                        return trajectory1;
                    }
                }, Color.BLUE, 0.1, 100.0, 't'));

        VisManager.registerLayer(TrajectoryLayer.create(
                new TrajectoryProvider() {

                    @Override
                    public Trajectory getTrajectory() {
                        return trajectory2;
                    }

                }, Color.YELLOW, 0.1, 100.0, 't'));

        VisManager.registerLayer(ConflictsLayer.create(new ConflictsHolder(conflicts), 1.0));

        // Overlay
        VisManager.registerLayer(VisInfoLayer.create());
    }
}
