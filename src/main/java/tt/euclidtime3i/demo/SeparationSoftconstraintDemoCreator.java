package tt.euclidtime3i.demo;

import java.awt.Color;
import java.util.Collection;
import java.util.LinkedList;

import javax.vecmath.Point2d;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.AStarShortestPath;
import org.jgrapht.util.Goal;
import org.jgrapht.util.HeuristicToGoal;

import tt.discrete.vis.TrajectoryLayer;
import tt.discrete.vis.TrajectoryLayer.TrajectoryProvider;
import tt.euclid2i.discretization.LazyGrid;
import tt.euclid2i.trajectory.LineSegmentsConstantSpeedTrajectory;
import tt.euclid2i.trajectory.StraightSegmentTrajectory;
import tt.euclidtime3i.Point;
import tt.euclidtime3i.Region;
import tt.euclidtime3i.Trajectory;
import tt.euclidtime3i.discretization.ConstantSpeedTimeExtension;
import tt.euclidtime3i.discretization.SeparationSoftConstraintWrapper;
import tt.euclidtime3i.discretization.Straight;
import tt.euclidtime3i.region.MovingCircle;
import tt.euclidtime3i.trajectory.Trajectories;
import tt.euclidtime3i.vis.RegionsLayer;
import tt.euclidtime3i.vis.RegionsLayer.RegionsProvider;
import tt.euclidtime3i.vis.TimeParameter;
import tt.euclidtime3i.vis.TimeParameterProjectionTo2d;
import tt.vis.GraphLayer;
import tt.vis.GraphLayer.GraphProvider;
import tt.vis.GraphPathLayer;
import tt.vis.GraphPathLayer.PathProvider;
import tt.vis.ParameterControlLayer;
import cz.agents.alite.creator.Creator;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.VisManager.SceneParams;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;


public class SeparationSoftconstraintDemoCreator implements Creator {

    @Override
    public void init(String[] args) {}

    @Override
    public void create() {

        initVisualization();

        // create time parameter
        final TimeParameter time = new TimeParameter();

        VisManager.registerLayer(ParameterControlLayer.create(time));

        // create discretization
        final DirectedGraph<tt.euclid2i.Point, tt.euclid2i.Line> spatialGraph
            = new LazyGrid(new tt.euclid2i.Point(0,0),
                    new LinkedList<tt.euclid2i.Region>(),
                    new tt.euclid2i.region.Rectangle(new tt.euclid2i.Point(-200,-200),
                    new tt.euclid2i.Point(200,200)),
                    LazyGrid.PATTERN_8_WAY,
                    25);

        // create dynamic obstacles
        final LinkedList<Region> dynamicObstacles = new LinkedList<Region>();
        dynamicObstacles.add(createMovingObstacle(spatialGraph, new tt.euclid2i.Point(-100,0), new tt.euclid2i.Point(100,0), 75));

        VisManager.registerLayer(RegionsLayer.create(new RegionsProvider() {

            @Override
            public Collection<Region> getRegions() {
                return dynamicObstacles;
            }
        }, new TimeParameterProjectionTo2d(time), Color.RED, Color.GRAY));


        // create spatio-temporal graph
        ConstantSpeedTimeExtension spatioTemporalGraph
            = new ConstantSpeedTimeExtension(spatialGraph, 5000, new int[]{1});

        // Add soft-constraint
        SeparationSoftConstraintWrapper<Point, Straight> graphWithSoftConstraints
            = new SeparationSoftConstraintWrapper<Point, Straight>(spatioTemporalGraph, dynamicObstacles, 11);

        final GraphPath<Point, Straight> path = AStarShortestPath
                .findPathBetween(graphWithSoftConstraints, new HeuristicToGoal<Point>() {
                            @Override
                            public double getCostToGoalEstimate(Point current) {
                                return (current.getPosition())
                                        .distance(new tt.euclid2i.Point(0, 150));
                            }
                        }, new Point(0, -100, 0), new Goal<Point>() {
                            @Override
                            public boolean isGoal(Point current) {
                                return current.x == 0 && current.y == 150;
                            }
                        }
                );

        final Trajectory trajectory = Trajectories.convertFromEuclid2iTrajectory(new StraightSegmentTrajectory<Point, Straight>(path, path.getEndVertex().getTime()));

        // graph
        VisManager.registerLayer(GraphLayer.create(new GraphProvider<tt.euclid2i.Point, tt.euclid2i.Line>() {

            @Override
            public Graph<tt.euclid2i.Point, tt.euclid2i.Line> getGraph() {
                return ((LazyGrid) spatialGraph).generateFullGraph();
            }
        }, new tt.euclid2i.vis.ProjectionTo2d(), new Color(250,250,250), Color.LIGHT_GRAY, 1, 4));

        // draw trajectory
        VisManager.registerLayer(TrajectoryLayer.create(new TrajectoryProvider<Point>() {

            @Override
            public tt.discrete.Trajectory<Point> getTrajectory() {
                return trajectory;
            }
        }, new TimeParameterProjectionTo2d(time), Color.BLUE, 1, 100, 's'));

        // draw the shortest path
        VisManager.registerLayer(GraphPathLayer.create(new PathProvider<Point, Straight>() {

            @Override
            public GraphPath<Point, Straight> getPath() {
                return path;
            }

        },  new TimeParameterProjectionTo2d(time),Color.BLUE, Color.BLUE, 2, 8));
    }

    private Region createMovingObstacle(
            Graph<tt.euclid2i.Point, tt.euclid2i.Line> graph,
            final tt.euclid2i.Point start, final tt.euclid2i.Point target,
            int radius) {

        GraphPath<tt.euclid2i.Point, tt.euclid2i.Line> path = AStarShortestPath
                .findPathBetween(graph, new HeuristicToGoal<tt.euclid2i.Point>() {

                    @Override
                    public double getCostToGoalEstimate(tt.euclid2i.Point current) {
                        return current.distance(target);
                    }

                }, start, target);

        final tt.euclid2i.Trajectory trajectory = new LineSegmentsConstantSpeedTrajectory<tt.euclid2i.Point, tt.euclid2i.Line>(
                0, path, 1, (int) path.getWeight());

        return new MovingCircle(trajectory, radius, (int) radius / 4);
    }

    private void initVisualization() {
        VisManager.setInitParam("Trajectory Tools Vis", 1024, 768);
        VisManager.setSceneParam(new SceneParams(){

            @Override
            public Point2d getDefaultLookAt() {
                return new Point2d(0,0);
            }

            @Override
            public double getDefaultZoomFactor() {
                return 1;
            } } );

        VisManager.init();

        // background
        VisManager.registerLayer(ColorLayer.create(Color.WHITE));

        // Overlay
        VisManager.registerLayer(VisInfoLayer.create());
    }




    public static void main(String[] args) {
        new SeparationSoftconstraintDemoCreator().create();
    }

}
