package tt.planner.homotopy.hvalue;

import tt.planner.homotopy.hvalue.HValueAnalyticIntegrator;
import tt.planner.homotopy.hvalue.HValueIntegrator;
import org.jscience.mathematics.number.Complex;

import java.util.List;

public class HValueAnalyticIntegratorTest extends AbstractHValueIntegratorTest {
    @Override
    HValueIntegrator initializeIntegrator(List<Complex> qRoots, List<Complex> pRoots) {
        return new HValueAnalyticIntegrator(qRoots, pRoots);
    }
}
