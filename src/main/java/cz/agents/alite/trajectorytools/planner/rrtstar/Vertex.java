package cz.agents.alite.trajectorytools.planner.rrtstar;

import java.util.Collection;
import java.util.HashSet;

public class Vertex<P, E> {
    P point;
    Vertex<P, E> parent;
    Collection<Vertex<P, E>> children;

    E edgeFromParent;

    double costFromRoot;
    double costFromParent;

    public Vertex(P point) {
        super();
        this.point = point;
        this.parent = null;
        this.costFromRoot = 0.0;
        this.costFromParent = 0.0;
        this.children = new HashSet<Vertex<P, E>>();
    }

    public P getPoint() {
        return point;
    }

    public double getCostFromRoot() {
        return costFromRoot;
    }

    public void addChild(Vertex<P, E> child) {
        children.add(child);
    }

    public void removeChild(Vertex<P, E> child) {
        children.remove(child);
    }

    public Vertex<P,E> getParent() {
        return parent;
    }

    public void setParent(Vertex<P,E> parent) {
        this.parent = parent;
    }

    public Collection<Vertex<P,E>> getChildren() {
        return children;
    }

    public void setCostFromParent(double costToParent) {
        this.costFromParent = costToParent;
    }

    public void setCostFromRoot(double costToRoot) {
        this.costFromRoot = costToRoot;
    }

    public double getCostFromParent() {
        return costFromParent;
    }

    @Override
    public String toString() {
        return "(" + point + ", toRoot=" + costFromRoot
                + ", toParent=" + costFromParent + ")";
    }

    public E getEdgeFromParent() {
        return edgeFromParent;
    }

    public void setEdgeFromParent(E edgeFromParent) {
        this.edgeFromParent = edgeFromParent;
    }

}
