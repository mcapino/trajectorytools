package cz.agents.alite.trajectorytools.graph.kinematicspatiotemporal;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Random;

import javax.vecmath.Point4d;

import org.junit.Test;

import cz.agents.alite.trajectorytools.graph.spatiotemporal.region.Box4dRegion;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.region.Region;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.rrtstar.KinematicStraightLineDomain;
import cz.agents.alite.trajectorytools.util.OrientedPoint;
import cz.agents.alite.trajectorytools.util.OrientedTimePoint;
import cz.agents.alite.trajectorytools.util.SpatialPoint;

public class DubinsDistanceTest {

	@Test
	public void test() {
		KinematicStraightLineDomain domain = new KinematicStraightLineDomain(new Box4dRegion(new Point4d(0,0,0,0), new Point4d(10,10,10,10)), new OrientedTimePoint(0,0,0,0,0,1,0), new ArrayList<Region>(), new SpatialPoint(20,20,5), 10, 15, 15, 15, 10, 50, 45, new Random(1));
		double distance = domain.dubins3dDistance(new OrientedPoint(0,0,0,0,-1,0), new OrientedPoint(500,0,0,0,1,0));
	}

}
