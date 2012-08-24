package cz.agents.alite.trajectorytools.util;

import java.util.LinkedList;
import java.util.List;

import cz.agents.alite.trajectorytools.trajectory.Trajectory;

public class VariableTimeStepTrajectoryApproximation {
	
	/**
	 * Approximate trajectory with samples of variable time step.
	 * @param trajectory Trajectory to be approximated.
	 * @param minTime Start time of the approximation.
	 * @param maxTime End time of the approximation.
	 * @param sampleStep Minimal sample step.
	 * @param maxAngle Maximal angle which is ignored. 
	 * @return
	 */
	public static List<TimePoint> approximate(Trajectory trajectory, double minTime, double maxTime, double sampleStep, double maxAngle){
		
		if(minTime < trajectory.getMinTime())minTime = trajectory.getMinTime();
		if(maxTime > trajectory.getMaxTime())maxTime = trajectory.getMaxTime();
		
		
		List<TimePoint> output = new LinkedList<TimePoint>();
		
		
		OrientedPoint prev = trajectory.getPosition(minTime);
		
		double prevt = minTime;
		
		//add first
		output.add(new TimePoint(prev,prevt));
		
		//add sampled
		for(double t = minTime+sampleStep; t < maxTime; t += sampleStep){
			OrientedPoint cur = trajectory.getPosition(t);
			
			if(Math.abs(prev.getAngle()-cur.getAngle()) > maxAngle){
				output.add(new TimePoint(cur,t));
				
				prev = cur;
				prevt = t;
			}
			
			
		}
		
		//add last
		OrientedPoint last = trajectory.getPosition(maxTime);
		output.add(new TimePoint(last,maxTime));
		
		return output;
		
	}

}
