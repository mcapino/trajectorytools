package cz.agents.alite.trajectorytools.planner.rrtstar;

public class Extension<P,E> {
    final public  P source;
    final public P target;
    final public E edge;
    final public double cost;
    final public boolean exact;

    public Extension(P source, P target, E edge, double cost, boolean exact) {
        super();
        this.source = source;
        this.target = target;
        this.edge = edge;
        this.cost = cost;
        this.exact = exact;
    }
}