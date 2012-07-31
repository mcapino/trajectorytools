package cz.agents.alite.trajectorytools.graph.spatiotemporal;

import cz.agents.alite.trajectorytools.util.TimePoint;

public class Bounds {
		final public double minx;
		final public double maxx;
		
		final public double miny;
		final public double maxy;
		
		final public double minz;
		final public double maxz;
		
		final public double mint;
		final public double maxt;
		
		public Bounds(double minx, double maxx, double miny,
				double maxy, double minz, double maxz, double mint, double maxt) {
			super();
			this.minx = minx;
			this.maxx = maxx;
			this.miny = miny;
			this.maxy = maxy;
			this.minz = minz;
			this.maxz = maxz;
			this.mint = mint;
			this.maxt = maxt;
		}	
		
		public boolean isInside(TimePoint timepoint) {
			return minx <= timepoint.getPoint3d().x &&
					maxx >= timepoint.getPoint3d().x &&
					miny <= timepoint.getPoint3d().y &&
					maxy >= timepoint.getPoint3d().y &&
					minz <= timepoint.getPoint3d().z &&
					maxz >= timepoint.getPoint3d().z &&
					mint <= timepoint.getTime() &&
					maxt >= timepoint.getTime() ;
		}
}
