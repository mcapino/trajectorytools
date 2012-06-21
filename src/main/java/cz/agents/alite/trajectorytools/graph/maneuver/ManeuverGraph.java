package cz.agents.alite.trajectorytools.graph.maneuver;

import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.SimpleWeightedGraph;

import cz.agents.deconfliction.waypointgraph.Waypoint;
import cz.agents.deconfliction.waypointgraph.WaypointGraph;

@SuppressWarnings("serial")
public class ManeuverGraph extends WaypointGraph<Maneuver> {

	double maxSpeed;
	
    public ManeuverGraph(double maxSpeed) {
        super(new ManeuverEdgeFactory(maxSpeed, maxSpeed));
        this.maxSpeed = maxSpeed;
    }
    
    public ManeuverGraph(double maxSpeed, EdgeFactory<Waypoint, Maneuver> edgeFactory) {
        super(edgeFactory);
        this.maxSpeed = maxSpeed;
    }

    public double getDuration(Maneuver m) {
        return getEdgeWeight(m);
    }
    
    public double getMaxSpeed() {
		return maxSpeed;
	}
}
