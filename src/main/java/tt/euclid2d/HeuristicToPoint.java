package tt.euclid2d;

import org.jgrapht.util.HeuristicToGoal;

public class HeuristicToPoint<P extends Point> implements HeuristicToGoal<P> {

    private final P target;

    public HeuristicToPoint(P target) {
        this.target = target;
    }

    @Override
    public double getCostToGoalEstimate(P current) {
        return current.distance(target);
    }
}
