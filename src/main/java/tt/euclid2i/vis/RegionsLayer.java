package tt.euclid2i.vis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Collection;

import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.region.Polygon;
import tt.euclid2i.region.Rectangle;
import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.layer.AbstractLayer;
import cz.agents.alite.vis.layer.VisLayer;

public class RegionsLayer extends AbstractLayer {

    public interface RegionsProvider {
        public Collection<? extends Region> getRegions();
    }

    private RegionsProvider regionsProvider;
    private Color edgeColor;
    private Color fillColor;

    RegionsLayer() {
    }

    public RegionsLayer(RegionsProvider regionsProvider, Color edgeColor, Color fillColor) {
        this.regionsProvider = regionsProvider;
        this.edgeColor = edgeColor;
        this.fillColor = fillColor;
    }

    @Override
    public void paint(Graphics2D canvas) {

        super.paint(canvas);

        Collection<? extends Region> regions = regionsProvider.getRegions();

        for (Region region : regions) {
            if (region instanceof Rectangle) {
                Rectangle rect = (Rectangle) region;

                canvas.setColor(fillColor);
                canvas.fillRect(Vis.transX(rect.getCorner1().x), Vis.transY(rect.getCorner1().y),
                        Vis.transX(rect.getCorner2().x) -  Vis.transX(rect.getCorner1().x),
                        Vis.transY(rect.getCorner2().y) -  Vis.transY(rect.getCorner1().y));

                canvas.setColor(edgeColor);
                canvas.drawRect(Vis.transX(rect.getCorner1().x), Vis.transY(rect.getCorner1().y),
                        Vis.transX(rect.getCorner2().x) -  Vis.transX(rect.getCorner1().x),
                        Vis.transY(rect.getCorner2().y) -  Vis.transY(rect.getCorner1().y));
            }

            if (region instanceof Polygon) {
                Polygon polygon = (Polygon) region;
                Point[] points = polygon.getPoints();

                int n = points.length;
                int x[] = new int[n];
                int y[] = new int[n];

                for (int i = 0; i < n; i++) {
                    x[i] = Vis.transX(points[i].x);
                    y[i] = Vis.transY(points[i].y);
                }

                canvas.setColor(fillColor);
                canvas.fillPolygon(x,y,n);

                canvas.setColor(edgeColor);
                canvas.drawPolygon(x,y,n);
            }
        }

    }

    public static VisLayer create(final RegionsProvider regionsProvider, final Color edgeColor, final Color fillColor) {
        return new RegionsLayer(regionsProvider, edgeColor, fillColor);
    }
}
