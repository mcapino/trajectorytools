package cz.agents.alite.trajectorytools.graph.spatiotemporal.rrtstar;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.vecmath.Vector3d;

import cz.agents.alite.trajectorytools.graph.spatiotemporal.maneuvers.SpatioTemporalManeuver;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.region.Box4dRegion;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.region.Region;
import cz.agents.alite.trajectorytools.planner.rrtstar.Extension;
import cz.agents.alite.trajectorytools.util.SpatialPoint;
import cz.agents.alite.trajectorytools.util.TimePoint;

public class ProcerusStraightLineDomain extends GuidedStraightLineDomain {

    Map<TimePoint, TimePoint> parents = new HashMap<TimePoint, TimePoint>();

    public ProcerusStraightLineDomain(Box4dRegion bounds,
            TimePoint initialPoint, Collection<Region> obstacles,
            SpatialPoint target, double targetReachedTolerance,
            double minSpeed, double optSpeed, double maxSpeed, double maxPitch,
            Random random) {
        super(bounds, initialPoint, obstacles, target, targetReachedTolerance,
                minSpeed, optSpeed, maxSpeed, maxPitch, random);
    }

    @Override
    public Extension<TimePoint, SpatioTemporalManeuver> extendTo(
            TimePoint from, TimePoint to) {
        Extension<TimePoint, SpatioTemporalManeuver> extension = super.extendTo(from, to);
        if (extension != null) {
            parents.put(extension.target, from);
        }
        return extension;
    }

    @Override
    protected boolean satisfiesDynamicLimits(TimePoint p1, TimePoint p2) {
        if (super.satisfiesDynamicLimits(p1, p2)) {
            // Check min length
        	if (p1.getSpatialPoint().distance(p2.getSpatialPoint()) < 20) {
        		return false;
        	}
        	
        	// Check angle
        	TimePoint parent = parents.get(p1);

            if (parent == null) return true;

            Vector3d incoming = new Vector3d(p1.getSpatialPoint());
            incoming.sub(parent.getSpatialPoint());

            Vector3d outgoing = new Vector3d(p2.getSpatialPoint());
            outgoing.sub(p1.getSpatialPoint());

            double angle = incoming.angle(outgoing);

            //return true;

            return (angle < Math.PI/4);

        } else {
            return false;
        }

    }

}
