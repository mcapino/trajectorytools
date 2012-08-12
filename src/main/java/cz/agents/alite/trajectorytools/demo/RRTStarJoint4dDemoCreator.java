package cz.agents.alite.trajectorytools.demo;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.vecmath.Point2d;

import org.jgrapht.GraphPath;

import cz.agents.alite.creator.Creator;
import cz.agents.alite.trajectorytools.graph.jointspatiotemporal.JointManeuver;
import cz.agents.alite.trajectorytools.graph.jointspatiotemporal.JointManeuversToTrajectoriesConverter;
import cz.agents.alite.trajectorytools.graph.jointspatiotemporal.JointState;
import cz.agents.alite.trajectorytools.graph.jointspatiotemporal.vis.JointRRTStarLayer;
import cz.agents.alite.trajectorytools.graph.jointspatiotemporal.rrtstar.GuidedSynchronousJointSpatioTemporalStraightLineDomain;
import cz.agents.alite.trajectorytools.graph.jointspatiotemporal.rrtstar.SynchronousJointSpatioTemporalStraightLineDomain;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.maneuvers.SpatioTemporalManeuver;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.maneuvers.Straight;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.region.Box4dRegion;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.region.Region;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.rrtstar.GuidedStraightLineDomain;
import cz.agents.alite.trajectorytools.planner.rrtstar.Domain;
import cz.agents.alite.trajectorytools.planner.rrtstar.RRTStarPlanner;
import cz.agents.alite.trajectorytools.simulation.SimulatedAgentEnvironment;
import cz.agents.alite.trajectorytools.trajectory.SpatioTemporalManeuverTrajectory;
import cz.agents.alite.trajectorytools.trajectory.Trajectory;
import cz.agents.alite.trajectorytools.util.SpatialPoint;
import cz.agents.alite.trajectorytools.util.TimePoint;
import cz.agents.alite.trajectorytools.vis.Regions4dLayer;
import cz.agents.alite.trajectorytools.vis.SimulatedAgentLayer;
import cz.agents.alite.trajectorytools.vis.SimulationControlLayer;
import cz.agents.alite.trajectorytools.vis.Regions4dLayer.RegionsProvider;
import cz.agents.alite.trajectorytools.vis.SimulatedAgentLayer.TimeProvider;
import cz.agents.alite.trajectorytools.vis.TrajectoriesLayer;
import cz.agents.alite.trajectorytools.vis.TrajectoriesLayer.TrajectoriesProvider;
import cz.agents.alite.trajectorytools.vis.TrajectoryLayer;
import cz.agents.alite.trajectorytools.vis.TrajectoryLayer.TrajectoryProvider;
import cz.agents.alite.trajectorytools.vis.projection.ProjectionTo2d;
import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;


public class RRTStarJoint4dDemoCreator implements Creator {

    RRTStarPlanner<JointState, JointManeuver> rrtstar;

    Box4dRegion bounds = new Box4dRegion(new TimePoint(0, 0, 0, 0), new TimePoint(1000, 1000, 200, 200));
    Collection<Region> obstacles = new LinkedList<Region>();
    double targetReachedTolerance = 50;

    double gamma = 1300;

    private double SEPARATION = 100;
    private double MINSPEED= 10;
    private double OPTSPEED = 15;
    private double MAXSPEED = 20;
    private double MAXPITCH = 45;
    
    final int nAgents = 2;
    Trajectory trajectories[] = new Trajectory[nAgents];
    private TimePoint[] starts = { new TimePoint(0,1000,50,0), new TimePoint(1000,1000,50,0)};
    private SpatialPoint[] targets = { new SpatialPoint(1000, 0, 50), new SpatialPoint(0, 0, 50)};

    Trajectory[] decoupledTrajectories = new Trajectory[starts.length];
    SimulatedAgentEnvironment simulation = new SimulatedAgentEnvironment();


    @Override
    public void init(String[] args) {

    }
    
