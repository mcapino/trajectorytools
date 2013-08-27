package tt.vis.problemcreator;

import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;
import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.vis.RegionsLayer;

import javax.vecmath.Point2d;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;

public class ProblemCreator {

    private PolygonCreator creator;

    public ProblemCreator() {
        preinit();
        this.creator = new PolygonCreator();
        listenersInit();
        afterInit();
    }

    private void preinit() {
        VisManager.setInitParam("Problem creator", 1024, 768, 200, 200);
        VisManager.setSceneParam(new VisManager.SceneParams() {

            @Override
            public Point2d getDefaultLookAt() {
                return new Point2d(500, 500);
            }

            @Override
            public double getDefaultZoomFactor() {
                return 0.5;
            }
        });
        VisManager.registerLayer(ColorLayer.create(Color.WHITE));
        VisManager.registerLayer(RegionsLayer.create(new RegionsLayer.RegionsProvider() {
            @Override
            public Collection<? extends Region> getRegions() {
                return creator.getPolygons();
            }
        }, Color.black, Color.gray));
        VisManager.registerLayer(RegionsLayer.create(new RegionsLayer.RegionsProvider() {
            @Override
            public Collection<? extends Region> getRegions() {
                return creator.getCurrent();
            }
        }, Color.black, Color.red));
    }

    private void listenersInit() {
        Vis vis = Vis.getInstance();

        vis.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    Point2d cursor = Vis.getCursorPosition();

                    int x = (int) Vis.transInvX((int) cursor.x);
                    int y = (int) Vis.transInvY((int) cursor.y);

                    creator.addPoint(new Point(x, y));
                }
            }
        });
        vis.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == ' ') {
                    creator.savePolygon();
                } else {
                    System.out.println(e.getKeyChar());
                }
            }
        });
    }

    private void afterInit() {
        VisManager.registerLayer(VisInfoLayer.create());
        VisManager.init();
    }

    public static void main(String[] args) {
        new ProblemCreator();
    }
}
