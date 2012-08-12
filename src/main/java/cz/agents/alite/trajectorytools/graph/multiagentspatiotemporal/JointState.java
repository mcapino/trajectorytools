package cz.agents.alite.trajectorytools.graph.multiagentspatiotemporal;

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
            dist += this.get(i).distance(other.get(i));
        }

        return dist;
    }
}
