package tt.planner.homotopy;

import org.jscience.mathematics.number.Complex;

import java.util.List;

public class LValueNumericIntegrator implements LValueIntegrator {

    private int samples;
    private List<Complex> qRoots;
    private List<Complex> pRoots;

    public LValueNumericIntegrator(List<Complex> qRoots, List<Complex> pRoots, int samples) {
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
        Complex qEvaluated = evaluatePolynomial(qRoots, point);
        Complex pEvaluated = evaluatePolynomial(pRoots, point);
        return qEvaluated.divide(pEvaluated);
    }

    private Complex evaluatePolynomial(List<Complex> roots, Complex point) {
        Complex evaluated = Complex.ONE;
        for (int i = 0; i < roots.size(); i++) {
            evaluated = evaluated.times(point.minus(roots.get(i)));

        }
        return evaluated;
    }
}
