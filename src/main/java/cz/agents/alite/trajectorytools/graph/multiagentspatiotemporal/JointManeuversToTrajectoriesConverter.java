package cz.agents.alite.trajectorytools.graph.multiagentspatiotemporal;

import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultDirectedGraph;

import cz.agents.alite.tactical.util.Converter;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.maneuvers.SpatioTemporalManeuver;
import cz.agents.alite.trajectorytools.trajectory.Trajectory;
import cz.agents.alite.trajectorytools.trajectorymetrics.TrajecotryDistanceMetricTest;
import cz.agents.alite.trajectorytools.util.TimePoint;

public class JointManeuversToTrajectoriesConverter {

    public JointManeuversToTrajectoriesConverter() {
        // TODO Auto-generated constructor stub
    }

    public static Trajectory[] convert(GraphPath<JointState, JointManeuver> path) {

        JointState startJointState = path.getStartVertex();
        JointState endJointState = path.getEndVertex();
        List<JointManeuver> jointManeuvers = path.getEdgeList();
        Graph<JointState, JointManeuver> jointGraph = path.getGraph();

        int nAgents = path.getStartVertex().nAgents();
        Trajectory[] trajetories =  new Trajectory[nAgents];

        for (int i = 0; i < nAgents; i++) {
            // Processing agent i
            Graph<TimePoint, SpatioTemporalManeuver> graph = new DefaultDirectedGraph<TimePoint, SpatioTemporalManeuver>(SpatioTemporalManeuver.class);
            TimePoint start = startJointState.get(i);
            List<SpatioTemporalManeuver> edges = new LinkedList<SpatioTemporalManeuver>();

            JointState current = startJointState;
            graph.addVertex(current.get(i));
            for (JointManeuver jointManeuver : jointManeuvers) {
                JointState jointEnd = jointGraph.getEdgeSource(jointManeuver);
                graph.addVertex(jointEnd.get(i));
                edges.add(jointGraph.getEdge(current, jointEnd).get(i));
            }
        }

        JointState current =

    }

}
