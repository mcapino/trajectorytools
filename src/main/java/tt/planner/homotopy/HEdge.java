package tt.planner.homotopy;

import org.jscience.mathematics.number.Complex;

public class HEdge<E> {

    E edge;
    Complex lIncrement;

    public HEdge(E edge, Complex lIncrement) {
        this.edge = edge;
        this.lIncrement = lIncrement;
    }


}
