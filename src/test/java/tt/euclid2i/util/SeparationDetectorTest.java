package tt.euclid2i.util;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.AStarShortestPathSimple;
import org.jgrapht.util.HeuristicToGoal;
import org.junit.Test;

import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.SegmentedTrajectory;
import tt.euclid2i.Trajectory;
import tt.euclid2i.discretization.ProbabilisticRoadmap;
import tt.euclid2i.region.Rectangle;
import tt.euclid2i.trajectory.BasicSegmentedTrajectory;
import tt.euclid2i.trajectory.SegmentedTrajectoryFactory;

public class SeparationDetectorTest {

    private static final int TEST_SIZE = 200;
    private static final int SEED = 119;
    private static final int SPEED = 1;
    private static final int SIZE = 500;
    private static final int VERTICES = 2500;
    private static final int NN_RADIUS = 25;
    private static final int SEP_RADIUS = 125;

    @Test
    public void testHasAnyPairwiseConflict() throws Exception {
        Random testSeed = new Random(SEED);

        Graph<Point, Line> graph = getProbabilisticRoadmap(testSeed.nextInt());
        List<Point> vertices = new ArrayList<Point>(graph.vertexSet());

        int different = 0;

        for (int i = 0; i < 100; i++) {
            final Point startA = vertices.get(testSeed.nextInt(1000));
            final Point endA = vertices.get(testSeed.nextInt(1000));

            final Point startB = vertices.get(testSeed.nextInt(1000));
            final Point endB = vertices.get(testSeed.nextInt(1000));

            GraphPath<Point, Line> pathA = findPath(graph, startA, endA);
            GraphPath<Point, Line> pathB = findPath(graph, startB, endB);

            if (pathA == null || pathB == null || pathA.getWeight() == 0 || pathB.getWeight() == 0)
                continue;

            BasicSegmentedTrajectory trajectoryA = SegmentedTrajectoryFactory.createConstantSpeedTrajectory(pathA, 0, SPEED, 2 * SIZE, pathA.getWeight() / SPEED);
            BasicSegmentedTrajectory trajectoryB = SegmentedTrajectoryFactory.createConstantSpeedTrajectory(pathB, 0, SPEED, 2 * SIZE, pathA.getWeight() / SPEED);

            SegmentedTrajectory[] segmentedTrajectories = new SegmentedTrajectory[] {trajectoryB};
            Trajectory[] trajectories = new Trajectory[] {trajectoryB};

            boolean colideNumeric = SeparationDetector.hasAnyPairwiseConflict(trajectoryA, trajectories, new int[] {SEP_RADIUS}, 1);
            boolean colideAnalytic = SeparationDetector.hasAnyPairwiseConflictAnalytic(trajectoryA, segmentedTrajectories, new int[] {SEP_RADIUS});

            if (colideNumeric != colideAnalytic) {
                different++;
            }
        }

        // 2% error is allowed due to numeric nonstability
        assertTrue(different < TEST_SIZE / 50);
    }

    private ProbabilisticRoadmap getProbabilisticRoadmap(int seed) {
        return new ProbabilisticRoadmap(VERTICES, NN_RADIUS, new Point[]{}, new Rectangle(new Point(0, 0), new Point(SIZE, SIZE)), new ArrayList<Region>(), new Random(seed));
    }

    private GraphPath<Point, Line> findPath(Graph<Point, Line> graph, Point startA, final Point endA) {
        return AStarShortestPathSimple.findPathBetween(graph, new HeuristicToGoal<Point>() {
            @Override
            public double getCostToGoalEstimate(Point current) {
                return endA.distance(current);
            }
        }, startA, endA);
    }
}
