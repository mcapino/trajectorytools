package tt.vis.problemcreator;

import tt.euclid2i.Point;
import tt.euclid2i.region.Polygon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PolygonCreator {

    private List<Polygon> polygons;
    private Polygon current;

    public PolygonCreator() {
        polygons = new ArrayList<Polygon>();
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
}
