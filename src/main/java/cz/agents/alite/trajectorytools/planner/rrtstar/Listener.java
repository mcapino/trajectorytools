package cz.agents.alite.trajectorytools.planner.rrtstar;

public interface Listener<S> {
    /**
     * A callback from the planner to the domain,
     * informing about a new vertex added to the tree.
     */
    void notifyNewVertex(S s);
}
