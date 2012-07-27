package cz.agents.alite.trajectorytools.vis;

import java.awt.Color;
import java.util.Collection;
import java.util.LinkedList;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.jgrapht.Graph;

import cz.agents.alite.trajectorytools.graph.spatial.region.BoxRegion;
import cz.agents.alite.trajectorytools.graph.spatial.region.Region;
import cz.agents.alite.trajectorytools.trajectory.Trajectory;
import cz.agents.alite.trajectorytools.util.Point;
import cz.agents.alite.vis.element.Line;
import cz.agents.alite.vis.element.aggregation.LineElements;
import cz.agents.alite.vis.element.aggregation.PointElements;
import cz.agents.alite.vis.element.implemetation.LineImpl;
import cz.agents.alite.vis.layer.AbstractLayer;
import cz.agents.alite.vis.layer.GroupLayer;
import cz.agents.alite.vis.layer.VisLayer;
import cz.agents.alite.vis.layer.terminal.LineLayer;
import cz.agents.alite.vis.layer.terminal.PointLayer;

public class RegionsLayer extends AbstractLayer {

    public static interface RegionsProvider {
    	 Collection<Region> getRegions();
    }
	
    RegionsLayer() {
    }

    public static VisLayer create(final RegionsProvider regionsProvider, final Color edgeColor, final int edgeStrokeWidth) {
        GroupLayer group = GroupLayer.create();

        // edges
        group.addSubLayer(LineLayer.create(new LineElements() {

            @Override
            public Iterable<Line> getLines() {
            	Collection<Region> regions = regionsProvider.getRegions();
            	LinkedList<Line> lines = new LinkedList<Line>();
            	
            	for (Region region : regions) {
            		if (region instanceof BoxRegion) {
            			BoxRegion box = (BoxRegion) region;
            			
            			double x1 = box.getCorner1().x;
            			double y1 = box.getCorner1().y;
            			
            			double x2 = box.getCorner2().x;
            			double y2 = box.getCorner2().y;
            			
            			lines.add(new LineImpl(new Point(x1,y1,0), new Point(x1,y2,0)));
            			lines.add(new LineImpl(new Point(x2,y1,0), new Point(x2,y2,0)));
            			lines.add(new LineImpl(new Point(x1,y1,0), new Point(x2,y1,0)));
            			lines.add(new LineImpl(new Point(x1,y2,0), new Point(x2,y2,0)));           			
            		
            		}
            	}
            	
                return lines;
            }

            @Override
            public int getStrokeWidth() {
                return edgeStrokeWidth;
            }

            @Override
            public Color getColor() {
                return edgeColor;
            }

        }));

        return group;
    }
}
