package tt.planner.rrtstar.graph;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.GreedyBestFirstSearch;
import org.jgrapht.util.GeneralHeuristic;
import org.jgrapht.util.HeuristicToGoal;
import tt.planner.rrtstar.Domain;
import tt.planner.rrtstar.Extension;
import tt.planner.rrtstar.ExtensionEstimate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GraphDomain<S, E> implements Domain<S, GraphPathEdge<S, E>> {

    private Graph<S, E> graph;
    private S goal;
    private GeneralHeuristic<S> heuristic;
    private Random random;
    private double radius;
    private double tryGoalRatio;

    private List<S> vertexSet;
    private int vertexCount;

    public GraphDomain(Graph<S, E> graph, GeneralHeuristic<S> heuristic, S goal, int seed, double radius, double tryGoalRatio) {
        this.graph = graph;
        this.heuristic = heuristic;
        this.goal = goal;
        this.radius = radius;
        this.tryGoalRatio = tryGoalRatio;

        this.random = new Random(seed);
        this.vertexSet = new ArrayList<S>(graph.vertexSet());
        this.vertexCount = vertexSet.size();
    }

    @Override
    public S sampleState() {
        if (Math.random() > tryGoalRatio) {
            int rnd = random.nextInt(vertexCount);
            return vertexSet.get(rnd);
        } else {
            return goal;
        }

    }

    @Override
    public Extension<S, GraphPathEdge<S, E>> extendTo(S from, final S to) {
        Extension<S, GraphPathEdge<S, E>> result = null;

        if ((heuristic.getCostEstimate(from, to) < radius) || !heuristic.isAdmissible()) {

            GraphPath<S, E> path = GreedyBestFirstSearch.findPathBetween(graph, new HeuristicToGoal<S>() {
                @Override
                public double getCostToGoalEstimate(S current) {
                    return heuristic.getCostEstimate(current, to);
                }

                @Override
                public boolean isAdmissible() {
                    return heuristic.isAdmissible();
                }


            }, from, to, radius);

            if (path != null) {
                result = new Extension<S, GraphPathEdge<S, E>>(from, to, new GraphPathEdge<S, E>(path), path.getWeight(), true);
            }
        }

        return result;
    }

    @Override
    public ExtensionEstimate estimateExtension(S from, S to) {
        return new ExtensionEstimate(heuristic.getCostEstimate(from, to), false);
    }

    @Override
    public double estimateCostToGo(S s) {
        return heuristic.getCostToGoalEstimate(s);
    }

    @Override
    public double distance(S s1, S s2) {
        return heuristic.getCostEstimate(s1, s2);
    }

    @Override
    public double nDimensions() {
        return 2;
    }

    @Override
    public boolean isInTargetRegion(S s) {
        return goal.equals(s);
    }
}
