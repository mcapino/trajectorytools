package tt.planner.homotopy;

import org.jscience.mathematics.number.Complex;

public interface LValueIntegrator {

    public Complex lineSegmentIncrement(Complex start, Complex end);
}
