package cz.agents.alite.trajectorytools.graph.spatiotemporal.region;

import javax.vecmath.Point3d;

import cz.agents.alite.trajectorytools.util.Point;
import cz.agents.alite.trajectorytools.util.TimePoint;

public class StaticBoxRegion implements Region {

    Point corner1;
    Point corner2;

    public StaticBoxRegion(Point corner1, Point corner2) {
        super();
        this.corner1 = corner1;
        this.corner2 = corner2;
    }

    @Override
    public boolean intersectsLine(TimePoint p1, TimePoint p2) {
        Point3d hitPoint = new Point3d();
        return isLineIntersectingBox(corner1, corner2, p1.getPoint3d(), p2.getPoint3d(), hitPoint);
    }

    @Override
    public boolean isInside(TimePoint p) {

        if (inBox(p.getPoint3d(), corner1, corner2, 1) &&
            inBox(p.getPoint3d(), corner1, corner2, 2) &&
            inBox(p.getPoint3d(), corner1, corner2, 3)) {
        return true;
        } else {
            return false;
        }
    }

    // found in Alite ... I guess it was originally taken from http://www.3dkingdoms.com/weekly/weekly.php?a=3
    // returns true if line (L1, L2) intersects with the box (B1, B2)
    // returns intersection point in hitPoint
    static boolean isLineIntersectingBox(Point3d boxCorner1, Point3d boxCorner2, Point3d lineEnd1, Point3d lineEnd2, Point3d hitPoint) {
        if (lineEnd2.x < boxCorner1.x && lineEnd1.x < boxCorner1.x)
            return false;
        if (lineEnd2.x > boxCorner2.x && lineEnd1.x > boxCorner2.x)
            return false;
        if (lineEnd2.y < boxCorner1.y && lineEnd1.y < boxCorner1.y)
            return false;
        if (lineEnd2.y > boxCorner2.y && lineEnd1.y > boxCorner2.y)
            return false;
        if (lineEnd2.z < boxCorner1.z && lineEnd1.z < boxCorner1.z)
            return false;
        if (lineEnd2.z > boxCorner2.z && lineEnd1.z > boxCorner2.z)
            return false;
        if (lineEnd1.x > boxCorner1.x && lineEnd1.x < boxCorner2.x && lineEnd1.y > boxCorner1.y && lineEnd1.y < boxCorner2.y && lineEnd1.z > boxCorner1.z && lineEnd1.z < boxCorner2.z) {
            hitPoint.set(lineEnd1);
            return true;
        }
        if ((getIntersection(lineEnd1.x - boxCorner1.x, lineEnd2.x - boxCorner1.x, lineEnd1, lineEnd2, hitPoint) && inBox(hitPoint, boxCorner1, boxCorner2, 1))
                || (getIntersection(lineEnd1.y - boxCorner1.y, lineEnd2.y - boxCorner1.y, lineEnd1, lineEnd2, hitPoint) && inBox(hitPoint, boxCorner1, boxCorner2, 2))
                || (getIntersection(lineEnd1.z - boxCorner1.z, lineEnd2.z - boxCorner1.z, lineEnd1, lineEnd2, hitPoint) && inBox(hitPoint, boxCorner1, boxCorner2, 3))
                || (getIntersection(lineEnd1.x - boxCorner2.x, lineEnd2.x - boxCorner2.x, lineEnd1, lineEnd2, hitPoint) && inBox(hitPoint, boxCorner1, boxCorner2, 1))
                || (getIntersection(lineEnd1.y - boxCorner2.y, lineEnd2.y - boxCorner2.y, lineEnd1, lineEnd2, hitPoint) && inBox(hitPoint, boxCorner1, boxCorner2, 2))
                || (getIntersection(lineEnd1.z - boxCorner2.z, lineEnd2.z - boxCorner2.z, lineEnd1, lineEnd2, hitPoint) && inBox(hitPoint, boxCorner1, boxCorner2, 3)))
            return true;

        return false;
    }


    static boolean getIntersection(double fDst1, double fDst2, Point3d P1, Point3d P2, Point3d hit) {
        if ((fDst1 * fDst2) >= 0.0)
            return false;
        if (fDst1 == fDst2)
            return false;

        hit.set(P2);
        hit.sub(P1);
        hit.scale(-fDst1 / (fDst2 - fDst1));
        hit.add(P1);
        return true;
    }

    static boolean inBox(Point3d hitPoint, Point3d boxCorner1, Point3d boxCorner2, final int axis) {
        if (axis == 1 && hitPoint.z >= boxCorner1.z && hitPoint.z <= boxCorner2.z && hitPoint.y >= boxCorner1.y && hitPoint.y <= boxCorner2.y)
            return true;
        if (axis == 2 && hitPoint.z >= boxCorner1.z && hitPoint.z <= boxCorner2.z && hitPoint.x >= boxCorner1.x && hitPoint.x <= boxCorner2.x)
            return true;
        if (axis == 3 && hitPoint.x >= boxCorner1.x && hitPoint.x <= boxCorner2.x && hitPoint.y >= boxCorner1.y && hitPoint.y <= boxCorner2.y)
            return true;
        return false;
    }

    public Point getCorner1() {
        return corner1;
    }

    public Point getCorner2() {
        return corner2;
    }

}
