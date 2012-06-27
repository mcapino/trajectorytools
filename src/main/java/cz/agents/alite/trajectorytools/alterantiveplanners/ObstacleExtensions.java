package cz.agents.alite.trajectorytools.alterantiveplanners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import cz.agents.alite.planner.spatialmaneuver.zone.BoxZone;
import cz.agents.alite.planner.spatialmaneuver.zone.TransformZone;
import cz.agents.alite.planner.spatialmaneuver.zone.Zone;
import cz.agents.alite.trajectorytools.graph.maneuver.CopyManeuverGraph;
import cz.agents.alite.trajectorytools.graph.maneuver.Maneuver;
import cz.agents.alite.trajectorytools.graph.maneuver.ManeuverGraph;
import cz.agents.alite.trajectorytools.graph.maneuver.ObstacleGraphView;
import cz.agents.alite.trajectorytools.graph.spatialwaypoint.SpatialWaypoint;
import cz.agents.alite.trajectorytools.planner.PathPlanner;
import cz.agents.alite.trajectorytools.planner.PlannedPath;
import cz.agents.alite.trajectorytools.util.Point;

public class ObstacleExtensions {

    private static final int DIRECTIONS = 4;

    private final PathPlanner<SpatialWaypoint, Maneuver> planner;
    
    public ObstacleExtensions(PathPlanner<SpatialWaypoint, Maneuver> planner) {
        this.planner = planner;
    }
<<<<<<< local
        
<<<<<<< local
=======
    public Collection<PlannedPath<SpatialWaypoint, DefaultManeuver>> planPath(ObstacleGraphView originalGraph, SpatialWaypoint startVertex, SpatialWaypoint endVertex) {
        final List<PlannedPath<SpatialWaypoint, DefaultManeuver>> paths = new ArrayList<PlannedPath<SpatialWaypoint, DefaultManeuver>>();
=======

>>>>>>> other
    public Collection<PlannedPath<SpatialWaypoint, Maneuver>> planPath(ObstacleGraphView originalGraph, SpatialWaypoint startVertex, SpatialWaypoint endVertex) {
<<<<<<< local
        final List<PlannedPath<SpatialWaypoint, Maneuver>> paths = new ArrayList<PlannedPath<SpatialWaypoint, Maneuver>>();
=======
        final Set<PlannedPath<SpatialWaypoint, Maneuver>> paths = new HashSet<PlannedPath<SpatialWaypoint, Maneuver>>();
>>>>>>> other
>>>>>>> other
        
        ObstacleExtender obstacleExtender = new ObstacleExtender(originalGraph);

        for (ManeuverGraph graph : obstacleExtender) {
<<<<<<< local
            PlannedPath<SpatialWaypoint, Maneuver> path = planner.planPath(graph, startVertex, endVertex);
=======
<<<<<<< local
            PlannedPath<SpatialWaypoint, DefaultManeuver> path = planner.planPath(graph, startVertex, endVertex);
>>>>>>> other
            if ( path != null && !contains(paths, path) ) {
=======
            PlannedPath<SpatialWaypoint, Maneuver> path = planner.planPath(graph, startVertex, endVertex);
            if ( path != null ) {
>>>>>>> other
                paths.add( path );
            }
        }
            
        return paths;
    }

<<<<<<< local
    private boolean contains(Collection<PlannedPath<SpatialWaypoint,Maneuver>> paths, PlannedPath<SpatialWaypoint,Maneuver> path) {
        for (PlannedPath<SpatialWaypoint, Maneuver> curPath : paths) {
=======
<<<<<<< local
    private boolean contains(Collection<PlannedPath<SpatialWaypoint,DefaultManeuver>> paths, PlannedPath<SpatialWaypoint,DefaultManeuver> path) {
        for (PlannedPath<SpatialWaypoint, DefaultManeuver> curPath : paths) {
>>>>>>> other
            if ( equalsPath(path, curPath) ) {
                return true;
            }
        }
        return false;
    }

    private boolean equalsPath(PlannedPath<SpatialWaypoint, Maneuver> path1, PlannedPath<SpatialWaypoint, Maneuver> path2) {
        if (path1.getPathLength() != path2.getPathLength()) {
            return false;
        } else {
            return Arrays.equals(new ArrayList<Maneuver>(path1.getEdgeList()).toArray(new Maneuver[0]), new ArrayList<Maneuver>(path2.getEdgeList()).toArray(new Maneuver[0]));
        }
    }

=======
>>>>>>> other
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

                    for (Maneuver edge : new ArrayList<Maneuver>(graph.edgeSet())) {
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
