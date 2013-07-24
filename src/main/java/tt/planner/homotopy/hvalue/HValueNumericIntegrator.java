package tt.planner.homotopy.hvalue;

import org.jscience.mathematics.number.Complex;

import java.util.List;


/**
 * This class implements simple numeric integrator.
 */

public class HValueNumericIntegrator implements HValueIntegrator {

    private int samples;
    private List<Complex> qRoots;
    private List<Complex> pRoots;

    /**
     * @param qRoots N-1 roots of the nominator (samples of free space)
     * @param pRoots N roots of the denominator (samples representing obstacles)
     */
    public HValueNumericIntegrator(List<Complex> qRoots, List<Complex> pRoots, int samples) {
        this.qRoots = qRoots;
        this.pRoots = pRoots;
        this.samples = samples;
    }

    @Override
    public Complex lineSegmentIncrement(Complex start, Complex end) {
        Complex integralSum = Complex.ZERO;

        for (double i = 0; i < samples; i++) {
            double lambda = i / samples;
            Complex point = start.times(1 - lambda).plus(end.times(lambda));
            integralSum = integralSum.plus(fractionValue(point));
        }

        return integralSum.times(end.minus(start)).divide(samples);
    }

    private Complex fractionValue(Complex point) {
        Complex qEvaluated = logEvaluatePolynomial(qRoots, point);
        Complex pEvaluated = logEvaluatePolynomial(pRoots, point);
        return qEvaluated.minus(pEvaluated).exp();
    }

    private Complex logEvaluatePolynomial(List<Complex> roots, Complex point) {
        Complex evaluated = Complex.ZERO;
        for (int i = 0; i < roots.size(); i++) {
            evaluated = evaluated.plus(point.minus(roots.get(i)).log());
        }
        return evaluated;
    }
}
