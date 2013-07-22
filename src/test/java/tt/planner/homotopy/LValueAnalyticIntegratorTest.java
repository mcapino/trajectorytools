package tt.planner.homotopy;

import org.jscience.mathematics.number.Complex;

import java.util.List;

public class LValueAnalyticIntegratorTest extends AbstractLValueIntegratorTest {
    @Override
    LValueIntegrator initializeIntegrator(List<Complex> qRoots, List<Complex> pRoots) {
        return new LValueAnalyticIntegrator(qRoots, pRoots);
    }
}
