package tt.euclidtime3i.discretization.softconstraints;

public class LinearSeparationPenaltyFunction implements PenaltyFunction {

    private double maxPenalty;
	private double minSeparation;

    public LinearSeparationPenaltyFunction(double maxPenalty, double minSeparation) {
        super();
        this.maxPenalty = maxPenalty;
        this.minSeparation = minSeparation;
    }

    @Override
    public double getPenalty(double dist, double t) {
        return Math.max(0, maxPenalty - (maxPenalty * dist) / minSeparation);
    }

    public double getMinSeparation() {
		return minSeparation;
	}
}