package tt.euclidyaw3d;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;

public class Point extends Point3d {

	public Point(tt.euclid2d.Point position, double yaw) {
		super(position.x, position.y, yaw);
	}

	public Point(double x, double y, double z) {
		super(x, y, z);
	}

	public Point(Tuple3d t1) {
		super(t1);
	}

	public tt.euclid2d.Point getPosition() {
		return new tt.euclid2d.Point(x,y);
	}

	/* Yaw in radians: (-pi/2 to +pi/2) */
	public double getYaw() {
		return z;
	}

}
