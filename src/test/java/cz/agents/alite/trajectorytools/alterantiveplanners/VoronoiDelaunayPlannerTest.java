package cz.agents.alite.trajectorytools.alterantiveplanners;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Before;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import cz.agents.alite.trajectorytools.graph.ObstacleGraphView;
import cz.agents.alite.trajectorytools.graph.spatial.GraphWithObstacles;
import cz.agents.alite.trajectorytools.graph.spatial.SpatialGraphs;
import cz.agents.alite.trajectorytools.graph.spatial.SpatialGridFactory;
import cz.agents.alite.trajectorytools.planner.AStarPlanner;
import cz.agents.alite.trajectorytools.planner.HeuristicFunction;
import cz.agents.alite.trajectorytools.planner.PlannedPath;
import cz.agents.alite.trajectorytools.util.SpatialPoint;

@RunWith(Theories.class)
public class VoronoiDelaunayPlannerTest {

    private static final AStarPlanner<SpatialPoint, DefaultWeightedEdge> planner = new AStarPlanner<SpatialPoint, DefaultWeightedEdge>();
    {
        planner.setHeuristicFunction(new HeuristicFunction<SpatialPoint>() {
        @Override
            public double getHeuristicEstimate(SpatialPoint current, SpatialPoint goal) {
                return current.distance(goal) + ( current.x > current.y ? 0.1 : -0.1 );
            }
        });
    }

	private AlternativePathPlanner<SpatialPoint, DefaultWeightedEdge> altPlanner;

    private static final int WORLD_SIZE = 9;

	private GraphWithObstacles<SpatialPoint, DefaultWeightedEdge> graph;

    @DataPoints
    public static TestData[] testData = new TestData[] {
    	new TestData(
    			new ArrayList<SpatialPoint>(), 
    			1
    			),
		new TestData(
				Arrays.asList(
						new SpatialPoint[] {
								new SpatialPoint(3, 3, 0)
						}), 
				2),
		new TestData(
				Arrays.asList(
						new SpatialPoint[] {
								new SpatialPoint(2, 3, 0),
								new SpatialPoint(4, 2, 0),
						}), 
				4),
		new TestData(
				Arrays.asList(
						new SpatialPoint[] {
								new SpatialPoint(2, 3, 0),
								new SpatialPoint(4, 2, 0),
								new SpatialPoint(5, 5, 0),
						}), 
				7),
    };

    @Theory
	public void testObstaclesDiagonal(TestData data) {

    	for (SpatialPoint obstacle : data.obstacles) {
			graph.addObstacle(obstacle);
		}
    	
        Collection<PlannedPath<SpatialPoint, DefaultWeightedEdge>> paths = altPlanner.planPath(
                graph,
                SpatialGraphs.getNearestVertex(graph, new SpatialPoint(0, 0, 0)),
                SpatialGraphs.getNearestVertex(graph, new SpatialPoint(WORLD_SIZE, WORLD_SIZE, 0))
                );
        
        System.out.println("paths: " + paths);

        assertEquals(data.expectedNumberOfPaths, paths.size());
	}

    @Theory
	public void testObstaclesHorizontal(TestData data) {

    	for (SpatialPoint obstacle : data.obstacles) {
			graph.addObstacle(obstacle);
		}
    	
        Collection<PlannedPath<SpatialPoint, DefaultWeightedEdge>> paths = altPlanner.planPath(
                graph,
                SpatialGraphs.getNearestVertex(graph, new SpatialPoint(0, WORLD_SIZE/2, 0)),
                SpatialGraphs.getNearestVertex(graph, new SpatialPoint(WORLD_SIZE, WORLD_SIZE/2, 0))
                );

        assertEquals(data.expectedNumberOfPaths, paths.size());
	}

    @Theory
	public void testObstaclesVertical(TestData data) {

    	for (SpatialPoint obstacle : data.obstacles) {
			graph.addObstacle(obstacle);
		}
    	
        Collection<PlannedPath<SpatialPoint, DefaultWeightedEdge>> paths = altPlanner.planPath(
                graph,
                SpatialGraphs.getNearestVertex(graph, new SpatialPoint(WORLD_SIZE/2, 0, 0)),
                SpatialGraphs.getNearestVertex(graph, new SpatialPoint(WORLD_SIZE/2, WORLD_SIZE, 0))
                );

        assertEquals(data.expectedNumberOfPaths, paths.size());
	}

	@Before
	public void setup() {
        graph = ObstacleGraphView.createFromGraph(SpatialGridFactory.create4WayGrid(WORLD_SIZE, WORLD_SIZE, WORLD_SIZE, WORLD_SIZE, 1.0), null);
        altPlanner = new VoronoiDelaunayPlanner<SpatialPoint, DefaultWeightedEdge>( planner );
	}

	private static class TestData {
		final Collection<SpatialPoint> obstacles;
		final int expectedNumberOfPaths;

		public TestData(Collection<SpatialPoint> obstacles, int expectedNumberOfPaths) {
			this.obstacles = obstacles;
			this.expectedNumberOfPaths = expectedNumberOfPaths;
		}
	}
}
