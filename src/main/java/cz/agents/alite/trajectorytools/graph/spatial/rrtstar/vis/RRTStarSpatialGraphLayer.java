package cz.agents.alite.trajectorytools.graph.spatial.rrtstar.vis;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;

import cz.agents.alite.trajectorytools.planner.rrtstar.RRTStarPlanner;
import cz.agents.alite.trajectorytools.planner.rrtstar.Vertex;
import cz.agents.alite.trajectorytools.util.SpatialPoint;
import cz.agents.alite.trajectorytools.vis.GraphPathLayer;
import cz.agents.alite.trajectorytools.vis.GraphPathLayer.PathProvider;
import cz.agents.alite.trajectorytools.vis.projection.ProjectionTo2d;
import cz.agents.alite.vis.element.Circle;
import cz.agents.alite.vis.element.Line;
import cz.agents.alite.vis.element.Point;
import cz.agents.alite.vis.element.aggregation.CircleElements;
import cz.agents.alite.vis.element.aggregation.LineElements;
import cz.agents.alite.vis.element.aggregation.PointElements;
import cz.agents.alite.vis.element.implemetation.CircleImpl;
import cz.agents.alite.vis.element.implemetation.PointImpl;
import cz.agents.alite.vis.layer.AbstractLayer;
import cz.agents.alite.vis.layer.GroupLayer;
import cz.agents.alite.vis.layer.VisLayer;
import cz.agents.alite.vis.layer.terminal.CircleLayer;
import cz.agents.alite.vis.layer.terminal.LineLayer;
import cz.agents.alite.vis.layer.terminal.PointLayer;

public class RRTStarSpatialGraphLayer extends AbstractLayer {


    RRTStarSpatialGraphLayer() {
    }

    public static <V extends SpatialPoint, E extends DefaultWeightedEdge> VisLayer create(final RRTStarPlanner<V, GraphPath<V,E>> rrtstar,
            final ProjectionTo2d<? super V> projection, final Color edgeColor,
            final Color vertexColor, final int edgeStrokeWidth,
            final int vertexStrokeWidth) {
        return create(rrtstar, projection, edgeColor, vertexColor, edgeStrokeWidth, vertexStrokeWidth, false);
    }

    public static <V extends SpatialPoint, E extends DefaultWeightedEdge> VisLayer create(final RRTStarPlanner<V, GraphPath<V,E>> rrtstar,
            final ProjectionTo2d<? super V> projection, final Color edgeColor,
            final Color vertexColor, final int edgeStrokeWidth,
            final int vertexStrokeWidth, final boolean visualizeSearch) {
        final GroupLayer group = GroupLayer.create();

        // edges
        group.addSubLayer(LineLayer.create(new LineElements() {

            @Override
            public Iterable<Line> getLines() {
                LinkedList<Line> lines = new LinkedList<Line>();

                Queue<Vertex<V, GraphPath<V, E>>> queue = new LinkedList<Vertex<V, GraphPath<V, E>>>();
                queue.add(rrtstar.getRoot());

                while(!queue.isEmpty()) {
                   Vertex<V, GraphPath<V, E>> current = queue.poll();
                   for (Vertex<V, GraphPath<V, E>> child : current.getChildren()) {
                        queue.offer(child);

                        final GraphPath<V, E> path = child.getEdgeFromParent();

                        group.addSubLayer(GraphPathLayer.create(new PathProvider<V, E>() {

                            @Override
                            public GraphPath<V,E> getPath() {
                                return path;
                            }

                        }, edgeColor, vertexColor, edgeStrokeWidth, vertexStrokeWidth));

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


                    Point2d point = projection.project(current.getState());
                    points.add(new PointImpl(new Point3d(point.x, point.y, 0)));

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


        // random sample
        group.addSubLayer(PointLayer.create(new PointElements() {

            @Override
            public Iterable<Point> getPoints() {
                LinkedList<Point> points = new LinkedList<Point>();

                if (!visualizeSearch) return points;

                V lastSample = rrtstar.getLastSample();

                if (lastSample != null) {
                    Point2d point = projection.project(lastSample);
                    points.add(new PointImpl(new Point3d(point.x, point.y, 0)));
                }
                return points;
            }

            @Override
            public int getStrokeWidth() {
                return 10;
            }

            @Override
            public Color getColor() {
                return Color.RED;
            }

        }));

        // new sample
        group.addSubLayer(PointLayer.create(new PointElements() {

            @Override
            public Iterable<Point> getPoints() {
                LinkedList<Point> points = new LinkedList<Point>();

                if (!visualizeSearch) return points;

                V newSample = rrtstar.getNewSample();

                if (newSample != null) {
                    Point2d point = projection.project(newSample);
                    points.add(new PointImpl(new Point3d(point.x, point.y, 0)));
                }
                return points;
            }

            @Override
            public int getStrokeWidth() {
                return 6;
            }

            @Override
            public Color getColor() {
                return Color.MAGENTA;
            }

        }));

        group.addSubLayer(CircleLayer.create(new CircleElements() {

            @Override
            public int getStrokeWidth() {
                return 1;
            }

            @Override
            public Color getColor() {
                return Color.MAGENTA;
            }

            @Override
            public Iterable<? extends Circle> getCircles() {
                ArrayList<Circle> circles = new ArrayList<Circle>();

                if (!visualizeSearch) return circles;

                V newSample = rrtstar.getNewSample();

                if (newSample != null) {
                    Point2d point = projection.project(newSample);
                    circles.add(new CircleImpl(new Point3d(point.x, point.y, 0), rrtstar.getNearBallRadius()));
                }

                return circles;
            }
        }));

        return group;
    }
}
