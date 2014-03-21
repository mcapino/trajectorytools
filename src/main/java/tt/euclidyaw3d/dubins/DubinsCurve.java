package tt.euclidyaw3d.dubins;

import tt.euclid2i.EvaluatedTrajectory;
import tt.euclid2i.trajectory.PointArrayTrajectory;
import tt.euclidyaw3d.Point;
import tt.util.NotImplementedException;

/**
 * This class implements logic for computing Dubins curves.
 * Most of the code is just Java reimplementation of ompl::base::DubinsStateSpace class from Open-Motion Planning Library OMPL
 */
public class DubinsCurve {

	final static double TWOPI = 2*Math.PI;
    final static double DUBINS_EPS = 1e-6;
    final static double DUBINS_ZERO = -1e-9;

    static class DubinsPath
    {
    	public static enum Segment {LEFT, STRAIGHT, RIGHT}
        /** Path segment types */
    	Segment[] type;
        /** Path segment lengths */
        double[] length = new double[3];
        /** Whether the path should be followed "in reverse" */
        boolean reverse = false;

        DubinsPath() {
        	this(new DubinsPath.Segment[] {DubinsPath.Segment.LEFT, DubinsPath.Segment.STRAIGHT, DubinsPath.Segment.LEFT }, 0, Double.MAX_VALUE,0);
        }

        DubinsPath(Segment[] type, double t, double p, double q)
        {
        	assert type.length == 3;
            this.type = type;
        	length[0] = t;
            length[1] = p;
            length[2] = q;
            assert(t >= 0.);
            assert(p >= 0.);
            assert(q >= 0.);
        }

        double length() {
            return length[0] + length[1] + length[2];
        }
    };

    static double mod2pi(double angleInRads)
    {
        if (angleInRads < 0 && angleInRads > DUBINS_ZERO) {
        	// Angle is close to zero
        	return 0;
        }
        return angleInRads - (TWOPI * Math.floor(angleInRads / TWOPI));
    }

	private Point start;
	private double rho;
	private DubinsPath path;

	public DubinsCurve(Point start, Point end, double rho) {
		this(start, end, rho, false);
	}

    public DubinsCurve(Point start, Point end, double rho, boolean reverseAllowed) {
        this.start = start;
        this.rho = rho;

    	this.path = getDubinsPath(start, end, rho);

        if (reverseAllowed) {
        	  DubinsPath reversedPath = getDubinsPath(end, start, rho);
              if (reversedPath.length() < path.length())
              {
                  reversedPath.reverse = true;
                  this.path = reversedPath;
              }
        }
    }

	protected DubinsPath getDubinsPath(Point start, Point end, double rho) {
		// Normalize to relative coordinate system, where the origin is at start (0,0)
    	double dx = end.x - start.x;
        double dy = end.y - start.y;
        double d = Math.sqrt(dx*dx + dy*dy) / rho; // normalize to r_min = 1
        double th = Math.atan2(dy, dx);
        double alpha = mod2pi(start.getYaw() - th);
        double beta = mod2pi(end.getYaw()- th);

        return canonicalDubins(d, alpha, beta);
	}

    public EvaluatedTrajectory getTrajectory(double speed, int samplingInterval) {

    	double duration = (path.length() * rho) / speed;
    	int nPoints = (int) Math.floor(duration / samplingInterval);

    	tt.euclid2d.Point points[] = new tt.euclid2d.Point[nPoints];

    	for (int i=0; i<nPoints; i++) {
    		points[i] = interpolate((i*samplingInterval) / duration).getPosition();
    	}

    	PointArrayTrajectory traj = new PointArrayTrajectory(points, samplingInterval, duration);

		return traj;
    }

    // Canonical Dubins. Start is assumed to be at (0,0) with orientation 0 rad. Rmin is assimed to be 1. The lengths are in rads.
	static private DubinsPath canonicalDubins(double d, double alpha, double beta)
    {
        if (d < DUBINS_EPS && Math.abs(alpha-beta) < DUBINS_EPS) {
            return new DubinsPath(new DubinsPath.Segment[] {DubinsPath.Segment.LEFT, DubinsPath.Segment.STRAIGHT, DubinsPath.Segment.LEFT }, 0, d, 0);
        }

        DubinsPath bestPath = dubinsLSL(d, alpha, beta);
        double minLength = bestPath.length();

        DubinsPath tmp = dubinsRSR(d, alpha, beta);
        double len;

        if ((len = tmp.length()) < minLength)
        {
            minLength = len;
            bestPath = tmp;
        }

        tmp = dubinsRSL(d, alpha, beta);
        if ((len = tmp.length()) < minLength)
        {
            minLength = len;
            bestPath = tmp;
        }
        tmp = dubinsLSR(d, alpha, beta);
        if ((len = tmp.length()) < minLength)
        {
            minLength = len;
            bestPath = tmp;
        }
        tmp = dubinsRLR(d, alpha, beta);
        if ((len = tmp.length()) < minLength)
        {
            minLength = len;
            bestPath = tmp;
        }
        tmp = dubinsLRL(d, alpha, beta);
        if ((len = tmp.length()) < minLength)
            bestPath = tmp;

        return bestPath;
    }

