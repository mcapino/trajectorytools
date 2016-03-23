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

    @Override
    public void init(String[] args) {
    }

    @Override
    public void create() {

        initVisualization();

        final Polygon footprint = (new Rectangle(new Point(-1000, -900), new Point(3000, 900))).toPolygon();

        List<Region> obstacles = new LinkedList<>();

        for (int i = 0; i < 6; i++) {
            int ystart = -4500;
            int yspacing = 4000;
            int size = 600;

            obstacles.add(new Rectangle(1500, ystart + i*yspacing - size/2, 1500+size, ystart + i*yspacing + size/2));
            obstacles.add(new Rectangle(-5000, ystart + i*yspacing - size/2, -5000+size, ystart + i*yspacing + size/2));


        }


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
        new ParkingDemo().create();
    }

}
