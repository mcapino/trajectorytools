package cz.agents.alite.trajectorytools.planner.rrtstar;

public class Extension<P,E> {
	final public  P source;
	final public P target;
	final public E edge;
	final public double cost;
	
	public Extension(P source, P target, E edge, double cost) {
		super();
		this.source = source;
		this.target = target;
		this.edge = edge;
		this.cost = cost;
	}	
}