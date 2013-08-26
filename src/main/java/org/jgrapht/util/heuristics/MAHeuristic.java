package org.jgrapht.util.heuristics;

import org.jgrapht.Graph;
import org.jgrapht.alg.AStarShortestPathSimple;
import org.jgrapht.util.Goal;
import org.jgrapht.util.HeuristicToGoal;

import java.util.Map;

public class MAHeuristic<S, M, E> implements HeuristicToGoal<M> {

    private Graph<S, E> graph;
    private MAStateProvider<S, M> provider;
    private Map<S, Double>[] distances;
    private int size;

    public MAHeuristic(Graph<S, E> graph, M goal, MAStateProvider<S, M> provider) {
        this.graph = graph;
        this.provider = provider;

        S[] goals = provider.getAgentsStates(goal);
        this.size = goals.length;
        this.distances = getDistances(goals);

    }

    @SuppressWarnings("unchecked")
    private Map<S, Double>[] getDistances(S[] goals) {
        Map<S, Double>[] shortestDistances = new Map[size];

        for (int i = 0; i < size; i++) {
            shortestDistances[i] = CustomAStar.calculateDistancesToGoal(graph, goals[i]);
        }

        return shortestDistances;
    }

    @Override
    public double getCostToGoalEstimate(M current) {
        S[] agentsStates = provider.getAgentsStates(current);

        double estimate = 0;
        for (int i = 0; i < size; i++) {
            try {
                estimate += distances[i].get(agentsStates[i]);
            } catch (NullPointerException ex) {
                throw new RuntimeException("No such state" + agentsStates[i] + " in shortest path tree");
            }
        }

        return estimate;
    }

    private static class CustomAStar<S, E> extends AStarShortestPathSimple<S, E> {

        public static <S, E> Map<S, Double> calculateDistancesToGoal(Graph<S, E> graph, S goal) {
            CustomAStar<S, E> astar = new CustomAStar<S, E>(graph, new ZeroHeuristic<S>(), goal, new Goal<S>() {
                @Override
                public boolean isGoal(S current) {
                    return false;
                }
            });

            astar.findPath(Integer.MAX_VALUE);
            return astar.getShortestDistances();
        }

        private CustomAStar(Graph<S, E> graph, HeuristicToGoal<S> heuristic, S startVertex, Goal<S> goal) {
            super(graph, heuristic, startVertex, goal);
        }

        public Map<S, Double> getShortestDistances() {
            return shortestDistanceToVertex;
        }
    }
}
