package cz.agents.alite.trajectorytools.planner.rrtstar;

import java.util.Collection;
import java.util.HashSet;

import cz.agents.alite.trajectorytools.util.TimePoint;

public class Vertex<P> {
	P point;
	Vertex<P> parent;
	Collection<Vertex<P>> children;
	
	double costToRoot;
	double costToParent;
	
	public Vertex(P point) {
		super();
		this.point = point;
		this.parent = null;
		this.costToRoot = 0.0;
		this.costToParent = 0.0;
		this.children = new HashSet<Vertex<P>>();
	}

	public P getPoint() {
		return point;
	}
	
	public double getCostToRoot() {
		return costToRoot;
	}
	
	public void addChild(Vertex<P> child) {
		children.add(child);
	}
	
	public void removeChild(Vertex<P> child) {
		children.remove(child);
	}
	
	public Vertex<P> getParent() {
		return parent;
	}

	public void setParent(Vertex<P> parent) {
		this.parent = parent;
	}
	
	public Collection<Vertex<P>> getChildren() {
		return children;
	}
	
	public void setCostToParent(double costToParent) {
		this.costToParent = costToParent;
	}
	
	public void setCostToRoot(double costToRoot) {
		this.costToRoot = costToRoot;
	}
	
	public double getCostToParent() {
		return costToParent;
	}
}
