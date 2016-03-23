package tt.euclid2i.vis;

import java.awt.Color;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Point2d;

import tt.euclid2i.Point;
import tt.euclid2i.Region;
import tt.euclid2i.region.Polygon;
import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.VisManager.SceneParams;
import cz.agents.alite.vis.layer.common.ColorLayer;

public class RegionsLayerTest {

    // Visual test -- uncomment the line below and you should see one outside filled polygon and one one inside filled polygon
    //@Test
    public void test() {

        VisManager.setInitParam("Trajectory Tools Vis", 1024, 768, 4000, 4000);
        VisManager.setSceneParam(new SceneParams() {

            @Override
            public Point2d getDefaultLookAt() {
                return new Point2d(500, 500);
            }

            @Override
            public double getDefaultZoomFactor() {
                return 0.4;
            }

        });

        VisManager.init();

        Vis.setPosition(50, 50, 1);

        final List<Polygon> regions = new LinkedList<Polygon>();
        regions.add(new Polygon(new Point[] {new Point(0,0), new Point(0,100), new Point(100,100),  new Point(100,0) }));
        regions.add(new Polygon(new Point[] {new Point(25,25), new Point(75,25), new Point(75,75),  new Point(25,75) }));

        // background
        VisManager.registerLayer(ColorLayer.create(Color.WHITE));
        VisManager.registerLayer(RegionsLayer.create(new RegionsLayer.RegionsProvider() {
            @Override
            public Collection<? extends Region> getRegions() {
                return regions;
            }
        }, Color.BLACK, Color.BLACK));

        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
