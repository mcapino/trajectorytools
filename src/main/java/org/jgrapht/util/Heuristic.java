package org.jgrapht.util;

public interface Heuristic<V> {
    double getCostToGoalEstimate(V current);
}
