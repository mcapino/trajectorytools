package cz.agents.alite.trajectorytools.graph.spatial.maneuvers;

import org.jgrapht.graph.DefaultWeightedEdge;

import cz.agents.alite.trajectorytools.graph.spatial.SpatialWaypoint;
import cz.agents.alite.trajectorytools.trajectory.Trajectory;
import cz.agents.alite.trajectorytools.util.OrientedPoint;
import cz.agents.alite.trajectorytools.util.Point;
import cz.agents.alite.trajectorytools.util.Vector;

public class Straight implements SpatialManeuver {
	
	private SpatialWaypoint start;
	private SpatialWaypoint end;
	private double speed;
	
	public Straight(SpatialWaypoint start, SpatialWaypoint end, double speed) {
		super();
		this.start = start;
		this.end = end;	
		this.speed = speed;
	}

	@Override
	public Trajectory getTrajectory(final double startTime) {
		return new Trajectory() {
			
			@Override
			public OrientedPoint getPosition(double t) {
				if (t < startTime || t > startTime + getDuration()) 
					throw new IllegalArgumentException("The position for time " + t + " which is undefined for this trajectory");
				
				double alpha = (t - startTime) / getDuration();
				assert(alpha >= 0.0 && alpha <= 1.0);
				
				Point pos = Point.interpolate(start, end, alpha);
				Vector dir;
				if (!end.equals(start)) {
					dir = Vector.subtract(end, start);
					dir.normalize();
				} else {
					dir = new Vector(0,1,0);
				}
				
				return new OrientedPoint(pos, dir);
			}
			
			@Override
			public double getMinTime() {
				return startTime;
			}
			
			@Override
			public double getMaxTime() {
				return startTime + getDuration();
			}
		};
	}

	@Override
	public double getDistance() {
		return start.distance(end);
	}

	@Override
	public double getDuration() {
		return getDistance()/speed;
	}

}
