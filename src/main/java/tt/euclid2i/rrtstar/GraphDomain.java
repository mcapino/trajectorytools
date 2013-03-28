package tt.euclid2i.rrtstar;

import java.util.Collection;
import java.util.Set;

import org.jgrapht.DirectedGraph;

import tt.euclid2i.Line;
import tt.euclid2i.Point;
import tt.euclid2i.region.Rectangle;
import tt.euclid2i.region.Region;
import tt.planner.rrtstar.Extension;

public class GraphDomain extends StraightLineDomain {

    DirectedGraph<Point, Line> graph;

    public GraphDomain(Rectangle bounds, DirectedGraph<Point, Line> graph,
            Collection<Region> obstacles,
            Region target, Point targetPoint,
            double tryGoalRatio) {
        super(bounds, obstacles, target, targetPoint, tryGoalRatio);
        this.graph = graph;
    }

    @Override
    public Extension<Point, Line> extendTo(
            Point from, Point to) {
        assert(graph.containsVertex(from));

        Set<Line> outEdges = graph.outgoingEdgesOf(from);

        Line bestEdge = null;
        double bestEdgeDistance = Double.POSITIVE_INFINITY;

        for (Line edge :  outEdges) {
            if (edge.getEnd().distance(to) < bestEdgeDistance) {
                bestEdgeDistance = edge.getEnd().distance(to);
                bestEdge = edge;
            }
        }

        return new Extension<Point, Line>(from, bestEdge.getEnd(), bestEdge, graph.getEdgeWeight(bestEdge), bestEdge.getEnd().equals(to));
    }



}
