package tt.planner.rrtstar.domain;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.GreedyBestFirstSearch;
import org.jgrapht.util.GeneralHeuristic;
import org.jgrapht.util.Goal;
import org.jgrapht.util.HeuristicToGoal;
import tt.planner.rrtstar.util.Extension;

public class GraphDomainExtending<S, E> extends GraphDomain<S, E> {

    public GraphDomainExtending(Graph<S, E> graph, GeneralHeuristic<S> heuristic, S goal, int seed,
                                int depthLimit, double tryGoalRatio) {
        super(graph, heuristic, goal, seed, depthLimit, tryGoalRatio);
    }

    @Override
    public Extension<S, GraphPathEdge<S, E>> extendTo(S from, final S to) {

        GreedyBestFirstSearch<S, E> algorithm = new GreedyBestFirstSearch<S, E>(graph, new HeuristicToGoal<S>() {
            @Override
            public double getCostToGoalEstimate(S current) {
                return heuristic.getCostEstimate(current, to);
            }

            @Override
            public boolean isAdmissible() {
                return heuristic.isAdmissible();
            }


        }, from, new Goal<S>() {
            @Override
            public boolean isGoal(S current) {
                return current.equals(to);
            }
        }, Double.MAX_VALUE, depthLimit
        );

        GraphPath<S, E> path = algorithm.findPath();

        if (path == null) {
            path = algorithm.getTraversedPath();
        }

        return new Extension<S, GraphPathEdge<S, E>>(path.getStartVertex(), path.getEndVertex(), new GraphPathEdge<S, E>(path), path.getWeight(), true);
    }

}
