package cz.agents.alite.trajectorytools.graph.spatial.region;

import cz.agents.alite.trajectorytools.graph.spatiotemporal.region.Region;
import cz.agents.alite.trajectorytools.trajectory.Trajectory;
import cz.agents.alite.trajectorytools.util.TimePoint;

public class TrajectorySafeRegion implements Region {

    Trajectory traj;

    
    @Override
    public boolean intersectsLine(TimePoint p1, TimePoint p2) {
        return false;
    }

    @Override
    public boolean isInside(TimePoint p) {
        // TODO Auto-generated method stub
        return false;
    }

}
