package tt.euclid2d.trajectory;

import javax.vecmath.Point2d;

import tt.euclid2d.Point;
import tt.euclid2d.trajectory.TrajectoryObjectiveFunctionAtPoint;
import tt.euclid2i.Trajectory;

public class DesiredTrajectoryDifferenceObjective implements	TrajectoryObjectiveFunctionAtPoint {

	Trajectory traj;
	double w;

	public DesiredTrajectoryDifferenceObjective(Trajectory traj, double w) {
		super();
		this.traj = traj;
		this.w = w;
	}

	@Override
	public double getCost(Point point, int time) {
		tt.euclid2i.Point trajPoint = traj.get(time);
		return w * point.distance(new Point2d(trajPoint.x, trajPoint.y));
	}

}
