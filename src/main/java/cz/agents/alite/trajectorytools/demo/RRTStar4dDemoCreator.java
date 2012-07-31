package cz.agents.alite.trajectorytools.demo;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

import org.jgrapht.GraphPath;

import cz.agents.alite.creator.Creator;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.maneuvers.SpatioTemporalManeuver;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.region.Box4dRegion;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.region.Region;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.region.StaticBoxRegion;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.rrtstar.SpatioTemporalStraightLineDomain;
import cz.agents.alite.trajectorytools.planner.rrtstar.Domain;
import cz.agents.alite.trajectorytools.planner.rrtstar.RRTStarPlanner;
import cz.agents.alite.trajectorytools.trajectory.SpatioTemporalManeuverTrajectory;
import cz.agents.alite.trajectorytools.trajectory.Trajectory;
import cz.agents.alite.trajectorytools.util.Point;
import cz.agents.alite.trajectorytools.util.TimePoint;
import cz.agents.alite.trajectorytools.vis.RRTStarLayer;
import cz.agents.alite.trajectorytools.vis.Regions4dLayer;
import cz.agents.alite.trajectorytools.vis.Regions4dLayer.RegionsProvider;
import cz.agents.alite.trajectorytools.vis.TrajectoryLayer;
import cz.agents.alite.trajectorytools.vis.TrajectoryLayer.TrajectoryProvider;
import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;


public class RRTStar4dDemoCreator implements Creator {

    RRTStarPlanner<TimePoint, SpatioTemporalManeuver> rrtstar;

    TimePoint initialPoint = new TimePoint(100, 100, 0, 0);
    Box4dRegion bounds = new Box4dRegion(new TimePoint(0, 0, 0, 0), new TimePoint(1000, 1000, 1000, 100));
    Collection<Region> obstacles = new LinkedList<Region>();
    Region target = new StaticBoxRegion(new Point(500, 850, -1000), new Point(600, 870, 1000));

    Trajectory trajectory = null;

    double gamma = 1300;

    @Override
    public void init(String[] args) {

    }

    @Override
    public void create() {
        obstacles.add(new StaticBoxRegion(new Point(250, 250, 0), new Point(750,750,750)));

        //obstacles.add(new BoxRegion(new Point(100, 100, 0), new Point(200,200,750)));

        obstacles.add(new StaticBoxRegion(new Point(100, 200, 0), new Point(230,950,750)));


        Domain<TimePoint, SpatioTemporalManeuver> domain = new SpatioTemporalStraightLineDomain(bounds, obstacles, target, 12,15,25, new Random(1));
        rrtstar = new RRTStarPlanner<TimePoint, SpatioTemporalManeuver>(domain, initialPoint, gamma);
        createVisualization();

        int n=300;
        for (int i=0; i<n; i++) {
            rrtstar.iterate();

            if (rrtstar.getBestVertex() != null) {
                //System.out.println("Best vertex: " + rrtstar.getBestVertex());
                GraphPath<TimePoint, SpatioTemporalManeuver> path = rrtstar.getBestPath();
                trajectory = new SpatioTemporalManeuverTrajectory<TimePoint, SpatioTemporalManeuver>(path, path.getWeight());
                //trajectory =  new SampledTrajectory(trajectory, 100);
            }

            /*
            try {
                Thread.sleep(50);
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

        VisManager.registerLayer(Regions4dLayer.create(new RegionsProvider() {

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
        }, Color.BLUE, 1, 2000.0, 't'));


        // Overlay
        VisManager.registerLayer(VisInfoLayer.create());
    }

  }
