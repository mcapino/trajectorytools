package tt.vis.problemcreator;

import cz.agents.alite.vis.Vis;
import tt.euclid2i.Point;
import tt.euclid2i.region.Polygon;

import javax.swing.*;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
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

    public void clearLast() {
        if (current != null) {
            current = null;
        } else {
            if (!polygons.isEmpty())
                polygons.remove(polygons.size() - 1);

        }
    }

    public void saveList() {
        String name = JOptionPane.showInputDialog(Vis.getInstance(), "Save as");

        try {
            File f = new File(name);
            ObjectOutputStream strem = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
            System.out.println(f.getAbsolutePath());

            strem.writeObject(polygons);
            strem.flush();
            strem.close();

            JOptionPane.showMessageDialog(Vis.getInstance(), "LinkedList<Polygon> serialized successfully", "Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(Vis.getInstance(), "LinkedList<Polygon> serialized unsuccessful", "Unsuccessful", JOptionPane.ERROR_MESSAGE);
        }
    }
}
