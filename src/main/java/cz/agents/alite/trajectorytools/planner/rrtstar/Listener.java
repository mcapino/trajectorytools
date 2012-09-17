package cz.agents.alite.trajectorytools.planner.rrtstar;

public abstract class Listener<S> {
    /**
     * A callback from the planner informing about a new vertex added to the tree.
     */
    public void notifyNewVertex(S s) {};

    /**
     * A callback from the planner to the domain,
     * informing about a new vertex added to the tree.
     */
    public void notifyIterationFinished() {};
}
