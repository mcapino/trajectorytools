package cz.agents.alite.trajectorytools.demo;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.LinkedList;

import javax.vecmath.Point2d;

import org.jgrapht.GraphPath;

import cz.agents.alite.creator.Creator;
import cz.agents.alite.trajectorytools.graph.spatial.maneuvers.SpatialManeuver;
import cz.agents.alite.trajectorytools.graph.spatial.region.BoxRegion;
import cz.agents.alite.trajectorytools.graph.spatial.region.Region;
import cz.agents.alite.trajectorytools.graph.spatial.rrtstar.SpatialStraightLineDomain;
import cz.agents.alite.trajectorytools.planner.rrtstar.Domain;
import cz.agents.alite.trajectorytools.planner.rrtstar.RRTStarPlanner;
import cz.agents.alite.trajectorytools.trajectory.SpatialManeuverTrajectory;
import cz.agents.alite.trajectorytools.trajectory.Trajectory;
import cz.agents.alite.trajectorytools.util.SpatialPoint;
import cz.agents.alite.trajectorytools.util.TimePoint;
import cz.agents.alite.trajectorytools.vis.RRTStarLayer;
import cz.agents.alite.trajectorytools.vis.Regions3dLayer;
import cz.agents.alite.trajectorytools.vis.Regions3dLayer.RegionsProvider;
import cz.agents.alite.trajectorytools.vis.TrajectoryLayer;
import cz.agents.alite.trajectorytools.vis.TrajectoryLayer.TrajectoryProvider;
import cz.agents.alite.trajectorytools.vis.projection.DefaultProjection;
import cz.agents.alite.trajectorytools.vis.projection.ProjectionTo2d;
import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;


public class RRTStar2dDemoCreator implements Creator {

    RRTStarPlanner<SpatialPoint, SpatialManeuver> rrtstar;

    SpatialPoint initialPoint = new SpatialPoint(100, 100, 0);
    BoxRegion bounds = new BoxRegion(new SpatialPoint(0, 0, 0), new SpatialPoint(1000, 1000, 1000));
    Collection<Region> obstacles = new LinkedList<Region>();
    Region target = new BoxRegion(new SpatialPoint(500, 850, -1000), new SpatialPoint(600, 870, 1000));

    Trajectory trajectory = null;

    @Override
    public void init(String[] args) {

    }

    @Override
    public void create() {
        obstacles.add(new BoxRegion(new SpatialPoint(250, 250, 0), new SpatialPoint(750,750,750)));

        //obstacles.add(new BoxRegion(new Point(100, 100, 0), new Point(200,200,750)));

        obstacles.add(new BoxRegion(new SpatialPoint(100, 200, 0), new SpatialPoint(230,950,750)));


        Domain<SpatialPoint, SpatialManeuver> domain = new SpatialStraightLineDomain(bounds, obstacles, target, 1.0);
        rrtstar = new RRTStarPlanner<SpatialPoint, SpatialManeuver>(domain, initialPoint, 1300);
        createVisualization();

        int n=100000;
        for (int i=0; i<n; i++) {
            rrtstar.iterate();

            if (rrtstar.getBestVertex() != null) {
                //System.out.println("Best vertex: " + rrtstar.getBestVertex());
                GraphPath<SpatialPoint, SpatialManeuver> path = rrtstar.getBestPath();
                trajectory = new SpatialManeuverTrajectory<SpatialPoint, SpatialManeuver>(0.0, path, path.getWeight());
                //trajectory =  new SampledTrajectory(trajectory, 100);
            }

            /*
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }

    }

    private void createVisualization() {
        VisManager.setInitParam("Trajectory Tools Vis", 1024, 768, 2000, 2000);
        VisManager.setPanningBounds(new Rectangle(-2000, -2000, 5000, 5000));
        VisManager.init();

        Vis.setPosition(50, 50, 1);

        // background
        VisManager.registerLayer(ColorLayer.create(Color.WHITE));

        // graph
        VisManager.registerLayer(RRTStarLayer.create(rrtstar, new DefaultProjection<SpatialPoint>(),  Color.GRAY, Color.GRAY, 1, 4));

        VisManager.registerLayer(Regions3dLayer.create(new RegionsProvider() {

            @Override
            public Collection<Region> getRegions() {
                LinkedList<Region> regions = new LinkedList<Region>();
                regions.add(bounds);
                regions.addAll(obstacles);
                regions.add(target);
                return regions;
            }
        }, Color.BLACK, 1));

        VisManager.registerLayer(TrajectoryLayer.create(new TrajectoryProvider() {

            @Override
            public Trajectory getTrajectory() {
                return trajectory;
            }
        }, new ProjectionTo2d<TimePoint>() {

            @Override
            public Point2d project(TimePoint point) {
                return new Point2d(point.x, point.y);
            }
        }, Color.BLUE, 10, 2000.0, 't'));


        // Overlay
        VisManager.registerLayer(VisInfoLayer.create());
    }

  }
