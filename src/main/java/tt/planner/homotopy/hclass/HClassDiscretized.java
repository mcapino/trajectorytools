package tt.planner.homotopy.hclass;

import org.jscience.mathematics.number.Complex;

public class HClassDiscretized implements HClass {
    private static final double MIN_VALUE = 1E-200;

    private long re;
    private long im;
    private int quadrant;

    public HClassDiscretized(Complex c, double precision) {
        double dRe = c.getReal();
        double dIm = c.getImaginary();

        int sRe = (int) Math.signum(dRe);
        int sIm = (int) Math.signum(dIm);

        re = (long) (Math.log(Math.abs(dRe) + MIN_VALUE) * sRe / precision);
        im = (long) (Math.log(Math.abs(dIm) + MIN_VALUE) * sIm / precision);

        quadrant = (sRe > 0 ? 1 : 0) + (dIm > 0 ? 2 : 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HClassDiscretized that = (HClassDiscretized) o;

        return im == that.im && quadrant == that.quadrant && re == that.re;
    }

    @Override
    public int hashCode() {
        int result = (int) (re ^ (re >>> 32));
        result = 31 * result + (int) (im ^ (im >>> 32));
        result = 31 * result + quadrant;
        return result;
    }
    
    public static class Provider implements HClassProvider{

        @Override
        public HClass assignHClass(Complex c, double precision) {
            return new HClassDiscretized(c, precision);
        }
    }
}
