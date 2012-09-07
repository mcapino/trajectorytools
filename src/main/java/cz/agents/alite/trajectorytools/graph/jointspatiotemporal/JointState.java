package cz.agents.alite.trajectorytools.graph.jointspatiotemporal;

import java.util.Arrays;

import cz.agents.alite.trajectorytools.util.TimePoint;

public class JointState {

    final TimePoint[] agentStates;

    public JointState(TimePoint[] agentStates) {
        this.agentStates = agentStates;
    }

    public int nAgents() {
        return agentStates.length;
    }

    public TimePoint get(int n) {
        return agentStates[n];
    }

    public double distance(JointState other) {
        double dist = 0.0;

        if (this.nAgents() != other.nAgents()) {
            throw new IllegalArgumentException("Joint states have different sizes.");
        }

        for (int i = 0 ; i < this.nAgents(); i++) {
            dist += this.get(i).getSpatialPoint().distance(other.get(i).getSpatialPoint());
        }

        return dist;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(agentStates);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JointState other = (JointState) obj;
		if (!Arrays.equals(agentStates, other.agentStates))
			return false;
		return true;
	}
    
}
