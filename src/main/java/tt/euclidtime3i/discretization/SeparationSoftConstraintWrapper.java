package tt.euclidtime3i.discretization;

import java.util.Collection;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.GraphDelegator;

import tt.euclidtime3i.Point;
import tt.euclidtime3i.Region;

public class SeparationSoftConstraintWrapper<V extends Point, E extends Straight> extends GraphDelegator<V,E>{
    private Collection<Region> dynamicObstacles;
    private double violationCost;

    public SeparationSoftConstraintWrapper(DirectedGraph<V, E> g, Collection<Region> dynamicObstacles,
            double violationCost) {
        super(g);
        this.violationCost = violationCost;
        this.dynamicObstacles = dynamicObstacles;
    }

    @Override
    public double getEdgeWeight(E e) {
        double cost = super.getEdgeWeight(e);

        for (Region region : dynamicObstacles) {
            if (region.isInside(e.getEnd())) {
                cost += violationCost;
            }
        }

        return cost;
    }

}
