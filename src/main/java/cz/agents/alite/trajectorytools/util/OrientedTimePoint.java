package cz.agents.alite.trajectorytools.util;

import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

public class OrientedTimePoint extends TimePoint {
    private static final long serialVersionUID = 5757663724715260037L;

    // A vector representing the orientation
    public Vector3d orientation;

    public OrientedTimePoint(TimePoint point, Vector3d orientation) {
        super(point);
        if (orientation == null) {
            throw new NullPointerException("Orientation cannot be null.");
        }
        this.orientation = new Vector(orientation);
    }

    public OrientedTimePoint(OrientedTimePoint orientedPoint) {
        super(orientedPoint);
        this.orientation = new Vector(orientedPoint.orientation);
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ orientation.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        try {
            return super.equals(other) && orientation.equals(((OrientedTimePoint) other).orientation);
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return super.toString() + "x" + orientation;
    }

    public void rotate(double angleInRads) {
        Matrix4d rotationMatrix = new Matrix4d();
        rotationMatrix.rotZ(angleInRads);
        rotationMatrix.transform(this.orientation);
    }

    public OrientedTimePoint getOppositeDirectionPoint() {
        Vector newOrientation = new Vector(orientation);
        newOrientation.x *= -1;
        newOrientation.y *= -1;

        return new OrientedTimePoint(this, newOrientation);
    }

      public double distanceInclOrientation(OrientedTimePoint p1)
      {
        double dx, dy, dz, dw, ddx, ddy, ddz;

        dx = this.x-p1.x;
        dy = this.y-p1.y;
        dz = this.z-p1.z;
        dw = this.w-p1.w;

        ddx = this.orientation.x - p1.orientation.x;
        ddy = this.orientation.y - p1.orientation.y;
        ddz = this.orientation.z - p1.orientation.z;

        return Math.sqrt(dx*dx + dy*dy + dz*dz +dw*dw + ddx*ddx + ddy*ddy + ddz*ddz);
      }

}
