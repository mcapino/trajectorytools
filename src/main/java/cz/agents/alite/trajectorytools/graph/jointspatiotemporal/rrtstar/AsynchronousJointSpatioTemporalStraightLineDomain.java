package cz.agents.alite.trajectorytools.graph.jointspatiotemporal.rrtstar;

import java.util.Collection;
import java.util.Random;

import cz.agents.alite.trajectorytools.graph.jointspatiotemporal.JointManeuver;
import cz.agents.alite.trajectorytools.graph.jointspatiotemporal.JointState;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.maneuvers.SpatioTemporalManeuver;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.region.Box4dRegion;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.region.Region;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.rrtstar.SpatioTemporalStraightLineDomain;
import cz.agents.alite.trajectorytools.planner.rrtstar.Domain;
import cz.agents.alite.trajectorytools.planner.rrtstar.Extension;
import cz.agents.alite.trajectorytools.planner.rrtstar.ExtensionEstimate;
import cz.agents.alite.trajectorytools.util.SpatialPoint;
import cz.agents.alite.trajectorytools.util.TimePoint;

public class AsynchronousJointSpatioTemporalStraightLineDomain implements Domain<JointState, JointManeuver> {

    SpatioTemporalStraightLineDomain[] domains;
    double separation;

    public AsynchronousJointSpatioTemporalStraightLineDomain(Box4dRegion bounds, double separation, TimePoint[] initialPoints,
            Collection<Region> obstacles, SpatialPoint[] targets, double targetReachedTolerance, double minSpeed,
            double optSpeed, double maxSpeed, double maxPitch, Random random) {
        super();

        this.separation = separation;

        assert(separated(initialPoints, separation));

        for (int i = 0; i < initialPoints.length; i++) {
            domains[i] = new SpatioTemporalStraightLineDomain(bounds,
                    initialPoints[i], obstacles, targets[i],
                    targetReachedTolerance, minSpeed, optSpeed, maxSpeed,
                    maxPitch, random);
        }


    }

    @Override
    public JointState sampleState() {
        TimePoint[] points = new TimePoint[nAgents()];
        do {
            for (int i = 0; i < nAgents(); i++) {
                points[i] = domains[i].sampleState();
            }
        } while (!separated(points, separation)); /* not necessary probably, will be checked when extending anyway */

        return new JointState(points);
    }

    @Override
    public Extension<JointState, JointManeuver> extendTo(JointState from, JointState to) {

        double cost = 0.0;
        boolean exact = true;
        SpatioTemporalManeuver[] maneuvers = new SpatioTemporalManeuver[from.nAgents()];
        TimePoint[] targets = new TimePoint[from.nAgents()];

        for (int i=0; i<from.nAgents(); i++) {
            Extension<TimePoint, SpatioTemporalManeuver> extension = domains[i].extendTo(from.get(i), to.get(i));
            if (extension != null) {
                maneuvers[i] = extension.edge;
                targets[i] = extension.target;
                cost += extension.cost;
                if (!extension.exact) {
                    exact = false;
                }
            } else {
                // extension not possible
                return null;
            }
        }

        // Check separation breach between individual trajectories



        Extension<JointState, JointManeuver> jointExtension = new Extension<JointState, JointManeuver> (from, new JointState(targets), new JointManeuver(maneuvers), cost, exact);
        return jointExtension;
    }

    @Override
    public ExtensionEstimate estimateExtension(
            JointState from, JointState to) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public double estimateCostToGo(JointState s) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double distance(JointState s1, JointState s2) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double nDimensions() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isInTargetRegion(JointState s) {
        // TODO Auto-generated method stub
        return false;
    }

    public int nAgents() {
        return domains.length;
    }

    private static boolean separated(TimePoint[] timePoints, double separation) {
        for (int i=0; i<timePoints.length; i++) {
            for (int j=i+1; j<timePoints.length; i++) {
                if (Math.abs(timePoints[i].getTime() - timePoints[j].getTime()) < 0.001) {
                    if (timePoints[i].getSpatialPoint().distance(timePoints[j].getSpatialPoint()) < separation) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

}
