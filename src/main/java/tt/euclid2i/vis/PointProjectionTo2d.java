package tt.euclid2i.vis;

import javax.vecmath.Point2d;

import tt.euclid2i.Point;

public class PointProjectionTo2d implements cz.agents.alite.trajectorytools.vis.projection.ProjectionTo2d<Point> {

    @Override
    public Point2d project(Point point) {
        return new Point2d(point.x, point.y);
    }

}
