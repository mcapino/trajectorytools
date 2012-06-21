package cz.agents.alite.trajectorytools.demo;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.math.plot.Plot3DPanel;

import cz.agents.alite.creator.Creator;
import cz.agents.alite.simulation.ConcurrentProcessSimulation;
import cz.agents.alite.simulation.Simulation;
import cz.agents.alite.trajectorytools.graph.maneuver.EightWayConstantSpeedGridGraph;
import cz.agents.alite.trajectorytools.graph.maneuver.ManeuverGraph;
import cz.agents.alite.vis.VisManager;
import cz.agents.alite.vis.layer.common.ColorLayer;
import cz.agents.alite.vis.layer.common.VisInfoLayer;
import cz.agents.deconfliction.agent.Agent;
import cz.agents.deconfliction.configuration.Parameters;
import cz.agents.deconfliction.environment.SimpleAirplaneEnvironment;
import cz.agents.deconfliction.problem.DeconflictionProblem;
import cz.agents.deconfliction.trajectory.TrajectoryApproximation;
import cz.agents.deconfliction.util.OrientedPoint;
import cz.agents.deconfliction.vis.AgentMissionLayer;
import cz.agents.deconfliction.vis.AgentTrajectoryLayer;
import cz.agents.deconfliction.vis.AirplaneLayer;
import cz.agents.deconfliction.vis.ConflictsLayer;
import cz.agents.deconfliction.vis.CoordinateAxesLayer;
import cz.agents.deconfliction.vis.GraphLayer;
import cz.agents.deconfliction.vis.NodeIdLayer;
import cz.agents.deconfliction.vis.SimulationControlLayer;

public abstract class DeconflictionCreator implements Creator {

        private Graph graph;

        @Override
        public void init(String[] args) {
        }

        @Override
        public void create() {


            graph = generateManeuverGraph();

            createVisualization();
        }

        protected ManeuverGraph generateManeuverGraph() {
            return new EightWayConstantSpeedGridGraph(params.MAX_X, params.MAX_Y, params.GRID_X, params.GRID_Y, params.SPEED);
        }

        private void createVisualization() {
            VisManager.setInitParam("Deconfliction", 1024, 768, params.MAX_X*2, params.MAX_Y*2);
            VisManager.setPanningBounds(new Rectangle(-500, -500, 1600, 1600));
            VisManager.init();

            // background
            VisManager.registerLayer(ColorLayer.create(Color.WHITE));

            // static
            VisManager.registerLayer(GraphLayer.create(getManeuvers(), new Color(220, 220, 220), new Color(240, 240, 240), 1, 4));

            // dynamic
            VisManager.registerLayer(AgentTrajectoryLayer.create(problem, params.SPEED/10, "t"));

            // dynamic
            VisManager.registerLayer(AgentMissionLayer.create(problem, params.SPEED/10, "m"));

            // dynamic
            //VisManager.registerLayer(AgentSeparationLayer.create(problem));

            // dynamic
            VisManager.registerLayer(ConflictsLayer.create(problem));

            VisManager.registerLayer(NodeIdLayer.create(getManeuvers(), Color.GRAY, 1, "n"));

            VisManager.registerLayer(CoordinateAxesLayer.create(Color.BLACK, 2, "c"));

            VisManager.registerLayer(AirplaneLayer.create(environment.getAirplaneStorage(), params.SEPARATION, params.SPEED/5));


            //VisManager.registerLayer(BubbleLayer.create());
            // Overlay
            VisManager.registerLayer(VisInfoLayer.create());

            VisManager.registerLayer(SimulationControlLayer.create(simulation,environment));

        }



}
