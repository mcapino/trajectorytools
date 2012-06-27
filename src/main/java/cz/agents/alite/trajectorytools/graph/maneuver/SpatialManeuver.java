package cz.agents.alite.trajectorytools.graph.maneuver;

import cz.agents.alite.trajectorytools.trajectory.Trajectory;

public interface SpatialManeuver {
	/**
	 * @param startTime time at which the maneuver starts
	 * @return the trajectory of the maneuver
	 */
	Trajectory getTrajectory(double startTime);
	
	/**
	 * @return the length of the maneuver in meters
	 */
	double getDistance(); 
	/**
	 * @return the duration of the maneuver in seconds
	 */
	double getDuration();
	/**
	 * @return the cost of the maneuver (the optimization criteria)
	 */
	double getCost();	
}
