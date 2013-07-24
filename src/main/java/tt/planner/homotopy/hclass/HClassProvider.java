package tt.planner.homotopy.hclass;

import org.jscience.mathematics.number.Complex;

public interface HClassProvider {

    public HClass assignHClass(Complex c, double precision);
}
