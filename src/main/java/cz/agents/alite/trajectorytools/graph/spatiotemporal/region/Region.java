package cz.agents.alite.trajectorytools.graph.spatiotemporal.region;

import java.util.Collection;

import cz.agents.alite.trajectorytools.util.TimePoint;

/**
 * An obstacle in 3D + time.
 */
public interface Region {
	public boolean isVisible(TimePoint p1, TimePoint p2);
	public boolean isInside(TimePoint p);	
}
