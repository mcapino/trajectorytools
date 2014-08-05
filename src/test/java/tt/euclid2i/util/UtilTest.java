package tt.euclid2i.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

import tt.euclid2i.Point;
import tt.euclid2i.probleminstance.RandomEnvironment;
import tt.euclid2i.region.Polygon;

public class UtilTest {

	@Test
	public void sampleFreeSpace() throws InterruptedException {
		RandomEnvironment env = new RandomEnvironment(1000, 1000, 30, 50, 1);

		for (int i=0; i<1000;i++) {
			Point p = Util.sampleFreeSpace(env.getBoundary(), env.getObstacles(), new Random());
			// must be inside
			assertTrue(((Polygon) env.getBoundary()).flip().isInside(p));
			// must not be outside
			assertFalse(((Polygon) env.getBoundary()).isInside(p));
		}

	}

}
