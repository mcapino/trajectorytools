package tt.euclidtime3i.discretization.softconstraints;

public class LinearSeparationPenaltyFunction implements PenaltyFunction {

    private double maxPenalty;
    private double minPenalty;
	private double minSeparation;
	
    public LinearSeparationPenaltyFunction(double maxPenalty, double minSeparation) {
    	this(maxPenalty, minSeparation, 0);
    }

    public LinearSeparationPenaltyFunction(double maxPenalty, double minSeparation, double minPenalty) {
        super();
        this.maxPenalty = maxPenalty;
        this.minPenalty = minPenalty;
        this.minSeparation = minSeparation;
    }

    @Override
    public double getPenalty(double dist, double t) {
        if (dist > minSeparation) {
        	return 0;
        } else {
        	return Math.max(minPenalty, maxPenalty - (maxPenalty * dist) / minSeparation);
        }
    	
    }

    public double getMinSeparation() {
		return minSeparation;
	}
}