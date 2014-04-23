package tt.euclid2i.region;

import static org.junit.Assert.*;

import org.junit.Test;

import tt.euclid2i.Point;

public class CircleTest {

	@Test
	public void intersectsLine() {

		Circle circle = new Circle(new Point(10,10), 10);

		assertTrue(circle.intersectsLine(new Point(10,-5), new Point(10,25)));
		assertTrue(circle.intersectsLine(new Point(7,7), new Point(0,0)));
		assertTrue(circle.intersectsLine(new Point(7,7), new Point(8,8)));
		assertFalse(circle.intersectsLine(new Point(0,22), new Point(20,24)));

	}

}
