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
import cz.agents.alite.trajectorytools.graph.ObstacleGraphView;
import cz.agents.alite.trajectorytools.graph.spatial.SpatialManeuverGraph;
import cz.agents.alite.trajectorytools.graph.spatial.SpatialGraphs;
import cz.agents.alite.trajectorytools.graph.spatial.SpatialManeuverGraphs;
import cz.agents.alite.trajectorytools.graph.spatial.SpatialWaypoint;
import cz.agents.alite.trajectorytools.graph.spatial.maneuvers.SpatialManeuver;
import cz.agents.alite.trajectorytools.planner.PathPlanner;
import cz.agents.alite.trajectorytools.planner.PlannedPath;
import cz.agents.alite.trajectorytools.util.Point;

public class ObstacleExtensions {

    private static final int DIRECTIONS = 4;

    private final PathPlanner<SpatialWaypoint, SpatialManeuver> planner;
    
    public ObstacleExtensions(PathPlanner<SpatialWaypoint, SpatialManeuver> planner) {
        this.planner = planner;
    }

    public Collection<PlannedPath<SpatialWaypoint, SpatialManeuver>> planPath(ObstacleGraphView originalGraph, SpatialWaypoint startVertex, SpatialWaypoint endVertex) {
        final Set<PlannedPath<SpatialWaypoint, SpatialManeuver>> paths = new HashSet<PlannedPath<SpatialWaypoint, SpatialManeuver>>();
        
        ObstacleExtender obstacleExtender = new ObstacleExtender(originalGraph);

        for (SpatialManeuverGraph graph : obstacleExtender) {
            PlannedPath<SpatialWaypoint, SpatialManeuver> path = planner.planPath(graph, startVertex, endVertex);

            if ( path != null ) {
                paths.add( path );
            }
        }
            
        return paths;
    }

    static class ObstacleExtender implements Iterable<SpatialManeuverGraph>{

        int[] directions;
        private final ObstacleGraphView<SpatialManeuver> originalGraph;

        public ObstacleExtender(ObstacleGraphView<SpatialManeuver> originalGraph) {
            this.originalGraph = originalGraph;
            
            directions = new int[originalGraph.getObstacles().size()];
        }

        @Override
        public Iterator<SpatialManeuverGraph> iterator() {
            return new Iterator<SpatialManeuverGraph>() {

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
                public SpatialManeuverGraph next() {
                    if ( !hasNext() ) {
                        throw new NoSuchElementException("No next element!");
                    }
                    
                    if ( !firstCall ) {
                        incrementDirections();
                    }
                    firstCall = false;
                   
                    return generateNextGraph();
                }

                protected SpatialManeuverGraph generateNextGraph() {
                    SpatialManeuverGraph graph = (SpatialManeuverGraph) SpatialGraphs.clone(originalGraph);

                    int currObstacle = 0;

                    for (Point obstacle : originalGraph.getObstacles()) {
                        removeObstacleExtension( graph, obstacle, directions[ currObstacle++ ] );
                    }
                    
                    return graph;
                    
                }

                private void removeObstacleExtension(SpatialManeuverGraph graph, Point obstacle, int direction) {

                    Zone zone = createZone(obstacle, direction);
                    
                    for (SpatialWaypoint vertex : new ArrayList<SpatialWaypoint>(graph.vertexSet())) {
                        if ( zone.testPoint( vertex ) ) {
                            graph.removeVertex( vertex );
                        }
                    }

                    for (SpatialManeuver edge : new ArrayList<SpatialManeuver>(graph.edgeSet())) {
                        if ( zone.testLine(graph.getEdgeSource(edge), graph.getEdgeTarget(edge), null) ) {
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
