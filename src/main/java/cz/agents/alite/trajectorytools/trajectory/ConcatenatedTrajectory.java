package cz.agents.alite.trajectorytools.trajectory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import cz.agents.alite.trajectorytools.util.OrientedPoint;

/**
 * A wrapper that interprets a list of trajectories as one concatenated trajectory. The trajectories must align
 * in time in such way that two consecutive trajectories must end and start in the same time respectively.
 * @author stolba
 *
 */

public class ConcatenatedTrajectory implements Trajectory {
	
	private final LinkedList<Trajectory> trajectories;
	
	private final double minTime;
	private final double maxTime;
	
	public ConcatenatedTrajectory(Trajectory t1, Trajectory t2){
		this(Arrays.asList(t1,t2));
	}
	
	public ConcatenatedTrajectory(List<Trajectory> inputTrajectories){
		
		if(inputTrajectories.isEmpty()){
			throw new IllegalArgumentException("Empty list of trajectories!");
		}
		
		trajectories = new LinkedList<Trajectory>(inputTrajectories);
		
		minTime = trajectories.getFirst().getMinTime();
		maxTime = trajectories.getLast().getMaxTime();
		
		//check consistency
		double t = minTime;
		
		for(Trajectory trajectory : trajectories){
			if(trajectory.getMinTime() != t){
				throw new IllegalArgumentException("Concatenated trajectories must align in time!");
			}
			
			t = trajectory.getMaxTime();
		}
		
	}

	@Override
	public double getMinTime() {
		return minTime;
	}

	@Override
	public double getMaxTime() {
		return maxTime;
	}

	@Override
	public OrientedPoint getPosition(double t) {
		if(t < minTime || t > maxTime){
			return null;
		}
		
		for(Trajectory trajectory : trajectories){
			if(t >= trajectory.getMinTime() && t <= trajectory.getMaxTime()){
				return trajectory.getPosition(t);
			}
		}
		
		throw new RuntimeException("Time point not found! Underlying trajectories must have changed illegaly!");
		
	}

}
