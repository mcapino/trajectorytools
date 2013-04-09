package tt.discrete;


/**
 * A trajectory having a certain cost.
 */
public interface EvaluatedTrajectory<S> extends Τrajectory<S> {
    public double getCost();
}
