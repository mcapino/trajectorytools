package tt.euclidtime3i.discretization.softconstraints;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.GraphDelegator;

import tt.euclid2i.Trajectory;
import tt.euclid2i.trajectory.LinearTrajectory;
import tt.euclidtime3i.Point;
import tt.euclidtime3i.discretization.Straight;

public class PairwiseSoftConstraintWrapper<V extends Point, E extends Straight> extends GraphDelegator<V,E>{

    private Trajectory[] otherTrajs;
    private PairwiseConstraint constraint;
    private double weight;

    public PairwiseSoftConstraintWrapper(DirectedGraph<V, E> g, Trajectory[] otherTrajs, PairwiseConstraint constraint, double weight) {
        super(g);
        this.otherTrajs = otherTrajs;
        this.constraint = constraint;
        this.weight = weight;
    }

    @Override
    public double getEdgeWeight(E e) {

        int duration = e.getEnd().getTime() - e.getStart().getTime();
        double distance = e.getStart().getPosition().distance(e.getEnd().getPosition());
        Trajectory traj = new LinearTrajectory(e.getStart().getTime(), e.getStart().getPosition(), e.getEnd().getPosition(), (int) Math.round(distance / duration), duration, super.getEdgeWeight(e));

        double cost = super.getEdgeWeight(e);
        for (int i = 0; i < otherTrajs.length; i++) {
            cost += weight * constraint.getPenalty(traj, otherTrajs[i]);
        }

        return cost;
    }

}
