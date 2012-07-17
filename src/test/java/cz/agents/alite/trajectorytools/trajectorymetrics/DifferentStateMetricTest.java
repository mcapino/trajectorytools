package cz.agents.alite.trajectorytools.trajectorymetrics;

import static junit.framework.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import cz.agents.alite.trajectorytools.graph.maneuver.FourWayConstantSpeedGridGraph;
import cz.agents.alite.trajectorytools.graph.maneuver.Maneuver;
import cz.agents.alite.trajectorytools.graph.maneuver.ManeuverGraphInterface;
import cz.agents.alite.trajectorytools.graph.spatialwaypoint.SpatialWaypoint;
import cz.agents.alite.trajectorytools.planner.PlannedPath;
import cz.agents.alite.trajectorytools.planner.PlannedPathImpl;
import cz.agents.alite.trajectorytools.planner.SingleVertexPlannedPath;
import cz.agents.alite.trajectorytools.util.Point;


public class DifferentStateMetricTest {

    private static final int WORLD_SIZE = 10;
    private ManeuverGraphInterface graph;
    private PlannedPath<SpatialWaypoint, Maneuver> path;
    private SpatialWaypoint startVertex;
    private SpatialWaypoint endVertex;
    private ManeuverTrajectoryMetric metric;

    @Before
    public void setup() {
        graph = FourWayConstantSpeedGridGraph.create(WORLD_SIZE, WORLD_SIZE, WORLD_SIZE, WORLD_SIZE, 1.0);
        startVertex = graph.getNearestWaypoint(new Point(2, 2, 0));
        endVertex = graph.getNearestWaypoint(new Point(5, 5, 0));
        List<Maneuver> edges = Arrays.asList(
                graph.getEdge(
                        startVertex, 
                        graph.getNearestWaypoint(new Point(3, 2, 0))
                        ),
                graph.getEdge(
                        graph.getNearestWaypoint(new Point(3, 2, 0)),
                        graph.getNearestWaypoint(new Point(4, 2, 0))
                        ),
                graph.getEdge(
                        graph.getNearestWaypoint(new Point(4, 2, 0)),
                        graph.getNearestWaypoint(new Point(5, 2, 0))
                        ),
                graph.getEdge(
                        graph.getNearestWaypoint(new Point(5, 2, 0)),
                        graph.getNearestWaypoint(new Point(5, 3, 0))
                        ),
                graph.getEdge(
                        graph.getNearestWaypoint(new Point(5, 3, 0)),
                        graph.getNearestWaypoint(new Point(5, 4, 0))
                        ),
                graph.getEdge(
                        graph.getNearestWaypoint(new Point(5, 4, 0)),
                        endVertex
                        )
                );
        path = new PlannedPathImpl<SpatialWaypoint, Maneuver>(graph, edges);
        
        metric = new DifferentStateMetric();
    }
    
    @Test
    public void testSetup() {
        assertEquals(6.0, path.getWeight(), 0.001);
        assertEquals(6.0, path.getPathLength(), 0.001);
    }

    @Test
    public void testSingleVertexPathOut() {
        SingleVertexPlannedPath otherPath = new SingleVertexPlannedPath(graph,
                graph.getNearestWaypoint(new Point(2, 5, 0))
                );
        
        assertEquals(1, metric.getTrajectoryDistance(otherPath, path), 0.001);
    }

    @Test
    public void testSingleVertexPathStart() {
        SingleVertexPlannedPath otherPath = new SingleVertexPlannedPath(graph, startVertex);
        
        assertEquals(0, metric.getTrajectoryDistance(otherPath, path), 0.001);
    }

    @Test
    public void testSingleVertexPathMid() {
        SingleVertexPlannedPath otherPath = new SingleVertexPlannedPath(graph,
                graph.getNearestWaypoint(new Point(5, 2, 0))
                );
        
        assertEquals(0, metric.getTrajectoryDistance(otherPath, path), 0.001);
    }
    
    @Test
    public void testSingleVertexPathEnd() {
        SingleVertexPlannedPath otherPath = new SingleVertexPlannedPath(graph, endVertex);
        
        assertEquals(0, metric.getTrajectoryDistance(otherPath, path), 0.001);
    }

    @Test
    public void testPathStart() {
        List<Maneuver> edges = Arrays.asList(
                graph.getEdge(
                        graph.getNearestWaypoint(new Point(3, 2, 0)),
                        graph.getNearestWaypoint(new Point(3, 3, 0))
                        ),
                graph.getEdge(
                        graph.getNearestWaypoint(new Point(3, 3, 0)),
                        graph.getNearestWaypoint(new Point(3, 4, 0))
                        )
                );

        PlannedPathImpl<SpatialWaypoint, Maneuver> otherPath = new PlannedPathImpl<SpatialWaypoint, Maneuver>(
                graph, 
                edges 
                );
        assertEquals(2/3.0, metric.getTrajectoryDistance(otherPath, path), 0.001);
    }

