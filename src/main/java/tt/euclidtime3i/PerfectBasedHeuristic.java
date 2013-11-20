package tt.euclidtime3i;

import org.jgrapht.Graph;
import org.jgrapht.util.HeuristicToGoal;
import org.jgrapht.util.heuristics.PerfectHeuristic;
import tt.euclid2i.Line;

public class PerfectBasedHeuristic<P extends Point, E> implements HeuristicToGoal<P> {

    private HeuristicToGoal<tt.euclid2i.Point> heuristics;

    public PerfectBasedHeuristic(Graph<tt.euclid2i.Point, Line> graph, tt.euclid2i.Point target) {
        this.heuristics = new PerfectHeuristic<tt.euclid2i.Point, Line>(graph, target);
    }

    @Override
    public double getCostToGoalEstimate(P current) {
        return heuristics.getCostToGoalEstimate(current.getPosition());
    }
}
