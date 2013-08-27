package tt.vis.problemcreator;

import tt.euclid2i.Point;
import tt.euclid2i.region.Polygon;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PolygonCreator {

    private LinkedList<Polygon> polygons;
    private Polygon current;

    public PolygonCreator() {
        polygons = new LinkedList<Polygon>();
    }

    public List<Polygon> getPolygons() {
        return polygons;
    }

    @SuppressWarnings("unchecked")
    public List<Polygon> getCurrent() {
        if (current != null) {
            return Collections.singletonList(current);
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    public void addPoint(Point point) {
        if (current != null) {
            current.addPoint(point);
        } else {
            current = new Polygon(new Point[]{point});
        }
    }

    public void savePolygon() {
        if (current != null) {
            polygons.add(current);
            current = null;
        }
    }

    public void clearLast() {
        if (current != null) {
            current = null;
        } else {
            if (!polygons.isEmpty())
                polygons.removeLast();
        }
    }
}
