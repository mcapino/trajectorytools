package cz.agents.alite.trajectorytools.graph.maneuver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jgrapht.Graph;
import org.jgrapht.graph.GraphDelegator;

import cz.agents.alite.trajectorytools.graph.spatialwaypoint.SpatialWaypoint;
import cz.agents.alite.trajectorytools.util.Point;

public class PlanarGraph<E> extends GraphDelegator<SpatialWaypoint, E> {
    private static final long serialVersionUID = -7039093249594157867L;

    public PlanarGraph(Graph<SpatialWaypoint, E> g) {
        super(g);
    }

    @Override
    public E addEdge(SpatialWaypoint sourceVertex, SpatialWaypoint targetVertex) {
        List<SpatialWaypoint> line = new LinkedList<SpatialWaypoint>(Arrays.asList(sourceVertex, targetVertex));

        //
        // Find intersections of edges with the line,
        // add there new points and split the edges.
        //

        List<E> toRemove = new ArrayList<E>();
        Map<E, SpatialWaypoint> toAdd = new HashMap<E, SpatialWaypoint>();
        for (E edge : edgeSet()) {

            SpatialWaypoint edgeSource = getEdgeSource(edge);
            SpatialWaypoint edgeTarget = getEdgeTarget(edge);
            SpatialWaypoint intersection = addLineIntersection(edgeSource, edgeTarget, line);

            if (intersection != null) {
                if (intersection.equals(edgeSource) || intersection.equals(edgeTarget)) {
                    // ok
                } else {
                    toRemove.add(edge);
                    addVertex(intersection);
                    toAdd.put( edge, intersection );
                }
            }
        }
        
        removeAllEdges(toRemove);

        for (Entry<E, SpatialWaypoint> entry : toAdd.entrySet()) {
            super.addEdge(getEdgeSource(entry.getKey()), entry.getValue());
            super.addEdge(entry.getValue(), getEdgeTarget(entry.getKey()));
        }

        SpatialWaypoint last = null;
        for (SpatialWaypoint vertex : line) {
            if (!containsVertex(vertex)) {
                addVertex(vertex);
            } 
            if (last != null) {
                super.addEdge(last, vertex);
                super.addEdge(vertex, last);
            }
            last = vertex;
        }

        return null; // multiple edges could be added
    }

    static SpatialWaypoint addLineIntersection(SpatialWaypoint point1, SpatialWaypoint point2, List<SpatialWaypoint> border) {
        SpatialWaypoint last = null;
        int index = 0;
        for (SpatialWaypoint vertex : border) {
            if (last != null) {
                SpatialWaypoint intersection = getIntersection(point1, point2, last, vertex);
                if (intersection != null) {
                    if (intersection.epsilonEquals(last, 0.001)) {
                        return last;
                    } else if (intersection.epsilonEquals(vertex, 0.001)) {
                        return vertex;
                    } else if (intersection.epsilonEquals(point1, 0.001)) {
                        border.add(index, point1);
                        return point1;
                    } else if (intersection.epsilonEquals(point2, 0.001)) {
                        border.add(index, point2);
                        return point2;
                    } else { 
                        border.add(index, intersection);
                        return intersection;
                    }
                }
            }
            last = vertex;
            index++;
        }
        return null;
    }

    /**
     * intersection in 2D
     * 
     * @param point1
     * @param point2
     * @param point3
     * @param point4
     * @return
     */
    static SpatialWaypoint getIntersection(Point point1, Point point2, Point point3, Point point4){
        double a1, a2, b1, b2, c1, c2;
        double r1, r2 , r3, r4;
        double denom;

        // Compute a1, b1, c1, where line joining points 1 and 2
        // is "a1 x + b1 y + c1 = 0".
        a1 = point2.y - point1.y;
        b1 = point1.x - point2.x;
        c1 = (point2.x * point1.y) - (point1.x * point2.y);

        // Compute r3 and r4.
        r3 = ((a1 * point3.x) + (b1 * point3.y) + c1);
        r4 = ((a1 * point4.x) + (b1 * point4.y) + c1);

        // Check signs of r3 and r4. If both point 3 and point 4 lie on
        // same side of line 1, the line segments do not intersect.
        if ((r3 != 0) && (r4 != 0) && same_sign(r3, r4)){
            return null;
        }

        // Compute a2, b2, c2
        a2 = point4.y - point3.y;
        b2 = point3.x - point4.x;
        c2 = (point4.x * point3.y) - (point3.x * point4.y);

        // Compute r1 and r2
        r1 = (a2 * point1.x) + (b2 * point1.y) + c2;
        r2 = (a2 * point2.x) + (b2 * point2.y) + c2;

        // Check signs of r1 and r2. If both point 1 and point 2 lie
        // on same side of second line segment, the line segments do
        // not intersect.
        if ((r1 != 0) && (r2 != 0) && (same_sign(r1, r2))){
            return null;
        }

        //Line segments intersect: compute intersection point.
        denom = (a1 * b2) - (a2 * b1);

        if (denom == 0) {
            return null;
        }

        return new SpatialWaypoint(((b1 * c2) - (b2 * c1)) / denom, ((a2 * c1) - (a1 * c2)) / denom);
    }

    static boolean same_sign(double a, double b){
        return (( a * b) >= 0);
    }
}