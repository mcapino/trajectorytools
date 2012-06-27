package cz.agents.alite.trajectorytools.graph.maneuver;

import org.jgrapht.EdgeFactory;

import cz.agents.alite.trajectorytools.graph.spatialwaypoint.SpatialWaypoint;

class ManeuverEdgeFactory implements EdgeFactory<SpatialWaypoint, Maneuver> {
    
	final double speed;
	final double waitManeuverDuration;	
	
	
	public ManeuverEdgeFactory(double speed, double waitManeuverDuration) {
		super();
		this.waitManeuverDuration = waitManeuverDuration;
		this.speed = speed;
	}

	@Override
    public Maneuver createEdge(SpatialWaypoint sourceVertex, SpatialWaypoint targetVertex) {
        if (sourceVertex.equals(targetVertex)) {
        	return new Maneuver(waitManeuverDuration);
        } else {
        	return new Maneuver(sourceVertex.distance(targetVertex) / speed);
        }
    }
}