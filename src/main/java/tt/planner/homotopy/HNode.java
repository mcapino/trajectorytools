package tt.planner.homotopy;

import tt.planner.homotopy.hvalue.HClass;
import tt.planner.homotopy.hvalue.HClassDiscretized;
import org.jscience.mathematics.number.Complex;

public class HNode<V> {

    private final V node;
    private final Complex lValue;
    private final HClass lClass;
    private final double precision;

    //TODO fix passing precision in the constructor all the time...

    public HNode(V node, Complex lValue, double comparisonPrecision) {
        this.node = node;
        this.lValue = lValue;
        this.precision = comparisonPrecision;
        this.lClass = new HClassDiscretized(lValue, precision);
    }

    public Complex getlValue() {
        return lValue;
    }

    public V getNode() {
        return node;
    }

    public double getPrecision() {
        return precision;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HNode hNode = (HNode) o;

        if (!lClass.equals(hNode.lClass)) return false;
        if (!node.equals(hNode.node)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = node.hashCode();
        result = 31 * result + lClass.hashCode();
        return result;
    }
}
