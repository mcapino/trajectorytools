package tt.euclidtime3i.discretization;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.GraphDelegator;

import tt.euclid2i.SegmentedTrajectory;
import tt.euclid2i.Trajectory;
import tt.euclid2i.trajectory.BasicSegmentedTrajectory;
import tt.euclid2i.util.SeparationDetector;
import tt.euclidtime3i.Point;

public class SeparationConstraintWrapper extends GraphDelegator<Point, Straight> implements DirectedGraph<Point, Straight>  {

    private Trajectory[] otherTrajs;
    private int separation;

    public SeparationConstraintWrapper(DirectedGraph<Point, Straight> g, Trajectory[] otherTrajs, int separation) {
        super(g);
        this.otherTrajs = otherTrajs;
        this.separation = separation;
    }

    @Override
    public Set<Straight> outgoingEdgesOf(Point vertex) {
        Set<Straight> allEdges = super.outgoingEdgesOf(vertex);
        Set<Straight> consistentEdges = new HashSet<Straight>();

        for (Straight edge : allEdges) {
            if (consistent(edge, Arrays.asList(otherTrajs), separation)) {
                consistentEdges.add(edge);
            }
        }

        return consistentEdges;
    }


    private boolean consistent(Straight e, Collection<Trajectory> otherTrajs, int sep) {

        int duration = e.getEnd().getTime() - e.getStart().getTime();
        double distance = e.getStart().getPosition().distance(e.getEnd().getPosition());

        Trajectory thisTraj = new BasicSegmentedTrajectory(Arrays.asList(e), duration, super.getEdgeWeight(e));

        Collection<SegmentedTrajectory> segmentedTrajectories = new LinkedList<SegmentedTrajectory>();
        Collection<Trajectory> nonsegmentedTrajectories = new LinkedList<Trajectory>();
        for (Trajectory otherTraj : otherTrajs) {
            if (!(otherTraj instanceof SegmentedTrajectory)) {
               segmentedTrajectories.add((SegmentedTrajectory) otherTraj);
            } else {
                nonsegmentedTrajectories.add(otherTraj);
            }
        }


        return !SeparationDetector.hasAnyPairwiseConflictAnalytic((tt.euclid2i.SegmentedTrajectory) thisTraj, segmentedTrajectories, sep)
                && !SeparationDetector.hasAnyPairwiseConflict(thisTraj, nonsegmentedTrajectories, sep, sep/4) ;
    }

}
