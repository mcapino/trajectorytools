package tt.euclidtime3i.discretization.softconstraints;

import tt.euclid2i.Point;
import tt.euclid2i.Trajectory;

public class PenaltyIntegrator {

    public static double integratePenalty(
            Trajectory thisTrajectory,
            PenaltyFunction[] penaltyFunctions,
            Trajectory[] otherTrajectories,
            int samplingInterval) {

        double penaltySum = 0;

        for (int t = thisTrajectory.getMinTime(); t < thisTrajectory.getMaxTime(); t += samplingInterval) {
            Point thisPos = thisTrajectory.get(t);
            for (int j = 0; j < otherTrajectories.length; j++) {

                if (otherTrajectories[j] != null) {
                    if (t >= otherTrajectories[j].getMinTime() && t <= otherTrajectories[j].getMaxTime()) {

                        // handle the case when the sample lies near the end of either this trajectory or other trajectory
                        double segmentLength = Math.min(Math.min(samplingInterval, thisTrajectory.getMaxTime() - t), otherTrajectories[j].getMaxTime() - t);

                        Point otherPos = otherTrajectories[j].get(t);
                        penaltySum += penaltyFunctions[j].getPenalty(thisPos.distance(otherPos), t) * segmentLength;
                    }
                }
            }
        }
        return penaltySum;
    }

    public static double integratePenalty( Trajectory thisTrajectory,
            PenaltyFunction penaltyFunction,
            Trajectory otherTrajectory,
            int samplingInterval) {

    	return integratePenalty(thisTrajectory, new PenaltyFunction[] {penaltyFunction}, new Trajectory[] {otherTrajectory}, samplingInterval);
    }

}
