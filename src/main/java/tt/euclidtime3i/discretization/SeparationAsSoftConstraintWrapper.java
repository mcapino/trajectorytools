package tt.euclidtime3i.discretization;

import java.util.Collection;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.GraphDelegator;

import tt.euclidtime3i.Point;
import tt.euclidtime3i.Region;
import tt.euclidtime3i.region.MovingCircle;

public class SeparationAsSoftConstraintWrapper<V extends Point, E extends Straight> extends GraphDelegator<V,E>{
    private Collection<Region> dynamicObstacles;
    private double maxViolationCost;

    public SeparationAsSoftConstraintWrapper(DirectedGraph<V, E> g, Collection<Region> dynamicObstacles,
            double maxViolationCost) {
        super(g);
        this.maxViolationCost = maxViolationCost;
        this.dynamicObstacles = dynamicObstacles;
    }

    @Override
    public double getEdgeWeight(E e) {
        double cost = super.getEdgeWeight(e);

        for (Region region : dynamicObstacles) {
            if (region.intersectsLine(e.getStart(), e.getEnd())) {
                MovingCircle mc = (MovingCircle) region;

                // tanh should work here: 1+tanh(1/sepdist) should be good


                cost += maxViolationCost;
            }
        }

        return cost;
    }

}
