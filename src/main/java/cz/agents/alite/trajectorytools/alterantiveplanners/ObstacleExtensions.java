package cz.agents.alite.trajectorytools.alterantiveplanners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import cz.agents.alite.planner.spatialmaneuver.zone.BoxZone;
import cz.agents.alite.planner.spatialmaneuver.zone.TransformZone;
import cz.agents.alite.planner.spatialmaneuver.zone.Zone;
import cz.agents.alite.trajectorytools.graph.maneuver.CopyManeuverGraph;
import cz.agents.alite.trajectorytools.graph.maneuver.DefaultManeuver;
import cz.agents.alite.trajectorytools.graph.maneuver.ManeuverGraph;
import cz.agents.alite.trajectorytools.graph.maneuver.ObstacleGraphView;
import cz.agents.alite.trajectorytools.graph.spatialwaypoint.SpatialWaypoint;
import cz.agents.alite.trajectorytools.planner.PathPlanner;
import cz.agents.alite.trajectorytools.planner.PlannedPath;
import cz.agents.alite.trajectorytools.util.Point;

public class ObstacleExtensions {

    private static final int DIRECTIONS = 4;

    private final PathPlanner<SpatialWaypoint, DefaultManeuver> planner;
    
    public ObstacleExtensions(PathPlanner<SpatialWaypoint, DefaultManeuver> planner) {
        this.planner = planner;
    }
        
    public Collection<PlannedPath<SpatialWaypoint, DefaultManeuver>> planPath(ObstacleGraphView originalGraph, SpatialWaypoint startVertex, SpatialWaypoint endVertex) {
        final List<PlannedPath<SpatialWaypoint, DefaultManeuver>> paths = new ArrayList<PlannedPath<SpatialWaypoint, DefaultManeuver>>();
        
        ObstacleExtender obstacleExtender = new ObstacleExtender(originalGraph);

        for (ManeuverGraph graph : obstacleExtender) {
            PlannedPath<SpatialWaypoint, DefaultManeuver> path = planner.planPath(graph, startVertex, endVertex);
            if ( path != null && !contains(paths, path) ) {
                paths.add( path );
            }
        }
            
        return paths;
    }

    private boolean contains(Collection<PlannedPath<SpatialWaypoint,DefaultManeuver>> paths, PlannedPath<SpatialWaypoint,DefaultManeuver> path) {
        for (PlannedPath<SpatialWaypoint, DefaultManeuver> curPath : paths) {
            if ( equalsPath(path, curPath) ) {
                return true;
            }
        }
        return false;
    }

    private boolean equalsPath(PlannedPath<SpatialWaypoint, DefaultManeuver> path1, PlannedPath<SpatialWaypoint, DefaultManeuver> path2) {
        if (path1.getPathLength() != path2.getPathLength()) {
            return false;
        } else {
            return Arrays.equals(new ArrayList<DefaultManeuver>(path1.getEdgeList()).toArray(new DefaultManeuver[0]), new ArrayList<DefaultManeuver>(path2.getEdgeList()).toArray(new DefaultManeuver[0]));
        }
    }

    static class ObstacleExtender implements Iterable<ManeuverGraph>{

        int[] directions;
        private final ObstacleGraphView originalGraph;

        public ObstacleExtender(ObstacleGraphView originalGraph) {
            this.originalGraph = originalGraph;
            
            directions = new int[originalGraph.getObstacles().size()];
        }

        @Override
        public Iterator<ManeuverGraph> iterator() {
            return new Iterator<ManeuverGraph>() {

                boolean firstCall = true;

                @Override
                public boolean hasNext() {
                    for (int i = directions.length-1; i >= 0; i--) {
                        if ( directions[i] != DIRECTIONS-1 ) {
                            return true;
                        }
                    }
                    return false;
                }

                @Override
                public ManeuverGraph next() {
                    if ( !hasNext() ) {
                        throw new NoSuchElementException("No next element!");
                    }
                    
                    if ( !firstCall ) {
                        incrementDirections();
                    }
                    firstCall = false;
                   
                    return generateNextGraph();
                }

                protected ManeuverGraph generateNextGraph() {
                    ManeuverGraph graph = CopyManeuverGraph.create( originalGraph );

                    int currObstacle = 0;

                    for (Point obstacle : originalGraph.getObstacles()) {
                        removeObstacleExtension( graph, obstacle, directions[ currObstacle++ ] );
                    }
                    
                    return graph;
                    
                }

                private void removeObstacleExtension(ManeuverGraph graph, Point obstacle, int direction) {

                    Zone zone = createZone(obstacle, direction);
                    
                    for (SpatialWaypoint vertex : new ArrayList<SpatialWaypoint>(graph.vertexSet())) {
                        if ( zone.testPoint( vertex ) ) {
                            graph.removeVertex( vertex );
                        }
                    }

                    for (DefaultManeuver edge : new ArrayList<DefaultManeuver>(graph.edgeSet())) {
                        if ( zone.testLine(edge.getSource(), edge.getTarget(), null) ) {
                            graph.removeEdge(edge);
                        }
                    }
                }

                private Zone createZone(Point obstacle, int direction) {
                    Zone zone;
                    Zone boxZone = new BoxZone(new Vector3d(Double.MAX_VALUE, 0.2, 0.2));

                    Vector3d translation = new Vector3d(obstacle.x, obstacle.y, obstacle.z - 0.1);
                    switch (direction) {
                    case 0: // NORTH
                        translation.x -= 0.1;
                        zone = new TransformZone(boxZone, translation, new Vector2d(1, 1), -Math.PI/2);
                        break;
                    case 1: // EAST
                        translation.y -= 0.1;
                        zone = new TransformZone(boxZone, translation, new Vector2d(1, 1), 0);
                        break;
                    case 2: // SOUTH
                        translation.x +=0.1;
                        zone = new TransformZone(boxZone, translation, new Vector2d(1, 1), Math.PI/2);
                        break;
                    case 3: // WEST
                        translation.y += 0.1;
                        zone = new TransformZone(boxZone, translation, new Vector2d(1, 1), Math.PI);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown direction: " + direction);
                    }

                    return zone;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        private void incrementDirections() {
            directions[0] ++;
            int index = 0;
            while ( directions[index] >= DIRECTIONS ) {
                directions[index] = 0;
                index ++;
                directions[index] ++;
            }
        }
    }
}
