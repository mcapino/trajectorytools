package tt.planner.rrtstar.graph;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.GreedyBestFirstSearch;
import org.jgrapht.util.GeneralHeuristic;
import org.jgrapht.util.Goal;
import tt.planner.rrtstar.Domain;
import tt.planner.rrtstar.Extension;
import tt.planner.rrtstar.ExtensionEstimate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GraphDomain<S, E> implements Domain<S, GraphPathEdge<S, E>> {

    private Graph<S, E> graph;
    private Goal<S> goal;
    private GeneralHeuristic<S> heuristic;
    private Random random;
    private double radius;

    private List<S> vertexSet;
    private int vertexCount;

    public GraphDomain(Graph<S, E> graph, GeneralHeuristic<S> heuristic, Goal<S> goal, int seed, double radius) {
        this.graph = graph;
        this.heuristic = heuristic;
        this.goal = goal;
        this.radius = radius;

        this.random = new Random(seed);
        this.vertexSet = new ArrayList<S>(graph.vertexSet());
        this.vertexCount = vertexSet.size();
    }

    @Override
    public S sampleState() {
        int rnd = random.nextInt(vertexCount);
        return vertexSet.get(rnd);
    }

    @Override
    public Extension<S, GraphPathEdge<S, E>> extendTo(S from, S to) {
        Extension<S, GraphPathEdge<S, E>> result = null;
        GraphPath<S, E> path = GreedyBestFirstSearch.findPathBetween(graph, heuristic, from, to, radius);

        if (path != null) {
            result = new Extension<S, GraphPathEdge<S, E>>(from, to, new GraphPathEdge<S, E>(path), path.getWeight(), false);
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
        return 1;
    }

    @Override
    public boolean isInTargetRegion(S s) {
        return goal.isGoal(s);
    }
}
