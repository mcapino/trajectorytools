package tt.euclidtime3i.util;

import static org.junit.Assert.*;

import org.jgrapht.GraphPath;
import org.jgrapht.SingleEdgeGraphPath;
import org.junit.Test;

import tt.euclid2i.Point;
import tt.euclid2i.Trajectory;
import tt.euclid2i.trajectory.LinearTrajectory;
import tt.euclidtime3i.Region;
import tt.euclidtime3i.discretization.Straight;
import tt.euclidtime3i.region.MovingCircle;

public class IntersectionCheckerTest {

	@Test
	public void test() {

		Region region1 = new MovingCircle(
			new LinearTrajectory(0, new Point(0,-1000), new Point(0,1000), 1, 1000, 1000),
			100);

		Region region2 = new MovingCircle(
				new LinearTrajectory(0, new Point(-1000,0), new Point(1000,0), 1, 1000, 1000),
				100);

		Region region3 = new MovingCircle(
				new LinearTrajectory(0, new Point(201,-1000), new Point(201,1000), 1, 1000, 1000),
				100);

		Region region4 = new MovingCircle(
				new LinearTrajectory(0, new Point(199,-1000), new Point(199,1000), 1, 1000, 1000),
				100);

		// cross conflict case
		assertTrue(IntersectionChecker.intersect(region1, region2));

		// two parallel regions:
		// this one is slighlty above the threshold, 200=100+100
		assertFalse(IntersectionChecker.intersect(region1, region3));
		// this one is slightly below
		assertTrue(IntersectionChecker.intersect(region1, region4));
	}

}
