package cz.agents.alite.trajectorytools.util;

import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;

public class Point extends Point3d implements cz.agents.alite.vis.element.Point {

    private static final long serialVersionUID = -4209007401452559887L;

    public Point(double x, double y, double z) {
        super(x, y, z);
    }

    public Point(double[] p) {
        super(p);
    }

    public Point(Point3d p1) {
        super(p1);
    }

    public Point(Point3f p1) {
        super(p1);
    }

    public Point(Tuple3f t1) {
        super(t1);
    }

    public Point(Tuple3d t1) {
        super(t1);
    }

    public Point() {
        super();
    }

    @Override
    public javax.vecmath.Point3d getPosition() {
        return this;
    }
    
    public static Point interpolate(Point p1, Point p2, double alpha) {
    	Point result = new Point();
    	((Tuple3d)result).interpolate(p1, p2, alpha);
    	return result;
    }
    
    

}
