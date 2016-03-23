package tt.euclidyaw3i.demo;

import cz.agents.alite.creator.Creator;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.VisManager.SceneParams;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.AStarShortestPath;
import org.jgrapht.util.HeuristicToGoal;
import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.discretization.LazyGrid;
import tt.euclid2i.region.Circle;
import tt.euclid2i.region.Polygon;
import tt.euclid2i.region.Rectangle;
import tt.euclid2i.vis.ProjectionTo2d;
import tt.euclid2i.vis.RegionsLayer;
import tt.euclid2i.vis.RegionsLayer.RegionsProvider;
import tt.euclidyaw3i.vis.AxisLayer;
import tt.euclidyaw3i.vis.FootPrintLayer;
import tt.vis.GraphLayer;
import tt.vis.GraphLayer.GraphProvider;
import tt.vis.GraphPathLayer;
import tt.vis.GraphPathLayer.PathProvider;

import javax.vecmath.Point2d;
import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class ParkingDemo implements Creator {

    private final Polygon carFootprint = (new Rectangle(new Point(-1000, -900), new Point(3000, 900))).toPolygon();
    private final Polygon duckiebotFootprint = (new Rectangle(new Point(-1600, -900), new Point(700, 900))).toPolygon();
    List<Polygon> obstacles = new LinkedList<>();

    @Override
    public void init(String[] strings) {}

    public ParkingDemo() {
        initVisualization();

        obstacles.addAll(createColumns());
        // create other cars
        obstacles.add(carFootprint.getRotated(new Point(0,0), 0.1f).getTranslated(new Point(3200, 1500)));
        obstacles.add(carFootprint.getRotated(new Point(0,0), -0.05f).getTranslated(new Point(2600, 9300)));
    }

    public void obstaclesDemo() {
        final Polygon footprint = duckiebotFootprint;
        VisManager.registerLayer(RegionsLayer.create(() -> obstacles, Color.BLACK, Color.BLACK));
    }

    public void minkowksiDemo() {
        List<Polygon> minkowskiObstacles = new LinkedList<>();

        float[] angleHolder = new float[1];

        VisManager.registerLayer(RegionsLayer.create(() -> minkowskiObstacles, Color.LIGHT_GRAY, Color.LIGHT_GRAY));
        VisManager.registerLayer(RegionsLayer.create(() -> obstacles, Color.BLACK, Color.BLACK));

        VisManager.registerLayer(FootPrintLayer.create(duckiebotFootprint, () ->
                        Collections.singleton(new tt.euclidyaw3i.Point(0,0,angleHolder[0])),
                Color.RED, null));

        for (int i=0; i < 1000; i++) {

            float angle = (float) ((((i % 100)-50) / 100.0) * 2 * Math.PI);
            Polygon footprint = duckiebotFootprint.getRotated(new tt.euclid2i.Point (0,0),angle);
            angleHolder[0] = angle;

            minkowskiObstacles.clear();
            for (Polygon obstacle : obstacles) {
                minkowskiObstacles.add(obstacle.minkowskiSum(footprint));
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void configurationSpaceDemo() {
        final Polygon footprint = duckiebotFootprint;
        VisManager.registerLayer(FootPrintLayer.create(footprint, () ->
                Collections.singleton(new tt.euclidyaw3i.Point(0, 0, 0)),
                Color.RED, null));

        VisManager.registerLayer(FootPrintLayer.create(footprint, () ->
                        Collections.singleton(new tt.euclidyaw3i.Point(5000, 3000, (float) Math.PI / 4)),
                Color.RED, null));

        VisManager.registerLayer(FootPrintLayer.create(footprint, () ->
                        Collections.singleton(new tt.euclidyaw3i.Point(0, 5000, (float) Math.PI)),
                Color.RED, null));
    }

    @Override
    public void create() {
        final Polygon footprint = duckiebotFootprint;

        tt.euclidyaw3i.Point initConf = new tt.euclidyaw3i.Point(0, 0, (float) Math.PI/2);
        tt.euclidyaw3i.Point goalConf = new tt.euclidyaw3i.Point(4000, 5500, (float) 0);

        VisManager.registerLayer(FootPrintLayer.create(footprint, () -> Collections.singleton(initConf),
                Color.BLACK, Color.RED));

        VisManager.registerLayer(FootPrintLayer.create(footprint, () -> Collections.singleton(goalConf),
                Color.RED, null));

        VisManager.registerLayer(RegionsLayer.create(() -> obstacles, Color.BLACK, Color.BLACK));

//        // Show initial configuration
//        VisManager.registerLayer(RegionsLayer.create(new RegionsProvider() {
//            @Override
//            public Collection<? extends Region> getRegions() {
//                Polygon r = footprint.getTranslated(initConf.getPos());
//                r = r.getRotated(new Point(0,0), initConf.getYawInRads());
//                return Collections.singleton(r);
//            }
//        }, Color.RED, null));
//
//        // Show goal configuration
//        VisManager.registerLayer(RegionsLayer.create(new RegionsProvider() {
//            @Override
//            public Collection<? extends Region> getRegions() {
//                Polygon r = footprint.getRotated(new Point(0,0), goalConf.getYawInRads());
//                r = r.getTranslated(goalConf.getPos());
//                return Collections.singleton(r);
//            }
//        }, Color.BLUE, null));

//        // plan the shortest path between these two points
//        final Point start = new Point(-5, -35);
//        final Point goal = new Point(0, 30);
//
//        // obstacle to avoid
//        Region obstacle = new Circle(new Point(0, 0), 22);
//        final Collection<Region> obstacles = Arrays.asList(new Region[] {obstacle});
//
//        // create grid discretization
//        // Note that this is "lazy" implementation, i.e. the graph is not stored in memory,
//        // but instead edges are computed on request
//        final DirectedGraph<Point, Line> graph = new LazyGrid(start,
//                obstacles, new Rectangle(new Point(-50, -50),
//                        new Point(50, 50)), LazyGrid.PATTERN_8_WAY, 5);
//
//        // or  a roadmap...
//        //        Rectangle bounds = new Rectangle(new Point(-100, -100), new Point(200,200));
//        //        final DirectedGraph<Point, Line> graph = new ProbabilisticRoadmap(1000, 14, new Point[] {start, goal}, bounds, obstacles , new Random(1));
//
//
//
//        // visualize graph
//        VisManager.registerLayer(GraphLayer.create(new GraphProvider<Point, Line>() {
//
//            @Override
//            public Graph<Point, Line> getGraph() {
//                if (graph instanceof LazyGrid) {
//                    return ((LazyGrid) graph).generateFullGraph();
//                } else {
//                    return graph;
//                }
//            }
//        }, new ProjectionTo2d(), Color.GRAY, Color.GRAY, 1, 4));
//
//        // visualize obstacles
//        VisManager.registerLayer(RegionsLayer.create(new RegionsProvider() {
//
//			@Override
//			public Collection<? extends Region> getRegions() {
//				return obstacles;
//			}
//		}, Color.BLACK));
//
//        // run A* on the graph
//        final GraphPath<Point, Line> pathBetween = AStarShortestPath.findPathBetween(graph, new HeuristicToGoal<Point>() {
//
//            @Override
//            public double getCostToGoalEstimate(Point current) {
//                return current.distance(goal);
//            }
//        }, start, goal);
//
//        // visualize the shortest path
//        VisManager.registerLayer(GraphPathLayer.create(new PathProvider<Point, Line>() {
//
//            @Override
//            public GraphPath<Point, Line> getPath() {
//                return pathBetween;
//            }
//
//        }, new ProjectionTo2d(), Color.RED, Color.RED, 2, 4));


    }




    private List<Polygon> createColumns() {
        List<Polygon> obstacles = new LinkedList<>();

        for (int i = 0; i < 6; i++) {
            int ystart = -4500;
            int yspacing = 4000;
            int size = 600;

            obstacles.add((new Rectangle(1500, ystart + i*yspacing - size/2, 1500+size, ystart + i*yspacing + size/2)).toPolygon());
            obstacles.add((new Rectangle(-5000, ystart + i*yspacing - size/2, -5000+size, ystart + i*yspacing + size/2)).toPolygon());
        }
        return obstacles;
    }

    private void initVisualization() {
        VisManager.setInitParam("Trajectory Tools Vis", 1024, 768, 30000, 30000);
        VisManager.setSceneParam(new SceneParams() {

            @Override
            public Point2d getDefaultLookAt() {
                return new Point2d(0, 0);
            }

            @Override
            public double getDefaultZoomFactor() {
                return 0.1;
            }
        });

        VisManager.init();
        VisManager.setInvertYAxis(true);

        // background
        VisManager.registerLayer(ColorLayer.create(Color.WHITE));

        // Overlay
        VisManager.registerLayer(VisInfoLayer.create());

        VisManager.registerLayer(AxisLayer.create(1000));
    }


    public static void main(String[] args) {
        new ParkingDemo().minkowksiDemo();   }

}
