package tt.euclidtime3i.demo;

import java.awt.Color;
import java.lang.invoke.ConstantCallSite;
import java.util.Collection;
import java.util.LinkedList;

import javax.vecmath.Point2d;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.SingleEdgeGraphPath;
import org.jgrapht.alg.AStarShortestPath;
import org.jgrapht.alg.AStarShortestPathSimple;
import org.jgrapht.util.Goal;
import org.jgrapht.util.Heuristic;

import tt.euclid2i.Trajectory;
import tt.euclid2i.discretization.LazyGrid;
import tt.euclid2i.discretization.LineSegmentConstantSpeedTrajectory;
import tt.euclidtime3i.Point;
import tt.euclidtime3i.discretization.Straight;
import tt.euclidtime3i.discretization.TimeExtendedGraph;
import tt.euclidtime3i.region.MovingCircle;
import tt.euclidtime3i.region.Region;
import tt.euclidtime3i.vis.RegionsLayer;
import tt.euclidtime3i.vis.RegionsLayer.RegionsProvider;
import tt.euclidtime3i.vis.SpatialProjectionTo2d;
import tt.euclidtime3i.vis.TimeParameter;
import tt.euclidtime3i.vis.TimeParameterProjectionTo2d;
import tt.vis.GraphLayer;
import tt.vis.GraphLayer.GraphProvider;
import tt.vis.GraphPathLayer;
import tt.vis.GraphPathLayer.PathProvider;
import tt.vis.ParameterControlLayer;
import tt.vis.TrajectoryLayer;
import tt.vis.TrajectoryLayer.TrajectoryProvider;
import cz.agents.alite.creator.Creator;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.VisManager.SceneParams;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;


public class PathfindingDemoCreator implements Creator {

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
        			new LinkedList<tt.euclid2i.region.Region>(),
        			new tt.euclid2i.region.Rectangle(new tt.euclid2i.Point(-50,-50),
        			new tt.euclid2i.Point(50,50)),
        			10);

        // create dynamic obstacles
        final LinkedList<Region> dynamicObstacles = new LinkedList<Region>();
        dynamicObstacles.add(createMovingObstacle(spatialGraph, new tt.euclid2i.Point(-10,0), new tt.euclid2i.Point(10,0), 5));

        VisManager.registerLayer(RegionsLayer.create(new RegionsProvider() {

			@Override
			public Collection<Region> getRegions() {
				return dynamicObstacles;
			}
		}, new TimeParameterProjectionTo2d(time), Color.RED, Color.GRAY));


        // create spatio-temporal graph
        TimeExtendedGraph spatioTemporalGraph = new TimeExtendedGraph(spatialGraph, 50, new int[]{1,2}, dynamicObstacles);

		final GraphPath<Point, Straight> path = AStarShortestPath
				.findPathBetween(spatioTemporalGraph, new Heuristic<Point>() {
					@Override
					public double getCostToGoalEstimate(Point current) {
						return (current.getPosition())
								.distance(new tt.euclid2i.Point(0, 10));
					}
				}, new Point(0, -10, 0), new Goal<Point>() {
					@Override
					public boolean isGoal(Point current) {
						return current.x == 0 && current.y == 10;
					}
				});

        // graph
        VisManager.registerLayer(GraphLayer.create(new GraphProvider<tt.euclid2i.Point, tt.euclid2i.Line>() {

            @Override
            public Graph<tt.euclid2i.Point, tt.euclid2i.Line> getGraph() {
                return ((LazyGrid) spatialGraph).generateFullGraph();
            }
        }, new tt.euclid2i.vis.ProjectionTo2d(), Color.GRAY, Color.GRAY, 1, 4));

        // draw the shortest path
        VisManager.registerLayer(GraphPathLayer.create(new PathProvider<Point, Straight>() {

            @Override
            public GraphPath<Point, Straight> getPath() {
                return path;
            }

        },  new TimeParameterProjectionTo2d(time),Color.RED, Color.RED, 2, 8));
    }

	private Region createMovingObstacle(
			Graph<tt.euclid2i.Point, tt.euclid2i.Line> graph,
			final tt.euclid2i.Point start, final tt.euclid2i.Point target,
			int radius) {

		GraphPath<tt.euclid2i.Point, tt.euclid2i.Line> path = AStarShortestPath
				.findPathBetween(graph, new Heuristic<tt.euclid2i.Point>() {

					@Override
					public double getCostToGoalEstimate(tt.euclid2i.Point current) {
						return current.distance(target);
					}
				}, start, target);

		final Trajectory trajectory = new LineSegmentConstantSpeedTrajectory<tt.euclid2i.Point, tt.euclid2i.Line>(
				0, path, 1, path.getWeight());

		VisManager.registerLayer(TrajectoryLayer.create(
				new TrajectoryProvider<tt.euclid2i.Point>() {
					@Override
					public tt.Trajectory<tt.euclid2i.Point> getTrajectory() {
						return trajectory;
					}
				}, new tt.euclid2i.vis.ProjectionTo2d(), Color.RED,
				1.0,
				100,
				't'));


		return new MovingCircle(trajectory, radius, (int) radius / 4);
	}

	private void initVisualization() {
        VisManager.setInitParam("Trajectory Tools Vis", 1024, 768, 200, 200);
        VisManager.setSceneParam(new SceneParams(){

            @Override
            public Point2d getDefaultLookAt() {
                return new Point2d(0,0);
            }

            @Override
            public double getDefaultZoomFactor() {
                return 5;
            } } );

        VisManager.init();

        // background
        VisManager.registerLayer(ColorLayer.create(Color.WHITE));

        // Overlay
        VisManager.registerLayer(VisInfoLayer.create());
    }


    public static void main(String[] args) {
        new PathfindingDemoCreator().create();
    }

}
