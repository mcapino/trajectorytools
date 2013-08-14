package tt.euclid2d;

import javax.vecmath.Point2d;
import javax.vecmath.Point2i;
import javax.vecmath.Tuple2d;

public class Point extends Point2d {

    public Point() {
        super();
    }

    public Point(double x, double y) {
        super(x, y);
    }

    public Point(Point2i p) {
        super(p.x, p.y);
    }

    public Point(Tuple2d t1) {
        super(t1);
    }

    private static final long serialVersionUID = 1936317975431366847L;
//    private static final int DECIMAL_PLACES_FOR_EQUALS_AND_HASH_CODE = 4;

    public static Point interpolate(Point p1, Point p2, double alpha) {
        Point result = new Point();
        ((Tuple2d) result).interpolate(p1, p2, alpha);
        return result;
    }

//    @Override
//    public String toString() {
//        return String.format("(%.2f, %.2f)", x, y);
//    }

//    @Override
//    public int hashCode() {
//    	int hash = (toFixedPoint(x, DECIMAL_PLACES_FOR_EQUALS_AND_HASH_CODE) ^ toFixedPoint(y, DECIMAL_PLACES_FOR_EQUALS_AND_HASH_CODE));
//        return hash;
//    }
//
//    @Override
//    public boolean equals(Tuple2d t1)
//    {
//        try {
//           return(toFixedPoint(this.x, DECIMAL_PLACES_FOR_EQUALS_AND_HASH_CODE) == toFixedPoint(t1.x, DECIMAL_PLACES_FOR_EQUALS_AND_HASH_CODE)
//                  && toFixedPoint(this.y, DECIMAL_PLACES_FOR_EQUALS_AND_HASH_CODE) == toFixedPoint(t1.y, DECIMAL_PLACES_FOR_EQUALS_AND_HASH_CODE));
//        }
//        catch (NullPointerException e2) {return false;}
//
//    }
//
//    /**
//     *  Converts a double value to a fixed-point representation
//     *  @param d the original double
//     *  @param n the number of decimal places after the radix point to be considered
//     *  @returns a long value which approximately corresponds to d*10^n
//     */
//    static int toFixedPoint(double d, int n) {
//        int value = (int) Math.round(d * Math.pow(10,n));
//        return value;
//    }


}