    @Test
    public void testPathMid() {
        List<Maneuver> edges = Arrays.asList(
                graph.getEdge(
                        graph.getNearestWaypoint(new Point(3, 1, 0)),
                        graph.getNearestWaypoint(new Point(3, 2, 0))
                        ),
                graph.getEdge(
                        graph.getNearestWaypoint(new Point(3, 2, 0)),
                        graph.getNearestWaypoint(new Point(3, 3, 0))
                        )
                );

        PlannedPathImpl<SpatialWaypoint, Maneuver> otherPath = new PlannedPathImpl<SpatialWaypoint, Maneuver>(
                graph, 
                edges 
                );
        assertEquals(2/3.0, metric.getTrajectoryDistance(otherPath, path), 0.001);
    }

    @Test
    public void testPathEnd() {
        List<Maneuver> edges = Arrays.asList(
                graph.getEdge(
                        graph.getNearestWaypoint(new Point(3, 0, 0)),
                        graph.getNearestWaypoint(new Point(3, 1, 0))
                        ),
                graph.getEdge(
                        graph.getNearestWaypoint(new Point(3, 1, 0)),
                        graph.getNearestWaypoint(new Point(3, 2, 0))
                        )
                );

        PlannedPathImpl<SpatialWaypoint, Maneuver> otherPath = new PlannedPathImpl<SpatialWaypoint, Maneuver>(
                graph, 
                edges 
                );
        assertEquals(2/3.0, metric.getTrajectoryDistance(otherPath, path), 0.001);
    }

    @Test
    public void testPathCorner() {
        List<Maneuver> edges = Arrays.asList(
                graph.getEdge(
                        graph.getNearestWaypoint(new Point(5, 1, 0)),
                        graph.getNearestWaypoint(new Point(5, 2, 0))
                        ),
                graph.getEdge(
                        graph.getNearestWaypoint(new Point(5, 2, 0)),
                        graph.getNearestWaypoint(new Point(6, 2, 0))
                        )
                );

        PlannedPathImpl<SpatialWaypoint, Maneuver> otherPath = new PlannedPathImpl<SpatialWaypoint, Maneuver>(
                graph, 
                edges 
                );
        assertEquals(2/3.0, metric.getTrajectoryDistance(otherPath, path), 0.001);
    }

    @Test
    public void testTwoPaths() {
        List<Maneuver> edges1 = Arrays.asList(
                graph.getEdge(
                        graph.getNearestWaypoint(new Point(1, 1, 0)),
                        graph.getNearestWaypoint(new Point(1, 2, 0))
                        ),
                graph.getEdge(
                        graph.getNearestWaypoint(new Point(1, 2, 0)),
                        graph.getNearestWaypoint(new Point(2, 2, 0))
                        ),
                graph.getEdge(
                        graph.getNearestWaypoint(new Point(2, 2, 0)),
                        graph.getNearestWaypoint(new Point(2, 3, 0))
                        ),
                graph.getEdge(
                        graph.getNearestWaypoint(new Point(2, 3, 0)),
                        graph.getNearestWaypoint(new Point(3, 3, 0))
                        )
                );
        List<Maneuver> edges2 = Arrays.asList(
                graph.getEdge(
                        graph.getNearestWaypoint(new Point(1, 1, 0)),
                        graph.getNearestWaypoint(new Point(2, 1, 0))
                        ),
                graph.getEdge(
                        graph.getNearestWaypoint(new Point(2, 1, 0)),
                        graph.getNearestWaypoint(new Point(2, 2, 0))
                        ),
                graph.getEdge(
                        graph.getNearestWaypoint(new Point(2, 2, 0)),
                        graph.getNearestWaypoint(new Point(3, 2, 0))
                        ),
                graph.getEdge(
                        graph.getNearestWaypoint(new Point(3, 2, 0)),
                        graph.getNearestWaypoint(new Point(3, 3, 0))
                        )
                );

        PlannedPathImpl<SpatialWaypoint, Maneuver> path1 = new PlannedPathImpl<SpatialWaypoint, Maneuver>(
                graph, 
                edges1 
                );
        
        PlannedPathImpl<SpatialWaypoint, Maneuver> path2 = new PlannedPathImpl<SpatialWaypoint, Maneuver>(
                graph, 
                edges2 
                );
        
        assertEquals(2/5.0, metric.getTrajectoryDistance(path1, path2), 0.001);
    }
}
