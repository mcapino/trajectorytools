package cz.agents.alite.trajectorytools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.vecmath.Matrix3d;

import org.jgrapht.alg.DirectedNeighborIndex;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;

import cz.agents.alite.trajectorytools.utils.BoundedInteger;
import cz.agents.alite.trajectorytools.utils.ManeuverEdge;
import cz.agents.alite.trajectorytools.utils.OrientedPoint;
import cz.agents.alite.trajectorytools.utils.Point;
import cz.agents.alite.trajectorytools.utils.Vector;

public class AirplaneManeuverGraph extends ListenableDirectedWeightedGraph<OrientedPoint, ManeuverEdge> {

    private static final long serialVersionUID = -1929824153316449669L;

    private static int ROTATIONAL_STEPS = 4;
    private static int SPATIAL_STEPS = 12;
    private static double RADIUS = 50;
    private static double CENTER_X = 750;
    private static double CENTER_Y = 750;

    private DirectedNeighborIndex<OrientedPoint, ManeuverEdge> neighborIndex;
    private OrientedPoint[][][] vertices = new OrientedPoint[ROTATIONAL_STEPS][2 * SPATIAL_STEPS + 1][2 * SPATIAL_STEPS + 1];;
    private double alpha;
    private double length;
    private Matrix3d rotation;

    public AirplaneManeuverGraph() {
        super(ManeuverEdge.class);

        prepareNeighborIndex();

        prepareHelperValues();
        generateVertices();
        generateEdges();
        removeOneWays(neighborIndex);
    }

    public DirectedNeighborIndex<OrientedPoint,ManeuverEdge> getNeighborIndex() {
        return neighborIndex;
    }

    public OrientedPoint getNearestVertex(OrientedPoint point) {
        List<OrientedPoint> vertices = new ArrayList<OrientedPoint>(vertexSet());
        OrientedPoint closest = vertices.iterator().next();

        for (OrientedPoint node : vertices){
            if (node.distance(point) < closest.distance(point)
              || ((node.distance(point) - closest.distance(point) < 1)
                  && (node.orientation.angle(point.orientation)) < closest.orientation.angle(point.orientation)))
            {
                closest = node;
            }
        }
        return closest;
    }

    private void prepareNeighborIndex() {
        neighborIndex = new DirectedNeighborIndex<OrientedPoint, ManeuverEdge>(this);
        addGraphListener(neighborIndex);
    }

    private void prepareHelperValues() {
        alpha = 2 * Math.PI / ROTATIONAL_STEPS;
        length = RADIUS * Math.tan(alpha / 2.0);
        rotation = new Matrix3d();
        rotation.rotZ(alpha);
    }

    private void generateVertices() {
        Vector primaryIncrement = new Vector(length, 0, 0);
        Vector secondaryIncrement = new Vector(primaryIncrement);
        rotation.transform(secondaryIncrement);

        for (int s = 0; s < ROTATIONAL_STEPS; s++) {
            Vector normalizedPrimaryIncrement = new Vector(primaryIncrement);
            normalizedPrimaryIncrement.normalize();

            Point currentPrimary = new Point(CENTER_X, CENTER_Y, 70);
            toInitialPosition(currentPrimary, primaryIncrement, secondaryIncrement, SPATIAL_STEPS);
            Point currentSecondary = new Point(currentPrimary);

            for (int j = -SPATIAL_STEPS; j <= SPATIAL_STEPS; j++) {
                for (int i = -SPATIAL_STEPS; i <= SPATIAL_STEPS; i++) {
                    vertices[s][i + SPATIAL_STEPS][j + SPATIAL_STEPS] = new OrientedPoint(new Point(currentPrimary), normalizedPrimaryIncrement);
                    addVertex(vertices[s][i + SPATIAL_STEPS][j + SPATIAL_STEPS]);

                    currentPrimary.add(primaryIncrement);
                }
                currentSecondary.add(secondaryIncrement);
                currentPrimary = new Point(currentSecondary);
            }

            rotation.transform(primaryIncrement);
            rotation.transform(secondaryIncrement);
        }
    }

    private void generateEdges() {
        for (int s = 0; s < ROTATIONAL_STEPS; s++) {
            for (int j = 0; j <= 2 * SPATIAL_STEPS; j++) {
                for (int i = 0; i <= 2 * SPATIAL_STEPS - 1; i++) {
                    // straight
                    ManeuverEdge maneuverEdge = ManeuverEdge.createStraightEdge(vertices[s][i][j], getDirectionByIndex(s), length);
                    addEdge(vertices[s][i][j], vertices[s][i + 1][j], maneuverEdge);
                    if (j + 1 <= 2 * SPATIAL_STEPS && 2 * SPATIAL_STEPS + 1 - i - 1 >= 0) {
                        // turn left
                        BoundedInteger newS = new BoundedInteger(s, ROTATIONAL_STEPS);
                        newS = newS.plus(+1);

                        maneuverEdge = ManeuverEdge.createTurnEdge(vertices[s][i][j], getDirectionByIndex(s), -RADIUS, alpha);
                        addEdge(vertices[s][i][j], vertices[newS.intValue()][j + 1][2 * SPATIAL_STEPS - i - 1], maneuverEdge);
                    }
                    if (i + 1 <= 2 * SPATIAL_STEPS && 2 * SPATIAL_STEPS - j + 1 <= 2 * SPATIAL_STEPS) {
                        // turn right
                        BoundedInteger newS = new BoundedInteger(s, ROTATIONAL_STEPS);
                        newS = newS.plus(-1);

                        maneuverEdge = ManeuverEdge.createTurnEdge(vertices[s][i][j], getDirectionByIndex(s), RADIUS, alpha);
                        addEdge(vertices[s][i][j], vertices[newS.intValue()][2 * SPATIAL_STEPS - j + 1][i + 1], maneuverEdge);
                    }
                }
            }
        }
    }

    private void removeOneWays(DirectedNeighborIndex<OrientedPoint, ManeuverEdge> neighborIndex) {
        boolean removedAtLeastOne;
        do {
            removedAtLeastOne = false;
            for (OrientedPoint orientedPoint : new HashSet<OrientedPoint>(vertexSet())) {
                if (neighborIndex.successorsOf(orientedPoint).isEmpty() || neighborIndex.predecessorsOf(orientedPoint).isEmpty()) {
                    removeVertex(orientedPoint);
                    removedAtLeastOne = true;
                }
            }
        } while(removedAtLeastOne);
    }

    private Vector getDirectionByIndex(int index) {
        Vector direction = new Vector(1, 0, 0);
        for (int i = 0; i < index; i++) {
            rotation.transform(direction);
        }
        return direction;
    }

    private void toInitialPosition(Point point, Vector primaryIncrement, Vector secondaryIncrement, int spatialSteps) {
        Vector primaryShift = new Vector(primaryIncrement);
        primaryShift.scale(-spatialSteps);
        point.add(primaryShift);

        Vector secondaryShift = new Vector(secondaryIncrement);
        secondaryShift.scale(-spatialSteps);
        point.add(secondaryShift);
    }

}
