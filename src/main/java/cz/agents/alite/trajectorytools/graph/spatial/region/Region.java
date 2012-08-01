package cz.agents.alite.trajectorytools.graph.spatial.region;

import cz.agents.alite.trajectorytools.util.SpatialPoint;

/**
 * An obstacle in 3D + time.
 */
public interface Region {
	public boolean intersectsLine(SpatialPoint p1, SpatialPoint p2);
	public boolean isInside(SpatialPoint p);	
}
