package tt.euclid2i.region;

import tt.euclid2i.Point;

/**
 * A region in an Euclidean 2d space
 */
public interface Region {
    public boolean intersectsLine(Point p1, Point p2);
    public boolean isInside(Point p);
}
