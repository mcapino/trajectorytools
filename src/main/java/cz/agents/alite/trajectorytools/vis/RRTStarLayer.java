package cz.agents.alite.trajectorytools.vis;

import java.awt.Color;
import java.util.LinkedList;
import java.util.Queue;

import javax.vecmath.Point3d;

import cz.agents.alite.trajectorytools.planner.rrtstar.RRTStarPlanner;
import cz.agents.alite.trajectorytools.planner.rrtstar.Vertex;
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

public class RRTStarLayer extends AbstractLayer {


    RRTStarLayer() {
    }

    public static <V> VisLayer create(final RRTStarPlanner<V,?> rrtstar, final Color edgeColor, final Color vertexColor,
            final int edgeStrokeWidth, final int vertexStrokeWidth) {
        GroupLayer group = GroupLayer.create();

        // edges
        group.addSubLayer(LineLayer.create(new LineElements() {

            @Override
            public Iterable<Line> getLines() {
                LinkedList<Line> lines = new LinkedList<Line>();

                Queue<Vertex<V,?>> queue = new LinkedList<Vertex<V,?>>();
                queue.add(rrtstar.getRoot());

                while(!queue.isEmpty()) {
                    Vertex<V,?> current = queue.poll();
                   for (Vertex<V,?> child : current.getChildren()) {
                        queue.offer(child);

                        Point3d source =  null;
                        Point3d target = null;

                        if (current.getPoint() instanceof Point3d) {
                            source = (Point3d) current.getPoint();
                        }


                        if (child.getPoint() instanceof Point3d) {
                            target = (Point3d) child.getPoint();
                        }

                        // draw edge
                        if (source != null && target != null) {
                            lines.add(new LineImpl(source, target));
                        }

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

                Queue<Vertex<V,?>> queue = new LinkedList<Vertex<V,?>>();
                queue.add(rrtstar.getRoot());

                while(!queue.isEmpty()) {
                    Vertex<V,?> current = queue.poll();

                    if (current.getPoint() instanceof Point) {
                        points.add((Point) current.getPoint());
                    }

                       for (Vertex<V,?> child : current.getChildren()) {
                        queue.offer(child);
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