    static DubinsPath dubinsLSL(double d, double alpha, double beta)
    {
        double ca = Math.cos(alpha), sa = Math.sin(alpha), cb = Math.cos(beta), sb = Math.sin(beta);
        double tmp = 2. + d*d - 2.*(ca*cb +sa*sb - d*(sa - sb));
        if (tmp >= DUBINS_ZERO)
        {
            double theta = Math.atan2(cb - ca, d + sa - sb);
            double t = mod2pi(-alpha + theta);
            double p = Math.sqrt(Math.max(tmp, 0.));
            double q = mod2pi(beta - theta);
            assert(Math.abs(p*Math.cos(alpha + t) - sa + sb - d) < DUBINS_EPS);
            assert(Math.abs(p*Math.sin(alpha + t) + ca - cb) < DUBINS_EPS);
            assert(mod2pi(alpha + t + q - beta + .5 * DUBINS_EPS) < DUBINS_EPS);
            return new DubinsPath(new DubinsPath.Segment[] {DubinsPath.Segment.LEFT, DubinsPath.Segment.STRAIGHT, DubinsPath.Segment.LEFT }, t, p, q);
        }
        return new DubinsPath();
    }


    static DubinsPath  dubinsRSR(double d, double alpha, double beta)
    {
        double ca = Math.cos(alpha), sa = Math.sin(alpha), cb = Math.cos(beta), sb = Math.sin(beta);
        double tmp = 2. + d*d - 2.*(ca*cb + sa*sb - d*(sb - sa));
        if (tmp >= DUBINS_ZERO)
        {
            double theta = Math.atan2(ca - cb, d - sa + sb);
            double t = mod2pi(alpha - theta);
            double p = Math.sqrt(Math.max(tmp, 0.));
            double q = mod2pi(-beta + theta);
            assert(Math.abs(p*Math.cos(alpha - t) + sa - sb - d) < DUBINS_EPS);
            assert(Math.abs(p*Math.sin(alpha - t) - ca + cb) < DUBINS_EPS);
            assert(mod2pi(alpha - t - q - beta + .5 * DUBINS_EPS) < DUBINS_EPS);
            return new DubinsPath(new DubinsPath.Segment[] {DubinsPath.Segment.RIGHT, DubinsPath.Segment.STRAIGHT, DubinsPath.Segment.RIGHT }, t, p, q);
        }
        return new DubinsPath();
    }

    static DubinsPath dubinsRSL(double d, double alpha, double beta)
    {
        double ca = Math.cos(alpha), sa = Math.sin(alpha), cb = Math.cos(beta), sb = Math.sin(beta);
        double tmp = d * d - 2. + 2. * (ca*cb + sa*sb - d * (sa + sb));
        if (tmp >= DUBINS_ZERO)
        {
            double p = Math.sqrt(Math.max(tmp, 0.));
            double theta = Math.atan2(ca + cb, d - sa - sb) - Math.atan2(2., p);
            double t = mod2pi(alpha - theta);
            double q = mod2pi(beta - theta);
            assert(Math.abs(p*Math.cos(alpha - t) - 2. * Math.sin(alpha - t) + sa + sb - d) < DUBINS_EPS);
            assert(Math.abs(p*Math.sin(alpha - t) + 2. * Math.cos(alpha - t) - ca - cb) < DUBINS_EPS);
            assert(mod2pi(alpha - t + q - beta + .5 * DUBINS_EPS) < DUBINS_EPS);
            return new DubinsPath(new DubinsPath.Segment[]{DubinsPath.Segment.RIGHT, DubinsPath.Segment.STRAIGHT, DubinsPath.Segment.LEFT }, t, p, q);
        }
        return new DubinsPath();
    }

    static DubinsPath dubinsLSR(double d, double alpha, double beta)
    {
        double ca = Math.cos(alpha), sa = Math.sin(alpha), cb = Math.cos(beta), sb = Math.sin(beta);
        double tmp = -2. + d * d + 2. * (ca*cb + sa*sb + d * (sa + sb));
        if (tmp >= DUBINS_ZERO)
        {
            double p = Math.sqrt(Math.max(tmp, 0.));
            double theta = Math.atan2(-ca - cb, d + sa + sb) - Math.atan2(-2., p);
            double t = mod2pi(-alpha + theta);
            double q = mod2pi(-beta + theta);
            assert(Math.abs(p*Math.cos(alpha + t) + 2. * Math.sin(alpha + t) - sa - sb - d) < DUBINS_EPS);
            assert(Math.abs(p*Math.sin(alpha + t) - 2. * Math.cos(alpha + t) + ca + cb) < DUBINS_EPS);
            assert(mod2pi(alpha + t - q - beta + .5 * DUBINS_EPS) < DUBINS_EPS);
            return new DubinsPath(new DubinsPath.Segment[]{DubinsPath.Segment.LEFT, DubinsPath.Segment.STRAIGHT, DubinsPath.Segment.RIGHT }, t, p, q);
        }
        return new DubinsPath();
    }

