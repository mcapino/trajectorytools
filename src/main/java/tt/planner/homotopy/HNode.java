package tt.planner.homotopy;

import org.jscience.mathematics.number.Complex;
import tt.planner.homotopy.hclass.HClass;

public class HNode<V> {

    private final V node;
    private final Complex lValue;
    private final HClass lClass;

    //TODO fix passing precision in the constructor all the time...

    HNode(V node, Complex lValue, HClass lClass) {
        this.node = node;
        this.lValue = lValue;
        this.lClass = lClass;
    }

    public Complex getlValue() {
        return lValue;
    }

    public V getNode() {
        return node;
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
