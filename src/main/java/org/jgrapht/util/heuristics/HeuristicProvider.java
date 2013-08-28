package org.jgrapht.util.heuristics;

import org.jgrapht.Graph;
import org.jgrapht.util.HeuristicToGoal;

public interface HeuristicProvider<S, E> {
    public HeuristicToGoal<S> getHeuristicToGoal(Graph<S, E> graph, S goal);
}
