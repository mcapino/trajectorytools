package tt.planner.homotopy.hvalue;

import org.jscience.mathematics.number.Complex;

import java.util.HashSet;
import java.util.Set;

public class HValueAllowPolicy implements HValuePolicy {

    private Set<Complex> allowed;

    public HValueAllowPolicy() {
        allowed = new HashSet<Complex>();
    }

    @Override
    public boolean isAllowed(Complex hValue, double precision) {
        for (Complex value : allowed) {
            double diff = value.minus(hValue).magnitude();
            double abs = (value.magnitude() + hValue.magnitude()) / 2;

            if (diff / abs < precision) return true;
        }
        return false;
    }

    public void allow(Complex hValue) {
        allowed.add(hValue);
    }
}
