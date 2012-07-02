package cz.agents.alite.trajectorytools.graph.maneuver;

import org.jgrapht.EdgeFactory;

import cz.agents.alite.trajectorytools.graph.spatial.SpatialWaypoint;
import cz.agents.alite.trajectorytools.graph.spatial.DefaultSpatialManeuverGraph;

@SuppressWarnings("serial")
public class ManeuverGraph extends DefaultSpatialManeuverGraph<DefaultManeuver> implements ManeuverGraphInterface {

	double maxSpeed;
	
    public ManeuverGraph(double maxSpeed) {
        super(new ManeuverEdgeFactory(maxSpeed, maxSpeed));
        this.maxSpeed = maxSpeed;
    }
    
    public ManeuverGraph(double maxSpeed, EdgeFactory<SpatialWaypoint, DefaultManeuver> edgeFactory) {
        super(edgeFactory);
        this.maxSpeed = maxSpeed;
    }

    public ManeuverGraph(ManeuverGraphInterface graph) {
    	super(new ManeuverEdgeFactory(graph.getMaxSpeed(), graph.getMaxSpeed()));
		maxSpeed = graph.getMaxSpeed();
	}
    
    public double getDuration(DefaultManeuver m) {
        return getEdgeWeight(m);
    }
    
    public double getMaxSpeed() {
		return maxSpeed;
	}
}
