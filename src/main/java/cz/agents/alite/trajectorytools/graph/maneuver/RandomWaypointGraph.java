package cz.agents.alite.trajectorytools.graph.maneuver;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.jgrapht.graph.DefaultWeightedEdge;

import cz.agents.alite.trajectorytools.graph.spatialwaypoint.SpatialWaypoint;


@SuppressWarnings("serial")
public class RandomWaypointGraph extends ManeuverGraph {
    SpatialWaypoint waypoints[];
    public RandomWaypointGraph(double sizeX, double sizeY, int nPoints, int branchFactor, int seed) {
        super(1.0);
        waypoints = new SpatialWaypoint[nPoints];
        int waypointCounter = 0;
        Random random = new Random(seed);

        // Generate vertices
        for (int i=0; i < nPoints; i++) {
                SpatialWaypoint w = new SpatialWaypoint(waypointCounter++, random.nextDouble()*sizeX, random.nextDouble()*sizeY);
                waypoints[i] = w;
                addVertex(w);
        }

        // Generate edges
        for (int i=0; i < nPoints; i++) {

            // Find a given number of closest vertices
            Set<SpatialWaypoint> neighbors = new HashSet<SpatialWaypoint>();
            for (int n=0; n < branchFactor; n++) {

                // Find the closest point
                SpatialWaypoint closestWaypoint = null;
                Double closestDist = Double.MAX_VALUE;

                for (int j = 0; j < nPoints; j++) {
                    if (waypoints[i].distance(waypoints[j]) < closestDist
                            && i != j
                            && !neighbors.contains(waypoints[j])
                    ) {
                        closestDist = waypoints[i].distance(waypoints[j]);
                        closestWaypoint = waypoints[j];
                    }
                }
                neighbors.add(closestWaypoint);
            }

            for (SpatialWaypoint neighbor : neighbors) {
                if (getEdge(waypoints[i], neighbor) == null) {
                    Maneuver maneuver = addEdge(waypoints[i], neighbor);
                    if (maneuver != null)
                        setEdgeWeight(maneuver, maneuver.getDuration());
                }
            }
        }
    }
}
