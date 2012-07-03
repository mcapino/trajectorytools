package cz.agents.alite.trajectorytools.vis;

import java.awt.Color;
import java.util.LinkedList;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.jgrapht.Graph;

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

public class GraphLayer extends AbstractLayer {

    GraphLayer() {
    }

    public static <V extends Point,E> VisLayer create(final Graph<V, E> graph, final Color edgeColor, final Color vertexColor,
            final int edgeStrokeWidth, final int vertexStrokeWidth) {
        return create(graph, edgeColor, vertexColor, edgeStrokeWidth, vertexStrokeWidth, 0.0);
    }
    public static <V extends Point,E> VisLayer create(final Graph<V, E> graph, final Color edgeColor, final Color vertexColor,
            final int edgeStrokeWidth, final int vertexStrokeWidth, final double offset) {
        GroupLayer group = GroupLayer.create();

        // edges
        group.addSubLayer(LineLayer.create(new LineElements() {

            @Override
            public Iterable<Line> getLines() {
                LinkedList<Line> lines = new LinkedList<Line>();
                Vector3d transition = new Vector3d(offset, offset, 0);
                for (E edge : graph.edgeSet()) {
                    Point3d source = new Point3d( graph.getEdgeSource(edge) );
                    source.add(transition);
                    Point3d target = new Point3d( graph.getEdgeTarget(edge) );
                    target.add(transition);
                    lines.add(new LineImpl(source, target));
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
                for (Point vertex : graph.vertexSet()) {
                    points.add(vertex);
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

    public static <V extends Point,E> VisLayer create(final GraphHolder<V, E> graphHolder, final Color edgeColor, final Color vertexColor,
            final int edgeStrokeWidth, final int vertexStrokeWidth) {
        return create(graphHolder, edgeColor, vertexColor, edgeStrokeWidth, vertexStrokeWidth, 0);
    }

    public static <V extends Point,E> VisLayer create(final GraphHolder<V, E> graphHolder, final Color edgeColor, final Color vertexColor,
            final int edgeStrokeWidth, final int vertexStrokeWidth, final double offset) {
        GroupLayer group = GroupLayer.create();

        // edges
        group.addSubLayer(LineLayer.create(new LineElements() {

            @Override
            public Iterable<Line> getLines() {
                LinkedList<Line> lines = new LinkedList<Line>();
                if (graphHolder.graph != null) {
                    Vector3d transition = new Vector3d(offset, offset, 0);
                    for (E edge : graphHolder.graph.edgeSet()) {
                        Point3d source = new Point3d( graphHolder.graph.getEdgeSource(edge) );
                        source.add(transition);
                        Point3d target = new Point3d( graphHolder.graph.getEdgeTarget(edge) );
                        target.add(transition);
                        lines.add(new LineImpl(source, target));
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
                if (graphHolder.graph != null) {
                    for (Point vertex : graphHolder.graph.vertexSet()) {
                        points.add(vertex);
                    }
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
