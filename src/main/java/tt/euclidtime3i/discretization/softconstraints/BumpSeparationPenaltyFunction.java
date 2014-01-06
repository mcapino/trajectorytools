package tt.euclidtime3i.discretization.softconstraints;

import tt.euclid2i.Point;

public class BumpSeparationPenaltyFunction implements SeparationPenaltyFunction {

    private double maxPenalty;
    private double steepness = 1.0;

    public BumpSeparationPenaltyFunction(double maxPenalty) {
        super();
        this.maxPenalty = maxPenalty;
    }

    @Override
    public double getPenalty(Point p1, Point p2, int minSeparation) {

    	double dist = p1.distance(p2);

    	if (dist <= minSeparation) {
    		double penalty = (maxPenalty/Math.exp(-steepness)) * Math.exp(-(steepness/(1-Math.pow(dist/minSeparation,2.0))));
    		return penalty;
    	} else {
    		return 0;
    	}
    }
}