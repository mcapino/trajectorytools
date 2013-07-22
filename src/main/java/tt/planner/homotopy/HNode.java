package tt.planner.homotopy;

import org.jscience.mathematics.number.Complex;

public class HNode<V> {

    private final V node;
    private final Complex lValue;
    private final double precision;

    public HNode(V node, Complex lValue, double comparisonPrecision) {
        this.node = node;
        this.lValue = lValue;
        this.precision = comparisonPrecision;
    }

    public V getNode() {
        return node;
    }

    public Complex getlValue() {
        return lValue;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HNode hNode = (HNode) o;

        //Symmetry
        double diff = lValue.minus(hNode.lValue).magnitude();
        double abs = lValue.plus(hNode.lValue).divide(2).magnitude();

        if (diff / abs > precision) return false;
        if (!node.equals(hNode.node)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return node.hashCode();
    }
}
