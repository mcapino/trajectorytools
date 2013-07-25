package tt.planner.homotopy.hvalue;

import tt.planner.homotopy.hvalue.HValueIntegrator;
import org.jscience.mathematics.number.Complex;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public abstract class AbstractHValueIntegratorTest {

    private static final double PRECISION = 0.01;

    protected HValueIntegrator integrator;
    protected List<Complex> testPath;

    abstract HValueIntegrator initializeIntegrator(List<Complex> qRoots, List<Complex> pRoots);

    @Before
    public void setUp() throws Exception {
        List<Complex> qRoots = new ArrayList<Complex>();
        qRoots.add(Complex.valueOf(1.51364, 8.76351));
        qRoots.add(Complex.valueOf(1.28651, 2.3531));

        List<Complex> pRoots = new ArrayList<Complex>();
        pRoots.add(Complex.valueOf(0, 0));
        pRoots.add(Complex.valueOf(0, 10));
        pRoots.add(Complex.valueOf(10, 0));

        testPath = new ArrayList<Complex>();
        testPath.add(Complex.valueOf(1, 1));
        testPath.add(Complex.valueOf(-1, 2));
        testPath.add(Complex.valueOf(-1, -1));
        testPath.add(Complex.valueOf(1, -1));

        integrator = initializeIntegrator(qRoots, pRoots);
    }


    @Test
    public void testLineSegmentIncrement() throws Exception {
        assertIncrementCorrect(testPath.get(0), testPath.get(1), Complex.valueOf(-0.0277826, 0.146181));
        assertIncrementCorrect(testPath.get(1), testPath.get(2), Complex.valueOf(-0.340719, 0.412643));
        assertIncrementCorrect(testPath.get(2), testPath.get(3), Complex.valueOf(-0.442806, 0.309466));
        assertIncrementCorrect(testPath.get(3), testPath.get(0), Complex.valueOf(-0.362021, 0.0638892));
    }

    private void assertIncrementCorrect(Complex from, Complex to, Complex expected) throws Exception {
        Complex value = integrator.lineSegmentIncrement(from, to);
        assertTrue(complexEquals(value, expected));
    }

    public boolean complexEquals(Complex real, Complex expected) {
        double errorAbs = real.minus(expected).magnitude();
        double expectedAbs = expected.magnitude();
        return Math.abs(errorAbs / expectedAbs) < PRECISION;
    }
}