    static DubinsPath dubinsRLR(double d, double alpha, double beta)
    {
        double ca = Math.cos(alpha), sa = Math.sin(alpha), cb = Math.cos(beta), sb = Math.sin(beta);
        double tmp = .125 * (6. - d * d  + 2. * (ca*cb + sa*sb + d * (sa - sb)));
        if (Math.abs(tmp) < 1.)
        {
            double p = TWOPI - Math.acos(tmp);
            double theta = Math.atan2(ca - cb, d - sa + sb);
            double t = mod2pi(alpha - theta + .5 * p);
            double q = mod2pi(alpha - beta - t + p);
            assert(Math.abs( 2.*Math.sin(alpha - t + p) - 2. * Math.sin(alpha - t) - d + sa - sb) < DUBINS_EPS);
            assert(Math.abs(-2.*Math.cos(alpha - t + p) + 2. * Math.cos(alpha - t) - ca + cb) < DUBINS_EPS);
            assert(mod2pi(alpha - t + p - q - beta + .5 * DUBINS_EPS) < DUBINS_EPS);
            return new DubinsPath(new DubinsPath.Segment[]{DubinsPath.Segment.RIGHT, DubinsPath.Segment.LEFT, DubinsPath.Segment.RIGHT }, t, p, q);
        }
        return new DubinsPath();
    }

    static DubinsPath dubinsLRL(double d, double alpha, double beta)
    {
        double ca = Math.cos(alpha), sa = Math.sin(alpha), cb = Math.cos(beta), sb = Math.sin(beta);
        double tmp = .125 * (6. - d * d  + 2. * (ca*cb + sa*sb - d * (sa - sb)));
        if (Math.abs(tmp) < 1.)
        {
            double p = TWOPI - Math.acos(tmp);
            double theta = Math.atan2(-ca + cb, d + sa - sb);
            double t = mod2pi(-alpha + theta + .5 * p);
            double q = mod2pi(beta - alpha - t + p);
            assert(Math.abs(-2.*Math.sin(alpha + t - p) + 2. * Math.sin(alpha + t) - d - sa + sb) < DUBINS_EPS);
            assert(Math.abs( 2.*Math.cos(alpha + t - p) - 2. * Math.cos(alpha + t) + ca - cb) < DUBINS_EPS);
            assert(mod2pi(alpha + t - p + q - beta + .5 * DUBINS_EPS) < DUBINS_EPS);
            return new DubinsPath(new DubinsPath.Segment[]{DubinsPath.Segment.LEFT, DubinsPath.Segment.RIGHT, DubinsPath.Segment.LEFT }, t, p, q);
        }
        return new DubinsPath();
    }

    public Point[] interpolateBy(double samplingInterval) {
    	int nPoint = (int) Math.ceil(getLength() / samplingInterval);
    	Point[] res = new Point[nPoint];

    	for (int i = 0; i < res.length; i++) {
			res[i] = interpolate(i/(double)res.length);
		}

    	return res;
    }

    /** Interpolates position on the path for t from [0,1] **/
    public Point interpolate(double t)
    {
        double seg = t * path.length();
        double phi = start.getYaw();
        double v;

        Point s = new Point(0, 0, start.getYaw());

        if (!path.reverse)
        {
            for (int i=0; i<3 && seg>0; i++)
            {
                v = Math.min(seg, path.length[i]);
                phi = s.getYaw();
                seg -= v;

                switch (path.type[i]) {
                	case LEFT:
                		s.x = s.x + Math.sin(phi+v) - Math.sin(phi);
                		s.y = s.y - Math.cos(phi+v) + Math.cos(phi);
                		s.z = phi + v;
                		break;

                	 case RIGHT:
                		s.x = s.x - Math.sin(phi-v) + Math.sin(phi);
                		s.y = s.y + Math.cos(phi-v) - Math.cos(phi);
                		s.z = phi - v;
                 		break;

                     case STRAIGHT:
                    	s.x = s.x + v * Math.cos(phi);
                    	s.y = s.y + v * Math.sin(phi);
                        break;
                }
            }
        } else {
            for (int i=0; i<3 && seg>0; i++)
            {
                v = Math.min(seg, path.length[2-i]);
                phi = s.getYaw();
                seg -= v;

                switch(path.type[2-i]) {
                    case LEFT:
                    	s.x = s.x + Math.sin(phi-v) - Math.sin(phi);
                    	s.y = s.y - Math.cos(phi-v) + Math.cos(phi);
                    	s.z = phi - v;
                        break;
                    case RIGHT:
                    	s.x = s.x - Math.sin(phi+v) + Math.sin(phi);
                    	s.y = s.y + Math.cos(phi+v) - Math.cos(phi);
                    	s.z = phi + v;
                        break;
                    case STRAIGHT:
                    	s.x = s.x - v * Math.cos(phi);
                    	s.y = s.y - v * Math.sin(phi);
                        break;
                }
            }
        }

        Point res = new Point(s.x * rho + start.x, s.y * rho + start.y, s.getYaw());
        return res;
    }

    public double getLength() {
    	return path.length() * rho;
    }

    public boolean isReverse() {
    	return path.reverse;
    }


}
