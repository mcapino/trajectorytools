package tt.euclidtime3i.discretization.softconstraints;


public interface PenaltyFunction {
    double getPenalty(double dist);
}