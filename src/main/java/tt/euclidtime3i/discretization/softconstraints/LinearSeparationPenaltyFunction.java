package tt.euclidtime3i.discretization.softconstraints;

import tt.euclid2i.Point;

public class LinearSeparationPenaltyFunction implements SeparationPenaltyFunction {

    int separation;
    double maxPenalty;

    public LinearSeparationPenaltyFunction(int separation, double maxPenalty) {
        super();
        this.separation = separation;
        this.maxPenalty = maxPenalty;
    }

    @Override
    public double getPenalty(Point p1, Point p2) {
        return Math.max(0, maxPenalty - (maxPenalty * p1.distance(p2))/separation);
    }
}