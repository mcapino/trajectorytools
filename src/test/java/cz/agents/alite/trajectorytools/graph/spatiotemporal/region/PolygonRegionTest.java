package cz.agents.alite.trajectorytools.graph.spatiotemporal.region;

import static org.junit.Assert.*;

import org.junit.Test;

import cz.agents.alite.trajectorytools.util.SpatialPoint;
import cz.agents.alite.trajectorytools.util.TimePoint;

public class PolygonRegionTest {

	@Test
	public void testIntersectsLineXY() {
		SpaceTimeRegion region = new PolygonRegion(new SpatialPoint[] {
				new SpatialPoint( -1, -1, 0 ),
				new SpatialPoint( -1, 1, 0 ),
				new SpatialPoint( 1, 1, 0 ),
				new SpatialPoint( 1, -1, 0 ),
		});

		assertTrue( region.intersectsLine(new TimePoint(0, 0, -1, 0), new TimePoint(0, 0, 1, 0)) );
		assertTrue( region.intersectsLine(new TimePoint(0, 0, 1, 0), new TimePoint(0, 0, -1, 0)) );
		assertFalse( region.intersectsLine(new TimePoint(0, 0, 1, 0), new TimePoint(0, 0, 2, 0)) );
		assertFalse( region.intersectsLine(new TimePoint(0, 0, -1, 0), new TimePoint(0, 0, -2, 0)) );
		assertFalse( region.intersectsLine(new TimePoint(0, 0, -2, 0), new TimePoint(0, 0, -1, 0)) );
		assertFalse( region.intersectsLine(new TimePoint(2, 2, -1, 0), new TimePoint(2, 2, 1, 0)) );
	}

	@Test
	public void testIntersectsLineXYCounterClockwise() {
		SpaceTimeRegion region = new PolygonRegion(new SpatialPoint[] {
				new SpatialPoint( 1, -1, 0 ),
				new SpatialPoint( 1, 1, 0 ),
				new SpatialPoint( -1, 1, 0 ),
				new SpatialPoint( -1, -1, 0 ),
		});

		assertTrue( region.intersectsLine(new TimePoint(0, 0, -1, 0), new TimePoint(0, 0, 1, 0)) );
		assertTrue( region.intersectsLine(new TimePoint(0, 0, 1, 0), new TimePoint(0, 0, -1, 0)) );
		assertFalse( region.intersectsLine(new TimePoint(0, 0, 1, 0), new TimePoint(0, 0, 2, 0)) );
		assertFalse( region.intersectsLine(new TimePoint(0, 0, -1, 0), new TimePoint(0, 0, -2, 0)) );
		assertFalse( region.intersectsLine(new TimePoint(0, 0, -2, 0), new TimePoint(0, 0, -1, 0)) );
		assertFalse( region.intersectsLine(new TimePoint(2, 2, -1, 0), new TimePoint(2, 2, 1, 0)) );
	}

	@Test
	public void testIntersectsLineXYRealWorld() {
		SpaceTimeRegion region = new PolygonRegion(new SpatialPoint[] {
				new SpatialPoint( 940.3090836012861, -1500.0, -500.0 ),
				new SpatialPoint( 940.3090836012861, -1500.0, 500.0 ),
				new SpatialPoint( -58.0, 333.0, 500.0 ),
				new SpatialPoint( -58.0, 333.0, -500.0 ),
		});

		assertTrue( region.intersectsLine(new TimePoint(800, -100, 100, 0), new TimePoint(-1119, -330, 100, 0)) );
		assertTrue( region.intersectsLine(new TimePoint(-1119, -330, 100, 0), new TimePoint(800, -100, 100, 0)) );
	}

	@Test
	public void testIsInsideXY() {
		SpaceTimeRegion region = new PolygonRegion(new SpatialPoint[] {
				new SpatialPoint( -1, -1, 0 ),
				new SpatialPoint( -1, 1, 0 ),
				new SpatialPoint( 1, 1, 0 ),
				new SpatialPoint( 1, -1, 0 ),
		});

		// not supported
		// assertTrue( region.isInside( new TimePoint(0, 0, 0, 0) ) );
		assertFalse( region.isInside( new TimePoint(0, 0, -1, 0) ) );
		assertFalse( region.isInside( new TimePoint(0, 0, 1, 0) ) );
		assertFalse( region.isInside( new TimePoint(2, 0, 0, 0) ) );
		assertFalse( region.isInside( new TimePoint(2, 2, 2, 0) ) );
	}

}
