package cz.agents.alite.trajectorytools.trajectorymetrics;

import static junit.framework.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.jgrapht.Graph;
import org.junit.Before;
import org.junit.Test;

import cz.agents.alite.trajectorytools.graph.spatial.SpatialGraphs;
import cz.agents.alite.trajectorytools.graph.spatial.SpatialGridFactory;
import cz.agents.alite.trajectorytools.graph.spatial.maneuvers.SpatialManeuver;
import cz.agents.alite.trajectorytools.planner.PlannedPath;
import cz.agents.alite.trajectorytools.planner.PlannedPathImpl;
import cz.agents.alite.trajectorytools.planner.SingleVertexPlannedPath;
import cz.agents.alite.trajectorytools.util.Point;
import cz.agents.alite.trajectorytools.util.Waypoint;


public class TrajecotryDistanceMetricTest {

    private static final int WORLD_SIZE = 10;
    private Graph<Waypoint, SpatialManeuver> graph;
    private PlannedPath<Waypoint, SpatialManeuver> path;
    private Waypoint startVertex;
    private Waypoint endVertex;
    private TrajectoryMetric<Waypoint, SpatialManeuver> metric;

    @Before
    public void setup() {
        graph = SpatialGridFactory.create4WayGrid(WORLD_SIZE, WORLD_SIZE, WORLD_SIZE, WORLD_SIZE, 1.0);
        startVertex = SpatialGraphs.getNearestVertex(graph, new Point(2, 2, 0));
        endVertex = SpatialGraphs.getNearestVertex(graph, new Point(5, 5, 0));
        List<SpatialManeuver> edges = Arrays.asList(
                graph.getEdge(
                        startVertex,
                        SpatialGraphs.getNearestVertex(graph, new Point(3, 2, 0))
                        ),
                graph.getEdge(
                        SpatialGraphs.getNearestVertex(graph, new Point(3, 2, 0)),
                        SpatialGraphs.getNearestVertex(graph, new Point(4, 2, 0))
                        ),
                graph.getEdge(
                        SpatialGraphs.getNearestVertex(graph, new Point(4, 2, 0)),
                        SpatialGraphs.getNearestVertex(graph, new Point(5, 2, 0))
                        ),
                graph.getEdge(
                        SpatialGraphs.getNearestVertex(graph, new Point(5, 2, 0)),
                        SpatialGraphs.getNearestVertex(graph, new Point(5, 3, 0))
                        ),
                graph.getEdge(
                        SpatialGraphs.getNearestVertex(graph, new Point(5, 3, 0)),
                        SpatialGraphs.getNearestVertex(graph, new Point(5, 4, 0))
                        ),
                graph.getEdge(
                        SpatialGraphs.getNearestVertex(graph, new Point(5, 4, 0)),
                        endVertex
                        )
                );
        path = new PlannedPathImpl<Waypoint, SpatialManeuver>(graph, edges);

        metric = new TrajectoryDistanceMetric<Waypoint, SpatialManeuver>();
    }

    @Test
    public void testSetup() {
        assertEquals(6.0, path.getWeight(), 0.001);
        assertEquals(6.0, path.getPathLength(), 0.001);
    }

    @Test
    public void testZeroDistance() {
        assertEquals(0.0, metric.getTrajectoryDistance(path, path), 0.001);
    }

    @Test
    public void testSingleVertexPathStart() {
        SingleVertexPlannedPath<Waypoint, SpatialManeuver> otherPath = new SingleVertexPlannedPath<Waypoint, SpatialManeuver>(graph, startVertex);

        assertEquals(0, metric.getTrajectoryDistance(otherPath, path), 0.001);
    }

    @Test
    public void testSingleVertexPathMid() {
        SingleVertexPlannedPath<Waypoint, SpatialManeuver> otherPath = new SingleVertexPlannedPath<Waypoint, SpatialManeuver>(graph,
                SpatialGraphs.getNearestVertex(graph, new Point(5, 2, 0))
                );

        assertEquals(0, metric.getTrajectoryDistance(otherPath, path), 0.001);
    }

    @Test
    public void testSingleVertexPathEnd() {
        SingleVertexPlannedPath<Waypoint, SpatialManeuver> otherPath = new SingleVertexPlannedPath<Waypoint, SpatialManeuver>(graph, endVertex);

        assertEquals(0, metric.getTrajectoryDistance(otherPath, path), 0.001);
    }

    @Test
    public void testPathStart() {
        List<SpatialManeuver> edges = Arrays.asList(
                graph.getEdge(
                        SpatialGraphs.getNearestVertex(graph, new Point(3, 2, 0)),
                        SpatialGraphs.getNearestVertex(graph, new Point(3, 3, 0))
                        ),
                graph.getEdge(
                        SpatialGraphs.getNearestVertex(graph, new Point(3, 3, 0)),
                        SpatialGraphs.getNearestVertex(graph, new Point(3, 4, 0))
                        )
                );

        PlannedPathImpl<Waypoint, SpatialManeuver> otherPath = new PlannedPathImpl<Waypoint, SpatialManeuver>(
                graph,
                edges
                );
        assertEquals(3, metric.getTrajectoryDistance(otherPath, path), 0.001);
    }

