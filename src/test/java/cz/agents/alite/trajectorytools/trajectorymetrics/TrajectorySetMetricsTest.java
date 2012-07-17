package cz.agents.alite.trajectorytools.trajectorymetrics;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

public class TrajectorySetMetricsTest {

    private static final int WORLD_SIZE = 10;
    private ManeuverGraphInterface graph;
    private Collection<PlannedPath<SpatialWaypoint, Maneuver>> paths;
    private SpatialWaypoint startVertex;
    private SpatialWaypoint endVertex;
    private ManeuverTrajectoryMetric metric;

    @Before
    public void setup() {
        graph = FourWayConstantSpeedGridGraph.create(WORLD_SIZE, WORLD_SIZE, WORLD_SIZE, WORLD_SIZE, 1.0);
        startVertex = graph.getNearestWaypoint(new Point(2, 2, 0));
        endVertex = graph.getNearestWaypoint(new Point(5, 5, 0));

        paths = new ArrayList<PlannedPath<SpatialWaypoint,Maneuver>>();

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
        paths.add( new PlannedPathImpl<SpatialWaypoint, Maneuver>(graph, edges) );

        
        edges = Arrays.asList(
                graph.getEdge(
                        graph.getNearestWaypoint(new Point(3, 5, 0)),
                        graph.getNearestWaypoint(new Point(4, 5, 0))
                        ),
                graph.getEdge(
                        graph.getNearestWaypoint(new Point(4, 5, 0)),
                        graph.getNearestWaypoint(new Point(5, 5, 0))
                        )
                );
        paths.add(new PlannedPathImpl<SpatialWaypoint, Maneuver>(graph, edges));

        metric = new TrajectoryDistanceMetric();
    }

    @Test
    public void testVertexOn() {
        assertEquals(
                0.5, 
                TrajectorySetMetrics.getRelativePlanSetAvgDiversity(
                        new SingleVertexPlannedPath(
                                graph, 
                                graph.getNearestWaypoint(new Point(4, 5, 0))
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
                        new SingleVertexPlannedPath(
                                graph, 
                                graph.getNearestWaypoint(new Point(4, 5, 0))
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
                        new SingleVertexPlannedPath(
                                graph, 
                                graph.getNearestWaypoint(new Point(4, 5, 0))
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
                        new SingleVertexPlannedPath(
                                graph, 
                                graph.getNearestWaypoint(new Point(3, 3, 0))
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
                        new SingleVertexPlannedPath(
                                graph, 
                                graph.getNearestWaypoint(new Point(3, 3, 0))
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
                        new SingleVertexPlannedPath(
                                graph, 
                                graph.getNearestWaypoint(new Point(3, 3, 0))
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
                        new SingleVertexPlannedPath(
                                graph, 
                                graph.getNearestWaypoint(new Point(4, 4, 0))
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
                        new SingleVertexPlannedPath(
                                graph, 
                                graph.getNearestWaypoint(new Point(5, 7, 0))
                                ), 
                        paths,
                        metric
                        ), 
                0.001
                );
    } 
}
