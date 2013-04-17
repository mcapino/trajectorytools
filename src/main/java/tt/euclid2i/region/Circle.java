package tt.euclid2i.region;

import java.io.NotActiveException;

import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.util.NotImplementedException;

public class Circle implements Region {

	Point center;
	int radius;

	public Circle(Point center, int radius) {
		super();
		this.center = center;
		this.radius = radius;
	}

	@Override
	public boolean intersectsLine(Point p1, Point p2) {
		throw new NotImplementedException();
	}

	@Override
	public boolean isInside(Point p) {
		return center.distance(p) <= radius;
	}

	@Override
	public Rectangle getBoundingBox() {
		return new Rectangle(new Point(center.x - radius, center.y - radius),
							 new Point(center.x + radius, center.y + radius));
	}



}
