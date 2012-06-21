package cz.agents.alite.trajectorytools.graph.maneuver;

import org.jgrapht.graph.DefaultWeightedEdge;

import cz.agents.deconfliction.waypointgraph.DefaultWaypointGraph;
import cz.agents.deconfliction.waypointgraph.Waypoint;


@SuppressWarnings("serial")
public class CompleteGridWaypointGraph extends DefaultWaypointGraph {
    Waypoint waypoints[][];
    public CompleteGridWaypointGraph(double sizeX, double sizeY, int gridX, int gridY) {
        super();
        waypoints = new Waypoint[gridX+1][gridY+1];
        int waypointCounter = 0;

        double xStep = sizeX/gridX;
        double yStep = sizeY/gridY;

        // Generate vertices
        for (int x=0; x <= gridX; x++) {
            for (int y=0; y <= gridX; y++) {
                Waypoint w = new Waypoint(waypointCounter++,x*xStep, y*yStep);
                waypoints[x][y] = w;
                addVertex(w);
            }
        }

        // Generate edges of complete graph
        for (int x=0; x <= gridX; x++) {
            for (int y=0; y <= gridY; y++) {
                Waypoint v1 = waypoints[x][y];
                int starty = y+1;
                for (int x2=x; x2 <= gridX; x2++) {
                    for (int y2=starty; y2 <= gridY; y2++) {
                        Waypoint v2 = waypoints[x2][y2];
                        if (v1 != v2) {
                            DefaultWeightedEdge edge = addEdge(v1, v2);
                            setEdgeWeight(edge, v1.distance(v2));
                        }
                    }
                    starty = 0;
                }
            }
        }


    }
}
