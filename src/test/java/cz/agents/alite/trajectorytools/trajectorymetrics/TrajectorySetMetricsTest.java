package cz.agents.alite.trajectorytools.trajectorymetrics;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import cz.agents.alite.trajectorytools.util.SpatialPoint;
import cz.agents.alite.trajectorytools.util.Waypoint;

public class TrajectorySetMetricsTest {

    private static final int WORLD_SIZE = 10;
    private Graph<Waypoint, SpatialManeuver> graph;
    private Collection<PlannedPath<Waypoint, SpatialManeuver>> paths;
    private Waypoint startVertex;
    private Waypoint endVertex;
    private TrajectoryMetric<Waypoint, SpatialManeuver> metric;

    @Before
    public void setup() {
        graph = SpatialGridFactory.create4WayGrid(WORLD_SIZE, WORLD_SIZE, WORLD_SIZE, WORLD_SIZE, 1.0);
        startVertex = SpatialGraphs.getNearestVertex(graph, new SpatialPoint(2, 2, 0));
        endVertex = SpatialGraphs.getNearestVertex(graph, new SpatialPoint(5, 5, 0));

        paths = new ArrayList<PlannedPath<Waypoint, SpatialManeuver>>();

        List<SpatialManeuver> edges = Arrays.asList(
                graph.getEdge(
                        startVertex,
                        SpatialGraphs.getNearestVertex(graph, new SpatialPoint(3, 2, 0))
                        ),
                graph.getEdge(
                        SpatialGraphs.getNearestVertex(graph, new SpatialPoint(3, 2, 0)),
                        SpatialGraphs.getNearestVertex(graph, new SpatialPoint(4, 2, 0))
                        ),
                graph.getEdge(
                        SpatialGraphs.getNearestVertex(graph, new SpatialPoint(4, 2, 0)),
                        SpatialGraphs.getNearestVertex(graph, new SpatialPoint(5, 2, 0))
                        ),
                graph.getEdge(
                        SpatialGraphs.getNearestVertex(graph, new SpatialPoint(5, 2, 0)),
                        SpatialGraphs.getNearestVertex(graph, new SpatialPoint(5, 3, 0))
                        ),
                graph.getEdge(
                        SpatialGraphs.getNearestVertex(graph, new SpatialPoint(5, 3, 0)),
                        SpatialGraphs.getNearestVertex(graph, new SpatialPoint(5, 4, 0))
                        ),
                graph.getEdge(
                        SpatialGraphs.getNearestVertex(graph, new SpatialPoint(5, 4, 0)),
                        endVertex
                        )
                );
        paths.add( new PlannedPathImpl<Waypoint, SpatialManeuver>(graph, edges) );


        edges = Arrays.asList(
                graph.getEdge(
                        SpatialGraphs.getNearestVertex(graph, new SpatialPoint(3, 5, 0)),
                        SpatialGraphs.getNearestVertex(graph, new SpatialPoint(4, 5, 0))
                        ),
                graph.getEdge(
                        SpatialGraphs.getNearestVertex(graph, new SpatialPoint(4, 5, 0)),
                        SpatialGraphs.getNearestVertex(graph, new SpatialPoint(5, 5, 0))
                        )
                );
        paths.add(new PlannedPathImpl<Waypoint, SpatialManeuver>(graph, edges));

        metric = new TrajectoryDistanceMetric<Waypoint, SpatialManeuver>();
    }

    @Test
    public void testVertexOn() {
        assertEquals(
                0.5,
                TrajectorySetMetrics.getRelativePlanSetAvgDiversity(
                        new SingleVertexPlannedPath<Waypoint, SpatialManeuver>(
                                graph,
                                SpatialGraphs.getNearestVertex(graph, new SpatialPoint(4, 5, 0))
                                ),
                        paths,
                        metric
                        ),
                0.001
                );
    }

    @Test
    public void testVertexOnMin() {
        assertEquals(
                0,
                TrajectorySetMetrics.getRelativePlanSetMinDiversity(
                        new SingleVertexPlannedPath<Waypoint, SpatialManeuver>(
                                graph,
                                SpatialGraphs.getNearestVertex(graph, new SpatialPoint(4, 5, 0))
                                ),
                        paths,
                        metric
                        ),
                0.001
                );
    }

    @Test
    public void testVertexOnMax() {
        assertEquals(
                1,
                TrajectorySetMetrics.getRelativePlanSetMaxDiversity(
                        new SingleVertexPlannedPath<Waypoint, SpatialManeuver>(
                                graph,
                                SpatialGraphs.getNearestVertex(graph, new SpatialPoint(4, 5, 0))
                                ),
                        paths,
                        metric
                        ),
                0.001
                );
    }

    @Test
    public void testVertexClose() {
        assertEquals(
                1.5,
                TrajectorySetMetrics.getRelativePlanSetAvgDiversity(
                        new SingleVertexPlannedPath<Waypoint, SpatialManeuver>(
                                graph,
                                SpatialGraphs.getNearestVertex(graph, new SpatialPoint(3, 3, 0))
                                ),
                        paths,
                        metric
                        ),
                0.001
                );
    }

    @Test
    public void testVertexCloseMin() {
        assertEquals(
                1,
                TrajectorySetMetrics.getRelativePlanSetMinDiversity(
                        new SingleVertexPlannedPath<Waypoint, SpatialManeuver>(
                                graph,
                                SpatialGraphs.getNearestVertex(graph, new SpatialPoint(3, 3, 0))
                                ),
                        paths,
                        metric
                        ),
                0.001
                );
    }

    @Test
    public void testVertexCloseMax() {
        assertEquals(
                2,
                TrajectorySetMetrics.getRelativePlanSetMaxDiversity(
                        new SingleVertexPlannedPath<Waypoint, SpatialManeuver>(
                                graph,
                                SpatialGraphs.getNearestVertex(graph, new SpatialPoint(3, 3, 0))
                                ),
                        paths,
                        metric
                        ),
                0.001
                );
    }

    @Test
    public void testVertexClose2() {
        assertEquals(
                1,
                TrajectorySetMetrics.getRelativePlanSetAvgDiversity(
                        new SingleVertexPlannedPath<Waypoint, SpatialManeuver>(
                                graph,
                                SpatialGraphs.getNearestVertex(graph, new SpatialPoint(4, 4, 0))
                                ),
                        paths,
                        metric
                        ),
                0.001
                );
    }

    @Test
    public void testVertexFar() {
        assertEquals(
                2,
                TrajectorySetMetrics.getRelativePlanSetAvgDiversity(
                        new SingleVertexPlannedPath<Waypoint, SpatialManeuver>(
                                graph,
                                SpatialGraphs.getNearestVertex(graph, new SpatialPoint(5, 7, 0))
                                ),
                        paths,
                        metric
                        ),
                0.001
                );
    }
}
