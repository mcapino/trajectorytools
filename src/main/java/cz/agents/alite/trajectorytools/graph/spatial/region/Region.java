package cz.agents.alite.trajectorytools.graph.spatial.region;

import cz.agents.alite.trajectorytools.util.Point;

/**
 * An obstacle in 3D + time.
 */
public interface Region {
	public boolean intersectsLine(Point p1, Point p2);
	public boolean isInside(Point p);	
}
