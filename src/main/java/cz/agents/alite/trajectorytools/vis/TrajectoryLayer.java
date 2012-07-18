package cz.agents.alite.trajectorytools.vis;

import java.awt.Color;
import java.util.ArrayList;

import cz.agents.alite.trajectorytools.trajectory.Trajectory;
import cz.agents.alite.trajectorytools.util.OrientedPoint;
import cz.agents.alite.vis.element.StyledLine;
import cz.agents.alite.vis.element.StyledPoint;
import cz.agents.alite.vis.element.aggregation.StyledLineElements;
import cz.agents.alite.vis.element.aggregation.StyledPointElements;
import cz.agents.alite.vis.element.implemetation.StyledPointImpl;
import cz.agents.alite.vis.layer.GroupLayer;
import cz.agents.alite.vis.layer.VisLayer;
import cz.agents.alite.vis.layer.common.CommonLayer;
import cz.agents.alite.vis.layer.terminal.StyledLineLayer;
import cz.agents.alite.vis.layer.terminal.StyledPointLayer;
import cz.agents.alite.vis.layer.toggle.KeyToggleLayer;

public class TrajectoryLayer extends CommonLayer {
	
	public static class TrajectoryHolder {
		public Trajectory trajectory;
	}
	
    public static Color getColorForAgent(int n) {
        return AgentColors.getColorForAgent(n);
    }

    public static VisLayer create(final TrajectoryHolder trajectoryHolder, final Color color, final double samplingInterval, final double maxTimeArg, final double pointTipLength, final char toggleKey) {
        GroupLayer group = GroupLayer.create();

        group.addSubLayer(StyledPointLayer.create(new StyledPointElements() {

            @Override
            public Iterable<? extends StyledPoint> getPoints() {
                ArrayList<StyledPoint> points = new ArrayList<StyledPoint>();
                Trajectory t = trajectoryHolder.trajectory;
                
                double maxTime = Math.min(t.getMaxTime(), maxTimeArg);

                points.add(new StyledPointImpl(t.getPosition(t.getMinTime()), color, 8));
                points.add(new StyledPointImpl(t.getPosition(t.getMaxTime()), color, 8));
                
                if (t != null) {
                    for (double time = t.getMinTime(); time < maxTime; time += samplingInterval) {
                        OrientedPoint pos = t.getPosition(time);
                    	if (pos != null) {
                            points.add(new StyledPointImpl(pos, color, 6));
                        }
                    }
                }

                return points;
            }

        }));

        group.addSubLayer(StyledLineLayer.create(new StyledLineElements() {

            @Override
            public Iterable<? extends StyledLine> getLines() {
                ArrayList<StyledLine> lines = new ArrayList<StyledLine>();
                return lines;
            }

        }));


        KeyToggleLayer toggle = KeyToggleLayer.create(toggleKey);
        toggle.addSubLayer(group);
        toggle.setEnabled(true);

        return toggle;

    }
}
