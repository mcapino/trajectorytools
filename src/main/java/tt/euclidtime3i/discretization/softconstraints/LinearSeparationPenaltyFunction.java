package tt.euclidtime3i.discretization.softconstraints;

import tt.euclid2i.Point;

public class LinearSeparationPenaltyFunction implements SeparationPenaltyFunction {

    private double maxPenalty;

    public LinearSeparationPenaltyFunction(double maxPenalty) {
        super();
        this.maxPenalty = maxPenalty;
    }

    @Override
    public double getPenalty(Point p1, Point p2, int minSeparation) {
        return Math.max(0, maxPenalty - (maxPenalty * p1.distance(p2)) / minSeparation);
    }
}