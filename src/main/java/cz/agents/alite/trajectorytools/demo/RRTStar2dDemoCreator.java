package cz.agents.alite.trajectorytools.demo;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.LinkedList;

import org.jgrapht.GraphPath;

import cz.agents.alite.creator.Creator;
import cz.agents.alite.trajectorytools.graph.spatial.maneuvers.SpatialManeuver;
import cz.agents.alite.trajectorytools.graph.spatial.region.BoxRegion;
import cz.agents.alite.trajectorytools.graph.spatial.region.Region;
import cz.agents.alite.trajectorytools.graph.spatial.rrtstar.SpatialStraightLineDomain;
import cz.agents.alite.trajectorytools.planner.rrtstar.Domain;
import cz.agents.alite.trajectorytools.planner.rrtstar.RRTStarPlanner;
import cz.agents.alite.trajectorytools.trajectory.ManeuverTrajectory;
import cz.agents.alite.trajectorytools.trajectory.Trajectory;
import cz.agents.alite.trajectorytools.util.Point;
import cz.agents.alite.trajectorytools.vis.RRTStarLayer;
import cz.agents.alite.trajectorytools.vis.RegionsLayer;
import cz.agents.alite.trajectorytools.vis.RegionsLayer.RegionsProvider;
import cz.agents.alite.trajectorytools.vis.TrajectoryLayer;
import cz.agents.alite.trajectorytools.vis.TrajectoryLayer.TrajectoryProvider;
import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;


public class RRTStar2dDemoCreator implements Creator {

    RRTStarPlanner<Point, SpatialManeuver> rrtstar;

    Point initialPoint = new Point(100, 100, 0);
    BoxRegion bounds = new BoxRegion(new Point(0, 0, 0), new Point(1000, 1000, 1000));
    Collection<Region> obstacles = new LinkedList<Region>();
    Region target = new BoxRegion(new Point(750, 550, -1000), new Point(800, 600, 1000));

    Trajectory trajectory = null;

    @Override
    public void init(String[] args) {

    }

    @Override
    public void create() {
        Region obstacle = new BoxRegion(new Point(250, 250, 0), new Point(750,750,750));
        obstacles.add(obstacle);


        Domain<Point, SpatialManeuver> domain = new SpatialStraightLineDomain(bounds, obstacles, target, 1.0);
        rrtstar = new RRTStarPlanner<Point, SpatialManeuver>(domain, initialPoint, 1300);
        createVisualization();

        int n=100000;
        for (int i=0; i<n; i++) {
            rrtstar.iterate();

            if (rrtstar.getBestVertex() != null) {
                //System.out.println("Best vertex: " + rrtstar.getBestVertex());
                GraphPath<Point, SpatialManeuver> path = rrtstar.getBestPath();
                trajectory =  new ManeuverTrajectory<Point, SpatialManeuver>(0.0, path, path.getWeight());
                // trajectory =  new SampledTrajectory(trajectory, 100);
            }

            /*
            try {
                Thread.sleep(100);
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
        VisManager.registerLayer(RRTStarLayer.create(rrtstar, Color.GRAY, Color.GRAY, 1, 4));

        VisManager.registerLayer(RegionsLayer.create(new RegionsProvider() {

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
        }, Color.BLUE, 10, 2000.0, 't'));


        // Overlay
        VisManager.registerLayer(VisInfoLayer.create());
    }

  }
