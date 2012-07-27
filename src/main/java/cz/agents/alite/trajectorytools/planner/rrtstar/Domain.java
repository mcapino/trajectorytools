package cz.agents.alite.trajectorytools.planner.rrtstar;

public interface Domain<P,E> {
	P getRandomSample();
	Extension<P,E> extendTo(P from, P to);	
	boolean isVisible(P p1, P p2);
	double evaluateExtensionCost(P p1, P p2);
	double distance(P p1, P p2);
	double nDimensions();
	boolean isInTargetRegion(P p);
}
