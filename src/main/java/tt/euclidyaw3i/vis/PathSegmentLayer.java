package tt.euclidyaw3i.vis;

import cz.agents.alite.vis.Vis;
import cz.agents.alite.vis.layer.AbstractLayer;
import cz.agents.alite.vis.layer.VisLayer;
import tt.euclidyaw3i.Point;
import tt.euclidyaw3i.discretization.PathSegment;

import java.awt.*;
import java.util.Collection;

public class PathSegmentLayer extends AbstractLayer {
	
	public interface PathSegmentsProvider {
		Collection<PathSegment> getPathSegements();
	}

	public static VisLayer create(final PathSegmentsProvider psProvider, final Color color) {

		return new AbstractLayer() {

			@Override
			public void paint(Graphics2D canvas) {
				canvas.setColor(color);
				canvas.setStroke(new BasicStroke(1));
				for (PathSegment segment : psProvider.getPathSegements()) {
					Point[] waypoints = segment.getWaypoints();
					for (int i = 0; i < waypoints.length-1; i++) {
						canvas.drawLine(
								Vis.transX(waypoints[i].getPos().x),
								Vis.transY(waypoints[i].getPos().y),
								Vis.transX(waypoints[i+1].getPos().x),
								Vis.transY(waypoints[i+1].getPos().y));
					}
				}

			}

		};

	}

}
