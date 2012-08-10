package cz.agents.alite.trajectorytools.graph.multiagentspatiotemporal;

import cz.agents.alite.trajectorytools.graph.spatiotemporal.maneuvers.SpatioTemporalManeuver;

public class JointManeuver {

    SpatioTemporalManeuver[] maneuvers;

    public JointManeuver(SpatioTemporalManeuver[] maneuvers) {
        this.maneuvers = maneuvers;
    }

    SpatioTemporalManeuver get(int i) {
        return maneuvers[i];
    }

}
