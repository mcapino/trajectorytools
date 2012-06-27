package cz.agents.alite.trajectorytools.planner;

import org.jgrapht.Graph;

public interface PathPlanner<V, E> {

    /**
     * @param graph the graph to be searched
     * @param startVertex the vertex at which the path should start
     * @param endVertex the vertex at which the path should end
     * 
     * @return planned path or null if no path exists
     */
    public PlannedPath<V, E> planPath(final Graph<V, E> graph, final V startVertex,
            final V endVertex);

    public void setGoalPenaltyFunction(GoalPenaltyFunction<V> functionG);
    public void setHeuristicFunction(HeuristicFunction<V> functionH);
}