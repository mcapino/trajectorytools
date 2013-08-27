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

    private static final char SAVE_POLYGON = 32;
    private static final char REMOVE_POLYGON = 8;

    private PolygonCreator polygonCreator;

    public ProblemCreator() {
        this.polygonCreator = new PolygonCreator();
        initialize();
    }

    private void handleKey(KeyEvent e) {
        System.out.println((int) e.getKeyChar());

        switch (e.getKeyChar()) {
            case SAVE_POLYGON:
                polygonCreator.savePolygon();
                break;

            case REMOVE_POLYGON:
                polygonCreator.clearLast();
                break;
        }
    }

    private void handleMouse(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            addPointToPolygon();
        }
    }

    private void addPointToPolygon() {
        Point2d cursor = Vis.getCursorPosition();
        int x = (int) Vis.transInvX((int) cursor.x);
        int y = (int) Vis.transInvY((int) cursor.y);
        polygonCreator.addPoint(new Point(x, y));
    }

    private void initialize() {
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
                return polygonCreator.getPolygons();
            }
        }, Color.black, transparent(Color.gray, 128)));
        VisManager.registerLayer(RegionsLayer.create(new RegionsLayer.RegionsProvider() {
            @Override
            public Collection<? extends Region> getRegions() {
                return polygonCreator.getCurrent();
            }
        }, Color.black, transparent(Color.red, 128)));

        initializeListeners();

        VisManager.registerLayer(VisInfoLayer.create());
        VisManager.init();
    }

    private void initializeListeners() {
        Vis vis = Vis.getInstance();

        vis.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouse(e);
            }
        });
        vis.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                handleKey(e);
            }
        });
    }

    private static Color transparent(Color c, int a) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
    }

    public static void main(String[] args) {
        new ProblemCreator();
    }
}
