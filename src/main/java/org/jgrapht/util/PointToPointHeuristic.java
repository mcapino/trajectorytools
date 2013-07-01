package org.jgrapht.util;

public interface PointToPointHeuristic<S> {
    double getCostEstimate(S from, S to);
}