    @Test
    public void testPathMid() {
        List<SpatialManeuver> edges = Arrays.asList(
                graph.getEdge(
                        SpatialGraphs.getNearestVertex(graph, new Point(3, 1, 0)),
                        SpatialGraphs.getNearestVertex(graph, new Point(3, 2, 0))
                        ),
                graph.getEdge(
                        SpatialGraphs.getNearestVertex(graph, new Point(3, 2, 0)),
                        SpatialGraphs.getNearestVertex(graph, new Point(3, 3, 0))
                        )
                );

        PlannedPathImpl<Waypoint, SpatialManeuver> otherPath = new PlannedPathImpl<Waypoint, SpatialManeuver>(
                graph,
                edges
                );
        assertEquals(2, metric.getTrajectoryDistance(otherPath, path), 0.001);
    }

    @Test
    public void testPathEnd() {
        List<SpatialManeuver> edges = Arrays.asList(
                graph.getEdge(
                        SpatialGraphs.getNearestVertex(graph, new Point(3, 0, 0)),
                        SpatialGraphs.getNearestVertex(graph, new Point(3, 1, 0))
                        ),
                graph.getEdge(
                        SpatialGraphs.getNearestVertex(graph, new Point(3, 1, 0)),
                        SpatialGraphs.getNearestVertex(graph, new Point(3, 2, 0))
                        )
                );

        PlannedPathImpl<Waypoint, SpatialManeuver> otherPath = new PlannedPathImpl<Waypoint, SpatialManeuver>(
                graph,
                edges
                );
        assertEquals(3, metric.getTrajectoryDistance(otherPath, path), 0.001);
    }

    @Test
    public void testPathCorner() {
        List<SpatialManeuver> edges = Arrays.asList(
                graph.getEdge(
                        SpatialGraphs.getNearestVertex(graph, new Point(5, 1, 0)),
                        SpatialGraphs.getNearestVertex(graph, new Point(5, 2, 0))
                        ),
                graph.getEdge(
                        SpatialGraphs.getNearestVertex(graph, new Point(5, 2, 0)),
                        SpatialGraphs.getNearestVertex(graph, new Point(6, 2, 0))
                        )
                );

        PlannedPathImpl<Waypoint, SpatialManeuver> otherPath = new PlannedPathImpl<Waypoint, SpatialManeuver>(
                graph,
                edges
                );
        assertEquals(2, metric.getTrajectoryDistance(otherPath, path), 0.001);
    }

    @Test
    public void testTwoPaths() {
        List<SpatialManeuver> edges1 = Arrays.asList(
                graph.getEdge(
                        SpatialGraphs.getNearestVertex(graph, new Point(1, 1, 0)),
                        SpatialGraphs.getNearestVertex(graph, new Point(1, 2, 0))
                        ),
                graph.getEdge(
                        SpatialGraphs.getNearestVertex(graph, new Point(1, 2, 0)),
                        SpatialGraphs.getNearestVertex(graph, new Point(2, 2, 0))
                        ),
                graph.getEdge(
                        SpatialGraphs.getNearestVertex(graph, new Point(2, 2, 0)),
                        SpatialGraphs.getNearestVertex(graph, new Point(2, 3, 0))
                        ),
                graph.getEdge(
                        SpatialGraphs.getNearestVertex(graph, new Point(2, 3, 0)),
                        SpatialGraphs.getNearestVertex(graph, new Point(3, 3, 0))
                        )
                );
        List<SpatialManeuver> edges2 = Arrays.asList(
                graph.getEdge(
                        SpatialGraphs.getNearestVertex(graph, new Point(1, 1, 0)),
                        SpatialGraphs.getNearestVertex(graph, new Point(2, 1, 0))
                        ),
                graph.getEdge(
                        SpatialGraphs.getNearestVertex(graph, new Point(2, 1, 0)),
                        SpatialGraphs.getNearestVertex(graph, new Point(2, 2, 0))
                        ),
                graph.getEdge(
                        SpatialGraphs.getNearestVertex(graph, new Point(2, 2, 0)),
                        SpatialGraphs.getNearestVertex(graph, new Point(3, 2, 0))
                        ),
                graph.getEdge(
                        SpatialGraphs.getNearestVertex(graph, new Point(3, 2, 0)),
                        SpatialGraphs.getNearestVertex(graph, new Point(3, 3, 0))
                        )
                );

        PlannedPathImpl<Waypoint, SpatialManeuver> path1 = new PlannedPathImpl<Waypoint, SpatialManeuver>(
                graph,
                edges1
                );

        PlannedPathImpl<Waypoint, SpatialManeuver> path2 = new PlannedPathImpl<Waypoint, SpatialManeuver>(
                graph,
                edges2
                );

        assertEquals(2, metric.getTrajectoryDistance(path1, path2), 0.001);
    }
}
