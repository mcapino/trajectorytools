package tt.vis.problemcreator;

import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;

import javax.vecmath.Point2d;
import java.awt.*;

public class ProblemCreator {

    public ProblemCreator() {
        initialize();
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
        VisManager.registerLayer(ProblemCreatorLayer.create());

        VisManager.registerLayer(VisInfoLayer.create());
        VisManager.init();
    }

    public static void main(String[] args) {
        new ProblemCreator();
    }
}
