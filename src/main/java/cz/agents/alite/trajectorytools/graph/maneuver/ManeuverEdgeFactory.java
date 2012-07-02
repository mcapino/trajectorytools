package cz.agents.alite.trajectorytools.graph.maneuver;

import org.jgrapht.EdgeFactory;

import cz.agents.alite.trajectorytools.graph.spatial.SpatialWaypoint;

class ManeuverEdgeFactory implements EdgeFactory<SpatialWaypoint, DefaultManeuver> {
    
	final double speed;
	final double waitManeuverDuration;	
	
	
	public ManeuverEdgeFactory(double speed, double waitManeuverDuration) {
		super();
		this.waitManeuverDuration = waitManeuverDuration;
		this.speed = speed;
	}

	@Override
    public DefaultManeuver createEdge(SpatialWaypoint sourceVertex, SpatialWaypoint targetVertex) {
        if (sourceVertex.equals(targetVertex)) {
        	return new DefaultManeuver(sourceVertex, targetVertex, waitManeuverDuration);
        } else {
        	return new DefaultManeuver(sourceVertex, targetVertex, sourceVertex.distance(targetVertex) / speed);
        }
    }
}