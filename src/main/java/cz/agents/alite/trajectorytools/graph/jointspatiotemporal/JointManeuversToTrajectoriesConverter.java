package cz.agents.alite.trajectorytools.graph.jointspatiotemporal;

import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.GraphPathImpl;

import cz.agents.alite.tactical.util.Converter;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.maneuvers.SpatioTemporalManeuver;
import cz.agents.alite.trajectorytools.trajectory.SpatioTemporalManeuverTrajectory;
import cz.agents.alite.trajectorytools.trajectory.Trajectory;
import cz.agents.alite.trajectorytools.trajectorymetrics.TrajecotryDistanceMetricTest;
import cz.agents.alite.trajectorytools.util.TimePoint;

public class JointManeuversToTrajectoriesConverter {

    public JointManeuversToTrajectoriesConverter() {
        // TODO Auto-generated constructor stub
    }

    public static Trajectory[] convert(GraphPath<JointState, JointManeuver> path) {

        JointState startJointState = path.getStartVertex();
        List<JointManeuver> jointManeuvers = path.getEdgeList();
        Graph<JointState, JointManeuver> jointGraph = path.getGraph();

        int nAgents = path.getStartVertex().nAgents();
        Trajectory[] trajectories =  new Trajectory[nAgents];

        for (int i = 0; i < nAgents; i++) {
            // Processing agent i
            
        	Graph<TimePoint, SpatioTemporalManeuver> graph = new DefaultDirectedGraph<TimePoint, SpatioTemporalManeuver>(SpatioTemporalManeuver.class);
            TimePoint startTimePoint = startJointState.get(i);
            List<SpatioTemporalManeuver> edges = new LinkedList<SpatioTemporalManeuver>();
            TimePoint endTimePoint = path.getEndVertex().get(i);
            double duration = 0.0;
            
            JointState currentJointState = startJointState;
            graph.addVertex(currentJointState.get(i));
            for (JointManeuver jointManeuver : jointManeuvers) {
                JointState jointManeuverEnd = jointGraph.getEdgeTarget(jointManeuver);
                graph.addVertex(jointManeuverEnd.get(i));
                SpatioTemporalManeuver edge = jointGraph.getEdge(currentJointState, jointManeuverEnd).get(i);
                edges.add(edge);
                duration += edge.getDuration();
            }
            
            GraphPath<TimePoint, SpatioTemporalManeuver> singleGraphPath 
            	= new GraphPathImpl<TimePoint, SpatioTemporalManeuver>(graph, startTimePoint, endTimePoint, edges, duration);
            
            trajectories[i] = new SpatioTemporalManeuverTrajectory<TimePoint, SpatioTemporalManeuver>(singleGraphPath, duration);
        }

        return trajectories;
    }

}
