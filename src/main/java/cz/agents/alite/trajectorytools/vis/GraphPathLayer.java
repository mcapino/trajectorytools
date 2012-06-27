package cz.agents.alite.trajectorytools.vis;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import cz.agents.alite.trajectorytools.planner.PlannedPath;
import cz.agents.alite.trajectorytools.util.Point;
import cz.agents.alite.vis.element.Line;
import cz.agents.alite.vis.element.StyledLine;
import cz.agents.alite.vis.element.StyledPoint;
import cz.agents.alite.vis.element.aggregation.LineElements;
import cz.agents.alite.vis.element.aggregation.PointElements;
import cz.agents.alite.vis.element.aggregation.StyledLineElements;
import cz.agents.alite.vis.element.aggregation.StyledPointElements;
import cz.agents.alite.vis.element.implemetation.LineImpl;
import cz.agents.alite.vis.element.implemetation.StyledLineImpl;
import cz.agents.alite.vis.element.implemetation.StyledPointImpl;
import cz.agents.alite.vis.layer.AbstractLayer;
import cz.agents.alite.vis.layer.GroupLayer;
import cz.agents.alite.vis.layer.VisLayer;
import cz.agents.alite.vis.layer.terminal.LineLayer;
import cz.agents.alite.vis.layer.terminal.PointLayer;
import cz.agents.alite.vis.layer.terminal.StyledLineLayer;
import cz.agents.alite.vis.layer.terminal.StyledPointLayer;

public class GraphPathLayer extends AbstractLayer {

    private static final double PATH_OFFSET = 1/40.0;
    static private Color[] pathColors = new Color[] {Color.RED, Color.BLUE, Color.GREEN, Color.CYAN, Color.ORANGE, Color.PINK, Color.YELLOW};

    GraphPathLayer() {
    }

    public static <V extends Point,E> VisLayer create(final Graph<V, E> graph, final PathHolder<V, E> pathHolder, final Color edgeColor, final Color vertexColor,
            final int edgeStrokeWidth, final int vertexStrokeWidth) {
        GroupLayer group = GroupLayer.create();

        // edges
        group.addSubLayer(LineLayer.create(new LineElements() {

            @Override
            public Iterable<Line> getLines() {
                Collection<Line> lines = new ArrayList<Line>();
                GraphPath<V,E> path = pathHolder.plannedPath;
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
                Collection<Point> points = new ArrayList<Point>();
                GraphPath<V,E> path = pathHolder.plannedPath;
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

    public static <V extends Point,E> VisLayer create(final Graph<V, E> graph, final Iterable<PlannedPath<V, E>> paths,
            final int edgeStrokeWidth, final int vertexStrokeWidth) {
        GroupLayer group = GroupLayer.create();

        // edges
        group.addSubLayer(StyledLineLayer.create(new StyledLineElements() {

            @Override
            public Iterable<StyledLine> getLines() {
                Collection<StyledLine> lines = new ArrayList<StyledLine>();
                int curPath = 0;
                for (PlannedPath<V, E> path : paths) {
                    Color color = pathColors[ curPath++ % pathColors.length];
                    Vector3d transition = new Vector3d(curPath * PATH_OFFSET,curPath * PATH_OFFSET, 0);
                    for (E edge : path.getEdgeList()) {
                        Point3d source = new Point3d( graph.getEdgeSource(edge) );
                        source.add(transition);
                        Point3d target = new Point3d( graph.getEdgeTarget(edge) );
                        target.add(transition);
                        lines.add(new StyledLineImpl(
                                source, 
                                target,
                                color,
                                edgeStrokeWidth));
                    }
                }
                return lines;
            }
        }));

        // vertices
        group.addSubLayer(StyledPointLayer.create(new StyledPointElements() {

            @Override
            public Iterable<StyledPoint> getPoints() {
                Collection<StyledPoint> points = new ArrayList<StyledPoint>();
                int curPath = 0;
                for (PlannedPath<V, E> path : paths) {
                    Color color = pathColors[ curPath++ % pathColors.length];
                    Vector3d transition = new Vector3d(curPath * PATH_OFFSET,curPath * PATH_OFFSET, 0);
                    for (E edge : path.getEdgeList()) {
                        Point3d source = new Point3d( graph.getEdgeSource(edge) );
                        source.add(transition);
                        points.add(new StyledPointImpl(source, color, vertexStrokeWidth));
                    }
                    points.add(new StyledPointImpl(path.getEndVertex(), color, vertexStrokeWidth));
                }
                return points;
            }
        }));

        return group;
    }
}
