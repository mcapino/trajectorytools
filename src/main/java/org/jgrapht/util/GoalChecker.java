package org.jgrapht.util;

/**
 *
 * @author Vojtech Letal <letalvoj@fel.cvut.cz>
 */
public interface GoalChecker<V> {

    boolean isGoal(V current);
}
