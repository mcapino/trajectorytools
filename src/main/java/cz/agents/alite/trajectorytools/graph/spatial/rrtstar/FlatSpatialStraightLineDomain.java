package cz.agents.alite.trajectorytools.graph.spatial.rrtstar;

import java.util.Collection;

import cz.agents.alite.trajectorytools.graph.spatial.region.BoxRegion;
import cz.agents.alite.trajectorytools.graph.spatial.region.SpaceRegion;
import cz.agents.alite.trajectorytools.util.SpatialPoint;

public class FlatSpatialStraightLineDomain extends SpatialStraightLineDomain {

	private double defaultZ;
	
	private static final int RETURN_TARGET_FREQ = 10; 
	private int currentReturnTarget = RETURN_TARGET_FREQ;

	private SpatialPoint targetPoint; 

	public FlatSpatialStraightLineDomain(BoxRegion bounds,
			Collection<SpaceRegion> obstacles, SpaceRegion targetRegion, SpatialPoint targetPoint, double speed,
			double defaultZ) {
		super(bounds, obstacles, targetRegion, speed);
		this.targetPoint = targetPoint;
		this.defaultZ = defaultZ;
	}

    @Override
    public SpatialPoint sampleState() {
        SpatialPoint point;    	
    	if ((--currentReturnTarget) < 0) {
    		currentReturnTarget = RETURN_TARGET_FREQ;
    		point = targetPoint;
    	} else {
	        do {
	            double x = bounds.getCorner1().x + (random.nextDouble() * (bounds.getCorner2().x - bounds.getCorner1().x));
	            double y = bounds.getCorner1().y + (random.nextDouble() * (bounds.getCorner2().y - bounds.getCorner1().y));
	            point = new SpatialPoint(x, y, defaultZ);
	        } while (!isInFreeSpace(point));
    	}
        return point;
    }
}
