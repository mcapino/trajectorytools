package tt.euclidtime3i.discretization.softconstraints;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.GraphDelegator;

import tt.euclid2i.Trajectory;
import tt.euclidtime3i.Point;
import tt.euclidtime3i.discretization.Straight;

public class PairwiseSoftConstraintWrapper<V extends Point, E extends Straight> extends GraphDelegator<V, E> {

    private Trajectory[] otherTrajs;
    private int[] separations;
    private PairwiseConstraint constraint;
    private double weight;

    public PairwiseSoftConstraintWrapper(DirectedGraph<V, E> g, Trajectory[] otherTrajs, int[] separations, PairwiseConstraint constraint, double weight) {
        super(g);
        this.otherTrajs = otherTrajs;
        this.separations = separations;
        this.constraint = constraint;
        this.weight = weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setOtherTrajs(Trajectory[] otherTrajs) {
        this.otherTrajs = otherTrajs;
    }

    public void setConstraint(PairwiseConstraint constraint) {
        this.constraint = constraint;
    }

    @Override
    public double getEdgeWeight(E e) {
        double cost = super.getEdgeWeight(e);

        if (weight == 0)
            return cost;
        else
            return cost + calculateEdgePenalty(e);
    }

    public double calculateEdgePenalty(E e) {
        double penalty = 0;

        Trajectory edgeTrajectory = new tt.euclidtime3i.trajectory.LinearTrajectory(e.getStart(), e.getEnd(), super.getEdgeWeight(e));

        for (int i = 0; i < otherTrajs.length; i++) {
            double constraintPenalty = constraint.getPenalty(edgeTrajectory, otherTrajs[i], separations[i]);
            if (constraintPenalty > 0) //Infinity times 0 is NaN
                penalty += weight * constraintPenalty;
        }

        return penalty;
    }

}
