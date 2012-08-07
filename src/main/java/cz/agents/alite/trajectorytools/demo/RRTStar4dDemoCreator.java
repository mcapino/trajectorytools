package cz.agents.alite.trajectorytools.demo;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

import javax.vecmath.Point2d;

import org.jgrapht.GraphPath;

import cz.agents.alite.creator.Creator;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.maneuvers.SpatioTemporalManeuver;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.maneuvers.Straight;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.region.Box4dRegion;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.region.Region;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.region.StaticBoxRegion;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.region.StaticSphereRegion;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.rrtstar.GuidedStraightLineDomain;
import cz.agents.alite.trajectorytools.planner.rrtstar.Domain;
import cz.agents.alite.trajectorytools.planner.rrtstar.RRTStarPlanner;
import cz.agents.alite.trajectorytools.trajectory.SpatioTemporalManeuverTrajectory;
import cz.agents.alite.trajectorytools.trajectory.Trajectory;
import cz.agents.alite.trajectorytools.util.SpatialPoint;
import cz.agents.alite.trajectorytools.util.TimePoint;
import cz.agents.alite.trajectorytools.vis.RRTStarLayer;
import cz.agents.alite.trajectorytools.vis.Regions4dLayer;
import cz.agents.alite.trajectorytools.vis.Regions4dLayer.RegionsProvider;
import cz.agents.alite.trajectorytools.vis.TrajectoryLayer;
import cz.agents.alite.trajectorytools.vis.TrajectoryLayer.TrajectoryProvider;
import cz.agents.alite.trajectorytools.vis.projection.ProjectionTo2d;
import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;


public class RRTStar4dDemoCreator implements Creator {

    RRTStarPlanner<TimePoint, SpatioTemporalManeuver> rrtstar;

    TimePoint initialPoint = new TimePoint(100, 100, 50, 0);
    Box4dRegion bounds = new Box4dRegion(new TimePoint(0, 0, 0, 0), new TimePoint(1000, 1000, 150, 200));
    Collection<Region> obstacles = new LinkedList<Region>();
    SpatialPoint target = new SpatialPoint(900, 760, 80);
    double targetReachedTolerance = 5;
    Region targetRegion =	new StaticSphereRegion(target, targetReachedTolerance);

    Trajectory trajectory = null;

    double gamma = 1300;

    @Override
    public void init(String[] args) {

    }

    @Override
    public void create() {
        //obstacles.add(new StaticBoxRegion(new SpatialPoint(250, 250, 0), new SpatialPoint(750,750,bounds.getCorner2().z*0.9)));

        //obstacles.add(new BoxRegion(new Point(100, 100, 0), new Point(200,200,750)));

        //obstacles.add(new StaticSphereRegion(new SpatialPoint(400, 100, 0),80));
        //obstacles.add(new StaticBoxRegion(new SpatialPoint(100, 200, 0), new SpatialPoint(230,950,bounds.getCorner2().z)));

        // Add obstacles

        for(int x=5; x < 20; x++) {
            for (int y=5; y < 20; y++) {
                int z = (int) (Math.random() * bounds.getCorner2().z / 2);
                obstacles.add(new StaticBoxRegion(new SpatialPoint(x*50, y*50, z), new SpatialPoint(x*50+30, y*50+30, z + bounds.getCorner2().z/2)));
            }
        }


        Domain<TimePoint, SpatioTemporalManeuver> domain = new GuidedStraightLineDomain(bounds, initialPoint, obstacles, target, targetReachedTolerance, 12,15,30, 45, 50,  new Random(1));
        rrtstar = new RRTStarPlanner<TimePoint, SpatioTemporalManeuver>(domain, initialPoint, gamma);
        createVisualization();

        double bestCost = Double.POSITIVE_INFINITY;
        int n=100000;
        for (int i=0; i<n; i++) {
            rrtstar.iterate();

            if (rrtstar.getBestVertex() != null && rrtstar.getBestVertex().getCostFromRoot() < bestCost) {
                bestCost = rrtstar.getBestVertex().getCostFromRoot();
                System.out.println("Iteration: " + i + " Best path cost: " + bestCost);
                GraphPath<TimePoint, SpatioTemporalManeuver> path = rrtstar.getBestPath();
                trajectory = new SpatioTemporalManeuverTrajectory<TimePoint, SpatioTemporalManeuver>(path, path.getWeight());
                //trajectory =  new SampledTrajectory(trajectory, 100);

                for (SpatioTemporalManeuver maneuver : path.getEdgeList()) {
                    if (maneuver instanceof Straight) {
                        Straight straight = (Straight) maneuver;
                        System.out.println(straight);
                    }
                }

            }

            /*
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }

    }

    private void createVisualization() {
        VisManager.setInitParam("Trajectory Tools Vis", 1024, 768, 4000, 4000);
        VisManager.setPanningBounds(new Rectangle(-4000, -4000, 10000, 10000));
        VisManager.init();

        Vis.setPosition(50, 50, 1);

        // background
        VisManager.registerLayer(ColorLayer.create(Color.WHITE));



        // X-Y TOP View ////////////////////////////////////////////////////////////////

        createView( new ProjectionTo2d<TimePoint>() {

            @Override
            public Point2d project(TimePoint point) {
                return new Point2d(point.x, point.y);
            }
        });


        // X-Z SIDE View ///////////////////////////////////////////////////////////////

        createView( new ProjectionTo2d<TimePoint>() {

            @Override
            public Point2d project(TimePoint point) {
                return new Point2d(point.x, -point.z + 1200);
            }
        });

        // Y-Z SIDE View ///////////////////////////////////////////////////////////////

        createView( new ProjectionTo2d<TimePoint>() {

            @Override
            public Point2d project(TimePoint point) {
                return new Point2d(point.z+1050, point.y);
            }
        });

        // Y-T View ///////////////////////////////////////////////////////////////

        createView( new ProjectionTo2d<TimePoint>() {

            @Override
            public Point2d project(TimePoint point) {
                return new Point2d(-point.w - 50, point.y);
            }
        });


        // X-T View ///////////////////////////////////////////////////////////////

        createView( new ProjectionTo2d<TimePoint>() {

            @Override
            public Point2d project(TimePoint point) {
                return new Point2d(point.x, -point.w - 50);
            }
        });

        ///////////////////////////////////////////////////////////////////////////

        // Overlay
        VisManager.registerLayer(VisInfoLayer.create());
    }

    private void createView(ProjectionTo2d<TimePoint> projection) {
        // graph
        VisManager.registerLayer(RRTStarLayer.create(rrtstar, projection, Color.GRAY, Color.GRAY, 1, 4));

        VisManager.registerLayer(TrajectoryLayer.create(new TrajectoryProvider() {

            @Override
            public Trajectory getTrajectory() {
                return trajectory;
            }
        }, projection, Color.BLUE, 0.5, bounds.getCorner2().w, 't'));

        VisManager.registerLayer(Regions4dLayer.create(new RegionsProvider() {

            @Override
            public Collection<Region> getRegions() {
                LinkedList<Region> regions = new LinkedList<Region>();
                regions.add(bounds);
                regions.addAll(obstacles);
                regions.add(targetRegion);
                return regions;
            }
        },
        projection,
        Color.BLACK, 1));
    }



  }