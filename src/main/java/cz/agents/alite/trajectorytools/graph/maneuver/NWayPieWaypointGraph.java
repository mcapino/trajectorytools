package cz.agents.alite.trajectorytools.graph.maneuver;

import cz.agents.deconfliction.util.Point;
import cz.agents.deconfliction.waypointgraph.Waypoint;

@SuppressWarnings("serial")
public abstract class NWayPieWaypointGraph extends ManeuverGraph {
    Waypoint waypoints[][];

    protected abstract int[][] getEdgePattern();

    public NWayPieWaypointGraph(Point center, double radius, int parts, int layers) {
        super(1.0);
        int waypointCounter = 0;

        Waypoint centerwp = new Waypoint(waypointCounter++, center.x, center.y);
        addVertex(centerwp);
        waypoints = new Waypoint[parts][layers];
        for (int i=0; i < parts; i++) {
            waypoints[i][0] = centerwp;
        }

        double angleStep = 2 * Math.PI / parts;
        double layerStep = radius / (layers-1);

        // Generate vertices
        for (int i=0; i < parts; i++) {
            for (int j=1; j < layers; j++) {
                Waypoint w = new Waypoint(waypointCounter++, center.x + Math.cos(i * angleStep) * j * layerStep,
                        center.y + Math.sin(i * angleStep) * j * layerStep );
                waypoints[i][j] = w;
                addVertex(w);
            }
        }

        // Generate edges
        for (int i=0; i < parts; i++) {
            for (int j=0; j < layers; j++) {
                Waypoint v1 = waypoints[i][j];
                     for (int[] edgeOffset : getEdgePattern()) {
                         int destPart = i + edgeOffset[0];
                         int destLayer = j + edgeOffset[1];

                         if (destPart == parts) destPart = 0;
                         if (destPart == -1) destPart = parts-1;

                         if (destPart >= 0 && destPart < parts && destLayer >= 0 && destLayer < layers) {
                                Waypoint v2 = waypoints[destPart][destLayer];
                                if (!v2.equals(v1)) { // Avoid connecting center with itself
                                    Maneuver edge = addEdge(v1, v2);
                                    if (edge != null)
                                        setEdgeWeight(edge, edge.getDuration());
                                }
                         }
                     }
                }
            }
        }

}
