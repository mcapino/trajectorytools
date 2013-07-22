package tt.planner.homotopy;

import org.jscience.mathematics.number.Complex;

import java.util.List;

public class LValueNumericIntegratorTest extends AbstractLValueIntegratorTest {

    private static final int SAMPLES_PER_EDGE = 1000;

    @Override
    LValueIntegrator initializeIntegrator(List<Complex> qRoots, List<Complex> pRoots) {
        return new LValueNumericIntegrator(qRoots, pRoots, SAMPLES_PER_EDGE);
    }
}
