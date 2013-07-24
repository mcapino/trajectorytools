package tt.planner.homotopy.hvalue;

import org.jscience.mathematics.number.Complex;

public interface HValueIntegrator {

    public Complex lineSegmentIncrement(Complex start, Complex end);
}
