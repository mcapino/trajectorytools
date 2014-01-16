package tt.euclidtime3i.discretization.softconstraints;

public class BumpSeparationPenaltyFunction implements PenaltyFunction {

    private double maxPenalty;
    private double steepness = 1.0;
	private double minSeparation;

    public BumpSeparationPenaltyFunction(double maxPenalty, double minSeparation) {
        super();
        this.maxPenalty = maxPenalty;
        this.minSeparation = minSeparation;
    }

    @Override
    public double getPenalty(double dist, double t) {

    	if (dist <= minSeparation) {
    		double penalty = (maxPenalty/Math.exp(-steepness)) * Math.exp(-(steepness/(1-Math.pow(dist/minSeparation,2.0))));
    		return penalty;
    	} else {
    		return 0;
    	}
    }
}