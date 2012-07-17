package cz.agents.alite.trajectorytools.graph.spatial.maneuvers;

import cz.agents.alite.trajectorytools.trajectory.Trajectory;
import cz.agents.alite.trajectorytools.util.OrientedPoint;
import cz.agents.alite.trajectorytools.util.Point;
import cz.agents.alite.trajectorytools.util.Vector;
import cz.agents.alite.trajectorytools.util.Waypoint;

public class Wait implements SpatialManeuver {
	
	private Waypoint waypoint;
	private double duration;
	
	public Wait(Waypoint waypoint, double duration) {
		super();
		this.waypoint = waypoint;
		this.duration = duration;
	}

	@Override
	public Trajectory getTrajectory(final double startTime) {
		return new Trajectory() {
			
			@Override
			public OrientedPoint getPosition(double t) {
				if (t < startTime || t > startTime + getDuration()) 
					throw new IllegalArgumentException("The position for time " + t + " which is undefined for this trajectory");
				
				Point pos = waypoint;
				Vector dir = new Vector(0,1,0);
				
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
		return 0;
	}

	@Override
	public double getDuration() {
		return duration;
	}

}
