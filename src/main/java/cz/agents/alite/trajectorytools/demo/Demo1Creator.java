package cz.agents.alite.trajectorytools.demo;

import java.awt.Color;
import java.awt.Rectangle;

import cz.agents.alite.creator.Creator;
import cz.agents.alite.trajectorytools.graph.maneuver.FourWayConstantSpeedGridGraph;
import cz.agents.alite.trajectorytools.graph.maneuver.ManeuverGraph;
import cz.agents.alite.trajectorytools.vis.GraphLayer;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;


public class Demo1Creator implements Creator {

        private ManeuverGraph graph;

        @Override
        public void init(String[] args) {
        }

        @Override
        public void create() {
            graph = new FourWayConstantSpeedGridGraph(10, 10, 10, 10, 1.0);

            createVisualization();
        }

        private void createVisualization() {
            VisManager.setInitParam("Trajectory Tools Vis", 1024, 768, 20, 20);
            VisManager.setPanningBounds(new Rectangle(-500, -500, 1600, 1600));
            VisManager.init();

            // background
            VisManager.registerLayer(ColorLayer.create(Color.WHITE));

            // static
            VisManager.registerLayer(GraphLayer.create(graph, new Color(220, 220, 220), new Color(240, 240, 240), 1, 4));

            // Overlay
            VisManager.registerLayer(VisInfoLayer.create());
        }



}