    private Trajectory[] getDecoupledTrajectories() {
    	for (int i=0; i < starts.length; i++) {
            Domain<TimePoint, SpatioTemporalManeuver> domain = new GuidedStraightLineDomain(bounds, starts[i], obstacles, targets[i], targetReachedTolerance, MINSPEED, OPTSPEED, MAXSPEED, MAXPITCH, new Random(1));
            RRTStarPlanner<TimePoint, SpatioTemporalManeuver> rrtstar = new RRTStarPlanner<TimePoint, SpatioTemporalManeuver>(domain, starts[i], gamma);
            GraphPath<TimePoint, SpatioTemporalManeuver> graphPath = rrtstar.plan(1000);
            assert(graphPath != null);
            decoupledTrajectories[i] = new SpatioTemporalManeuverTrajectory<TimePoint, SpatioTemporalManeuver>(graphPath, graphPath.getWeight());
    	}
    	return decoupledTrajectories;
    }

    @Override
    public void create() {
        //obstacles.add(new StaticBoxRegion(new SpatialPoint(250, 250, 0), new SpatialPoint(750,750,bounds.getCorner2().z*0.9)));

        //obstacles.add(new BoxRegion(new Point(100, 100, 0), new Point(200,200,750)));

        //obstacles.add(new StaticSphereRegion(new SpatialPoint(400, 100, 0),80));
        //obstacles.add(new StaticBoxRegion(new SpatialPoint(100, 200, 0), new SpatialPoint(230,950,bounds.getCorner2().z)));

        // Add obstacles


        Domain<JointState, JointManeuver> domain
            = new GuidedSynchronousJointSpatioTemporalStraightLineDomain(bounds, SEPARATION, starts, obstacles, targets, getDecoupledTrajectories(), 250, targetReachedTolerance, 10, 15, 30, 45, new Random(1));
        rrtstar = new RRTStarPlanner<JointState, JointManeuver>(domain, new JointState(starts), gamma);
        createVisualization();

        double bestCost = Double.POSITIVE_INFINITY;
        int n=30000;
        for (int i=0; i<n; i++) {
            rrtstar.iterate();

            if (rrtstar.getBestVertex() != null && rrtstar.getBestVertex().getCostFromRoot() < bestCost) {
                bestCost = rrtstar.getBestVertex().getCostFromRoot();
                System.out.println("Iteration: " + i + " Best path cost: " + bestCost);
                GraphPath<JointState, JointManeuver> path = rrtstar.getBestPath();
                
                trajectories = JointManeuversToTrajectoriesConverter.convert(path);
                
                for (int j=0; j<trajectories.length; j++) {
                	simulation.updateTrajectory("t"+j, trajectories[j]);
                }
            }

            
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
        
        // Simulation Control
        VisManager.registerLayer(SimulationControlLayer.create(simulation));
    }

    private void createView(ProjectionTo2d<TimePoint> projection) {
        // graph
        VisManager.registerLayer(JointRRTStarLayer.create(rrtstar, projection, 1, 4));

        VisManager.registerLayer(TrajectoriesLayer.create(new TrajectoriesProvider() {

            @Override
            public Trajectory[] getTrajectories() {
                List<Trajectory> trajs = new LinkedList<Trajectory>(Arrays.asList(trajectories));
                //trajs.addAll(Arrays.asList(decoupledTrajectories));
            	return trajs.toArray(new Trajectory[1]);
            }
        }, projection, 0.5, bounds.getCorner2().w, 't'));

        VisManager.registerLayer(Regions4dLayer.create(new RegionsProvider() {

            @Override
            public Collection<Region> getRegions() {
                LinkedList<Region> regions = new LinkedList<Region>();
                regions.add(bounds);
                regions.addAll(obstacles);
                return regions;
            }
        },
        projection,
        Color.BLACK, 1));
        
        VisManager.registerLayer(SimulatedAgentLayer.create(simulation.getAgentStorage(), projection, new TimeProvider() {

            @Override
            public double getTime() {
                return simulation.getTime();
            }
        }, SEPARATION, 5));
    }



  }
