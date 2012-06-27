package cz.agents.alite.trajectorytools.planner;

import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.alg.AStarShortestPath;
import org.jgrapht.graph.GraphDelegator;

import cz.agents.alite.trajectorytools.graph.maneuver.DefaultManeuver;

/**
 * An implementation of <a href="http://en.wikipedia.org/wiki/A*_search_algorithm">A* search algorithm</a>.
 */
public final class AStarPlanner<V, E extends DefaultManeuver> implements PathPlanner<V, E>
{
    private HeuristicFunction<V> functionH = new NullHeuristicFunction<V>();
    private GoalPenaltyFunction<V> functionG = new NullGoalPenaltyFunction<V>();

    @Override
    public PlannedPath<V, E> planPath(final Graph<V, E> graph, final V startVertex,
            final V endVertex) {
        GraphWithPenaltyFunction<V, E> penaltyGraph = new GraphWithPenaltyFunction<V, E>(graph, functionG);
        try {
            AStarShortestPath<V, E> aStar = new AStarShortestPath<V, E>(penaltyGraph, startVertex, endVertex, new AStarShortestPath.Heuristic<V>() {
                @Override
                public double getHeuristicEstimate(V current, V goal) {
                    return functionH.getHeuristicEstimate(current, goal);
                }
            });

            List<E> pathEdgeList = aStar.getPathEdgeList();
            if (pathEdgeList != null) {
                return new PlannedPathImpl<V, E>(graph, startVertex, endVertex, pathEdgeList, aStar.getPathLength());
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void setGoalPenaltyFunction(GoalPenaltyFunction<V> functionG) {
        this.functionG = functionG;
    }

    @Override
    public void setHeuristicFunction(HeuristicFunction<V> functionH) {
        this.functionH = functionH;
    }
    
    static class GraphWithPenaltyFunction<V, E extends DefaultManeuver> extends GraphDelegator<V, E> {
        private static final long serialVersionUID = -3985698807336517743L;
        private final GoalPenaltyFunction<V> functionG;

        public GraphWithPenaltyFunction(Graph<V, E> g, GoalPenaltyFunction<V> functionG) {
            super(g);
            this.functionG = functionG;
        }
        
        @SuppressWarnings("unchecked")
        public double getEdgeWeight(E e) {
            return super.getEdgeWeight(e) + functionG.getGoalPenalty((V) e.getTarget());
        };
        
    }
}
