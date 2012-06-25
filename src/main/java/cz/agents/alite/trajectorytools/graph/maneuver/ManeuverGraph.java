package cz.agents.alite.trajectorytools.graph.maneuver;

import org.jgrapht.EdgeFactory;

import cz.agents.alite.trajectorytools.graph.spatialwaypoint.SpatialWaypoint;
import cz.agents.alite.trajectorytools.graph.spatialwaypoint.WaypointGraph;

@SuppressWarnings("serial")
public class ManeuverGraph extends WaypointGraph<Maneuver> {

	double maxSpeed;
	
    public ManeuverGraph(double maxSpeed) {
        super(new ManeuverEdgeFactory(maxSpeed, maxSpeed));
        this.maxSpeed = maxSpeed;
    }
    
    public ManeuverGraph(double maxSpeed, EdgeFactory<SpatialWaypoint, Maneuver> edgeFactory) {
        super(edgeFactory);
        this.maxSpeed = maxSpeed;
    }

    public ManeuverGraph(ManeuverGraph graph) {
    	super(new ManeuverEdgeFactory(graph.maxSpeed, graph.maxSpeed));
		maxSpeed = graph.maxSpeed;
	}
    
    public double getDuration(Maneuver m) {
        return getEdgeWeight(m);
    }
    
    public double getMaxSpeed() {
		return maxSpeed;
	}
}
