package cz.agents.alite.trajectorytools.graph.spatiotemporal.rrtstar;

import java.util.Collection;

import cz.agents.alite.trajectorytools.graph.spatial.maneuvers.SpatialManeuver;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.Bounds;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.region.Region;
import cz.agents.alite.trajectorytools.planner.rrtstar.Domain;
import cz.agents.alite.trajectorytools.planner.rrtstar.Extension;
import cz.agents.alite.trajectorytools.util.TimePoint;

public class SpatioTemporalStraightLineDomain /*implements Domain<TimePoint, SpatialManeuver>*/ {
	/*
	Bounds bounds;
	Collection<Region> obstacles;
	
	double vmin;
	double vmax;
	
	
	@Override
	public TimePoint getRandomSample() {
		double x = bounds.minx + (Math.random() * (bounds.maxx - bounds.minx));
		double y = bounds.miny + (Math.random() * (bounds.maxy - bounds.miny));
		double z = bounds.minz + (Math.random() * (bounds.maxz - bounds.minz));
		double t = bounds.mint + (Math.random() * (bounds.maxt - bounds.mint));
		
		return new TimePoint(x, y, z, t);
	}

	@Override
	public TimePoint steer(TimePoint from, TimePoint to) {
		return to;
	}

	@Override
	public boolean isVisible(TimePoint p1, TimePoint p2) {
		// check speed constraints
		double requiredSpeed = (p1.get3dPoint().distance(p2.get3dPoint()))/(p2.getTime() - p1.getTime());
		if (requiredSpeed < vmin || requiredSpeed > vmax) {
			return false;
		}
		
		// check obstacles
		for (Region obstacle : obstacles) {
			if (!obstacle.isVisible(p1, p2)) {
				return false;
			}
		}		
		return true;
	}

	@Override
	public double cost(TimePoint p1, TimePoint p2) {
		return p2.get3dPoint().distance(p1.get3dPoint());
	}

	@Override
	public double distance(TimePoint p1, TimePoint p2) {
		return p1.distance(p2);
	}

	@Override
	public double nDimensions() {
		return 4;
	}

	@Override
	public Extension<TimePoint, SpatialManeuver> extendTo(TimePoint from,
			TimePoint to) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double evaluateExtensionCost(TimePoint p1, TimePoint p2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isInTargetRegion(TimePoint p) {
		// TODO Auto-generated method stub
		return false;
	}*/
}
