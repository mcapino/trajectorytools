package tt.planner.homotopy;

import org.jscience.mathematics.number.Complex;

import java.util.List;

public class LValueNumericIntegratorTest extends AbstractLValueIntegratorTest {
    @Override
    LValueIntegrator initializeIntegrator(List<Complex> qRoots, List<Complex> pRoots) {
        return new LValueNumericIntegrator(qRoots, pRoots, 1000);
    }
}
