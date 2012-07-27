package cz.agents.alite.trajectorytools.demo;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import cz.agents.alite.creator.Creator;
import cz.agents.alite.trajectorytools.graph.spatial.maneuvers.SpatialManeuver;
import cz.agents.alite.trajectorytools.graph.spatial.region.BoxRegion;
import cz.agents.alite.trajectorytools.graph.spatial.region.Region;
import cz.agents.alite.trajectorytools.graph.spatial.rrtstar.SpatialStraightLineDomain;
import cz.agents.alite.trajectorytools.planner.rrtstar.Domain;
import cz.agents.alite.trajectorytools.planner.rrtstar.RRTStarPlanner;
import cz.agents.alite.trajectorytools.util.Point;
import cz.agents.alite.trajectorytools.vis.RRTStarLayer;
import cz.agents.alite.trajectorytools.vis.RegionsLayer;
import cz.agents.alite.trajectorytools.vis.RegionsLayer.RegionsProvider;
import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;


public class RRTStar2dDemoCreator implements Creator {

	RRTStarPlanner<Point, SpatialManeuver> rrtstar;
	
	Point initialPoint = new Point(100, 100, 0);
	BoxRegion bounds = new BoxRegion(new Point(0, 0, 0), new Point(1000, 1000, 1000));
	Collection<Region> obstacles = new LinkedList<Region>();
	Region target = new BoxRegion(new Point(900, 900, 900), new Point(1000, 1000, 1000));
	
    @Override
    public void init(String[] args) {
    	
    }

    @Override
    public void create() {
    	Region obstacle = new BoxRegion(new Point(250, 250, 0), new Point(750,750,750));
		obstacles.add(obstacle);
		
		
		Domain<Point, SpatialManeuver> domain = new SpatialStraightLineDomain(bounds, obstacles, target, 1.0);
		rrtstar = new RRTStarPlanner<Point, SpatialManeuver>(domain, initialPoint, 1500);		    	
        createVisualization();
        
        int n=1500;
        for (int i=0; i<n; i++) {
        	rrtstar.iterate();
        	
        	try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
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
        
        
        // Overlay
        VisManager.registerLayer(VisInfoLayer.create());
    }

  }
