package org.jgrapht.util;

/**
 *
 * @author Vojtech Letal <letalvoj@fel.cvut.cz>
 */
public interface Heuristic<V> {

    double getCostToGoalEstimate(V current);
}
