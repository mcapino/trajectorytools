package tt.euclid2i.util;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.AStarShortestPathSimple;
import org.jgrapht.util.HeuristicToGoal;
import org.junit.Assert;
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
import tt.euclidtime3i.discretization.Straight;
import tt.euclidtime3i.trajectory.LinearTrajectory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertTrue;

public class SeparationDetectorTest {

    private static final int TEST_SIZE = 500;
    private static final int SEED = 119;
    private static final float SPEED = 1.0f;
    private static final int SIZE = 500;
    private static final int VERTICES = 2500;
    private static final int NN_RADIUS = 25;
    private static final int SEP_RADIUS = 125;

    @Test
    public void testPartiallyOverlappingTrajectories() {
        Point whatever = new Point(0, 0);
        Straight a = new Straight(whatever, 0, whatever, 1);
        Straight b = new Straight(whatever, 2, whatever, 3);

        BasicSegmentedTrajectory tA = new BasicSegmentedTrajectory(Collections.singletonList(a), 2);
        BasicSegmentedTrajectory tB = new BasicSegmentedTrajectory(Collections.singletonList(b), 2);

        Assert.assertTrue(SeparationDetector.hasConflictAnalytic(tA, tB, 1));
    }

    @Test
    public void testNonoverlappingTrajectories() {
        Point whatever = new Point(0, 0);
        Straight a = new Straight(whatever, 0, whatever, 1);
        Straight b = new Straight(whatever, 2, whatever, 3);

        BasicSegmentedTrajectory tA = new BasicSegmentedTrajectory(Collections.singletonList(a), 1);
        BasicSegmentedTrajectory tB = new BasicSegmentedTrajectory(Collections.singletonList(b), 1);

        Assert.assertFalse(SeparationDetector.hasConflictAnalytic(tA, tB, 1));
    }

    @Test
    public void testAnalyticToNumericSolution() {
        Random testSeed = new Random(SEED);

        Graph<Point, Line> graph = getProbabilisticRoadmap(testSeed.nextInt());
        List<Point> vertices = new ArrayList<Point>(graph.vertexSet());

        int different = 0;
        for (int i = 0; i < TEST_SIZE; i++) {
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

            boolean colideNumeric = SeparationDetector.hasAnyPairwiseConflict(trajectoryA, new Trajectory[]{trajectoryB}, new int[]{SEP_RADIUS}, 1);
            boolean colideAnalytic = SeparationDetector.hasConflictAnalytic(trajectoryA, trajectoryB, SEP_RADIUS);

            if (colideNumeric != colideAnalytic) {
                different++;
            }
        }

        // up to 2% error is allowed due to the numeric instability of numeric method
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

    // Separation detector should regard "touching" trajectories as non-conflicting. I.e if distance = separation, we are okay
    @Test
    public void testTouch() throws Exception {
        SegmentedTrajectory traj1 = new LinearTrajectory(new tt.euclidtime3i.Point(0, 0, 0), new tt.euclidtime3i.Point(0, 100, 100), 100);
        SegmentedTrajectory traj2 = new LinearTrajectory(new tt.euclidtime3i.Point(50, 0, 0), new tt.euclidtime3i.Point(50, 100, 100), 100);

        // distance between trajs is 50, separation distance is 51 => this is conflict
        assertTrue(SeparationDetector.hasAnyPairwiseConflict(traj1, new Trajectory[]{traj2}, new int[]{51}, 10));
        assertTrue(SeparationDetector.hasConflictAnalytic(traj1, traj2, 51));
        // distance between trajs is 50, separation distance is 50 => this is not a conflict
        assertTrue(!SeparationDetector.hasAnyPairwiseConflict(traj1, new Trajectory[]{traj2}, new int[]{50}, 10));
        assertTrue(!SeparationDetector.hasConflictAnalytic(traj1, traj2, 50));
    }

}
