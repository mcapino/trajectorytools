package tt.euclid2i.vis;

import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.layer.AbstractLayer;
import cz.agents.alite.vis.layer.VisLayer;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.region.Polygon;
import tt.euclid2i.region.Rectangle;

import java.awt.*;
import java.util.Collection;

public class RegionsLayer extends AbstractLayer {

    public interface RegionsProvider {
        Collection<? extends Region> getRegions();
    }

    private RegionsProvider regionsProvider;
    private Color edgeColor;
    private Color fillColor;
    private boolean fill;

    RegionsLayer() {
    }

    public RegionsLayer(RegionsProvider regionsProvider, Color edgeColor, Color fillColor, boolean fill) {
        this.regionsProvider = regionsProvider;
        this.edgeColor = edgeColor;
        this.fillColor = fillColor;
        this.fill = fill;
    }

    public RegionsLayer(RegionsProvider regionsProvider, Color edgeColor, Color fillColor) {
        this(regionsProvider, edgeColor, fillColor, true);
    }

    @Override
    public void paint(Graphics2D canvas) {

        super.paint(canvas);

        Collection<? extends Region> regions = regionsProvider.getRegions();

        for (Region region : regions) {
            if (region instanceof Rectangle) {
                Rectangle rect = (Rectangle) region;

                if (!fill) {
                    canvas.setColor(fillColor);
                    canvas.fillRect(Vis.transX(rect.getCorner1().x), Vis.transY(rect.getCorner1().y),
                            Vis.transX(rect.getCorner2().x) - Vis.transX(rect.getCorner1().x),
                            Vis.transY(rect.getCorner2().y) - Vis.transY(rect.getCorner1().y));
                }

                canvas.setColor(edgeColor);
                canvas.drawRect(Vis.transX(rect.getCorner1().x), Vis.transY(rect.getCorner1().y),
                        Vis.transX(rect.getCorner2().x) - Vis.transX(rect.getCorner1().x),
                        Vis.transY(rect.getCorner2().y) - Vis.transY(rect.getCorner1().y));
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

                if (n == 1) {
                    canvas.setColor(edgeColor);
                    canvas.fillOval(x[0], y[0], 2, 2);

                } else {
                    if (!fill) {
                        canvas.setColor(fillColor);
                        canvas.fillPolygon(x, y, n);
                    }

                    canvas.setColor(edgeColor);
                    canvas.drawPolygon(x, y, n);
                }
            }
        }

    }

    public static VisLayer create(final RegionsProvider regionsProvider, final Color edgeColor, final Color fillColor) {
        return new RegionsLayer(regionsProvider, edgeColor, fillColor);
    }
}
