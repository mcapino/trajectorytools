package tt.euclidtime3i.discretization;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.GraphDelegator;

import tt.euclid2i.SegmentedTrajectory;
import tt.euclid2i.Trajectory;
import tt.euclid2i.trajectory.BasicSegmentedTrajectory;
import tt.euclid2i.util.SeparationDetector;
import tt.euclidtime3i.Point;

@SuppressWarnings("serial")
public class SeparationConstraintWrapper extends GraphDelegator<Point, Straight> implements DirectedGraph<Point, Straight>  {

    private Trajectory[] otherTrajs;
    private int[] separations;

    public SeparationConstraintWrapper(DirectedGraph<Point, Straight> g, Trajectory[] otherTrajs, int[] separations) {
        super(g);
        this.otherTrajs = otherTrajs;
        this.separations = separations;
    }

    @Override
    public Set<Straight> outgoingEdgesOf(Point vertex) {
        Set<Straight> allEdges = super.outgoingEdgesOf(vertex);
        Set<Straight> consistentEdges = new HashSet<Straight>();

        for (Straight edge : allEdges) {
            if (consistent(edge, otherTrajs, separations)) {
                consistentEdges.add(edge);
            }
        }

        return consistentEdges;
    }


    private boolean consistent(Straight e, Trajectory otherTrajs[], int[] separations) {

        int duration = e.getEnd().getTime() - e.getStart().getTime();

        Trajectory thisTraj = new BasicSegmentedTrajectory(Arrays.asList(e), duration, super.getEdgeWeight(e));

        SegmentedTrajectory[] segmentedTrajs = new SegmentedTrajectory[otherTrajs.length];
        for (int i=0; i<otherTrajs.length; i++) {
            assert(otherTrajs[i] instanceof SegmentedTrajectory);
            segmentedTrajs[i] = (SegmentedTrajectory) otherTrajs[i];
        }


        return !SeparationDetector.hasAnyPairwiseConflict(thisTraj, otherTrajs, separations, 10);
        //return !SeparationDetector.hasAnyPairwiseConflictAnalytic((tt.euclid2i.SegmentedTrajectory) thisTraj, segmentedTrajs, separations);
    }

}
