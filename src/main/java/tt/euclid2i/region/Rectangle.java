package tt.euclid2i.region;

import tt.euclid2d.util.Intersection;
import tt.euclid2i.Point;
import tt.euclid2i.Region;

public class Rectangle implements Region {

    Point corner1;
    Point corner2;

    public Rectangle(Point corner1, Point corner2) {
        super();
        this.corner1 = corner1;
        this.corner2 = corner2;
    }

    @Override
    public boolean intersectsLine(Point p1, Point p2) {
        tt.euclid2d.Point startPoint = new tt.euclid2d.Point(p1.x, p1.y);
        tt.euclid2d.Point endPoint = new tt.euclid2d.Point(p2.x, p2.y);

        return Intersection.linesIntersect(startPoint, endPoint, new tt.euclid2d.Point(corner1.x, corner1.y), new tt.euclid2d.Point(corner1.x, corner2.y), true) ||
                Intersection.linesIntersect(startPoint, endPoint, new tt.euclid2d.Point(corner1.x, corner2.y), new tt.euclid2d.Point(corner2.x, corner2.y), true) ||
                Intersection.linesIntersect(startPoint, endPoint, new tt.euclid2d.Point(corner2.x, corner2.y), new tt.euclid2d.Point(corner2.x, corner1.y), true) ||
                Intersection.linesIntersect(startPoint, endPoint, new tt.euclid2d.Point(corner2.x, corner1.y), new tt.euclid2d.Point(corner1.x, corner1.y), true) ;
    }

    @Override
    public boolean isInside(Point p) {
        return corner1.x <= p.x && p.x <= corner2.x &&
                corner1.y <= p.y && p.y <= corner2.y;
    }

    public Point getCorner1() {
        return corner1;
    }

    public Point getCorner2() {
        return corner2;
    }

    public Rectangle inflate(int inflateBy) {
        int minx = Math.min(corner1.x, corner2.x);
        int maxx = Math.max(corner1.x, corner2.x);
        int miny = Math.min(corner1.y, corner2.y);
        int maxy = Math.max(corner1.y, corner2.y);

        return new Rectangle(new Point(minx-inflateBy, miny-inflateBy), new Point(maxx+inflateBy, maxy+inflateBy));
    }

	@Override
	public Rectangle getBoundingBox() {
		return this;
	}



}
