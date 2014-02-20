package tt.euclid2d;

public class Geometry2d {

    private Geometry2d() {
    }

    public static double distance(Line l, Point p) {
        Point x = l.getStart();
        Point y = l.getEnd();

        Point dpx = sub(p, x);
        Point dyx = sub(y, x);

        double nominator = dot(dpx, dyx);
        double denominator = dot(dyx, dyx);

        double u;
        if (denominator > 0)
            if (nominator <= 0)
                u = 0;
            else if (nominator >= denominator)
                u = 1;
            else
                u = nominator / denominator;
        else
            u = 0;

        Point nearest = add(x, scale(dyx, u));

        return nearest.distance(p);
    }

    public static Point scale(Point point, double s) {
        return new Point(point.x * s, point.y * s);
    }

    public static double dot(Point a, Point b) {
        return a.x * b.x + a.y * b.y;
    }

    public static Point sub(Point a, Point b) {
        return new Point(a.x - b.x, a.y - b.y);
    }

    public static Point add(Point a, Point b) {
        return new Point(a.x + b.x, a.y + b.y);
    }
}
