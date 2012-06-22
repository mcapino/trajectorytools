package cz.agents.alite.trajectorytools.vis;

import java.awt.Color;
import java.util.LinkedList;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

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

public class GraphPathLayer extends AbstractLayer {

    GraphPathLayer() {
    }

    public static <V extends Point,E> VisLayer create(final Graph<V, E> graph, final PathHolder<V, E> pathHolder, final Color edgeColor, final Color vertexColor,
            final int edgeStrokeWidth, final int vertexStrokeWidth) {
        GroupLayer group = GroupLayer.create();

        // edges
        group.addSubLayer(LineLayer.create(new LineElements() {

            @Override
            public Iterable<Line> getLines() {
                LinkedList<Line> lines = new LinkedList<Line>();
                GraphPath<V,E> path = pathHolder.graphPath;
                if (path != null) {
	                for (E edge : path.getEdgeList()) {
	                    lines.add(new LineImpl(graph.getEdgeSource(edge), graph.getEdgeTarget(edge)));
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

        // vertices
        group.addSubLayer(PointLayer.create(new PointElements() {

            @Override
            public Iterable<Point> getPoints() {
                LinkedList<Point> points = new LinkedList<Point>();
                GraphPath<V,E> path = pathHolder.graphPath;
                if (path != null) {
	                for (E edge : path.getEdgeList()) {
	                    points.add(graph.getEdgeSource(edge));
	                }
	                points.add(path.getEndVertex());
                }
                return points;
            }

            @Override
            public int getStrokeWidth() {
                return vertexStrokeWidth;
            }

            @Override
            public Color getColor() {
                return vertexColor;
            }

        }));

        return group;
    }

}
