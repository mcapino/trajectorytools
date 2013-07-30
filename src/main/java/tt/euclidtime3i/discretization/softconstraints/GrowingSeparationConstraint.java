package tt.euclidtime3i.discretization.softconstraints;

import tt.euclid2i.Point;
import tt.euclid2i.Trajectory;

public class GrowingSeparationConstraint implements PairwiseConstraint {

    SeparationPenaltyFunction penaltyFunction;
    int samplingInterval;
    int separation;

    public GrowingSeparationConstraint(int separation,
            int samplingInterval) {
        super();
        this.samplingInterval = samplingInterval;
        this.separation = separation;
    }

    @Override
    public double getPenalty(Trajectory t1, Trajectory t2, double weight) {
        return integratePenalty(t1, new Trajectory[] {t2}, (int) (this.separation * weight), 1000, samplingInterval);
    }

    public static double integratePenalty(
            Trajectory thisTrajectory,
            Trajectory[] otherTrajectories,
            int separation, int penalty,
            int samplingInterval) {

        double penaltySum = 0;

        for (int t = thisTrajectory.getMinTime(); t < thisTrajectory.getMaxTime(); t += samplingInterval) {
            Point thisPos = thisTrajectory.get(t);
            for (int j = 0; j < otherTrajectories.length; j++) {

                if (otherTrajectories[j]!= null) {
                    if (t >= otherTrajectories[j].getMinTime() && t <= otherTrajectories[j].getMaxTime()) {

                        // handle the case when the sample lies near the end of either this trajectory or other trajectory
                        int segmentLength = Math.min(Math.min(samplingInterval, thisTrajectory.getMaxTime() - t), otherTrajectories[j].getMaxTime() - t);



                        Point otherPos = otherTrajectories[j].get(t);
                        if (thisPos.distance(otherPos) < separation) {
                            penaltySum += penalty * segmentLength;
                        }
                    }
                }
            }
        }
        return penaltySum;
    }

}
