package tt.planner.homotopy.hvalue;

import org.jscience.mathematics.number.Complex;

import java.util.List;

public class HValueNumericIntegratorTest extends AbstractHValueIntegratorTest {

    private static final int SAMPLES_PER_EDGE = 1000;

    @Override
    HValueIntegrator initializeIntegrator(List<Complex> qRoots, List<Complex> pRoots) {
        return new HValueNumericIntegrator(qRoots, pRoots, 1, SAMPLES_PER_EDGE);
    }
}
