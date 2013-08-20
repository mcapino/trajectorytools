package tt.planner.homotopy;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.Graphs;
import org.jgrapht.graph.AbstractDirectedGraphWrapper;
import org.jgrapht.util.Goal;
import org.jscience.mathematics.number.Complex;
import tt.planner.homotopy.hclass.HClass;
import tt.planner.homotopy.hclass.HClassProvider;
import tt.planner.homotopy.hvalue.HValueIntegrator;
import tt.planner.homotopy.hvalue.HValuePolicy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * This class implements an graph wrapper for usage as it is explained in paper "Topological constrains in search-based
 * robot path planning" (S. Bhattacharya). The wrapped graph can be easily searched for shortest trajectories in different
 * homotopy classes simply using any graph path planing algorithm.
 */

public class HomotopyGraphWrapper<V, E> extends AbstractDirectedGraphWrapper<HNode<V>, HEdge<V, E>> {

    private DirectedGraph<V, E> graph;
    private Goal<V> goal;
    private ProjectionToComplexPlane<V> projection;

    private HValueIntegrator integrator;
    private HClassProvider<V> provider;
    private HValuePolicy policy;
    private double precision;

    private HashMap<E, Complex> lValues;

    public HomotopyGraphWrapper(DirectedGraph<V, E> graph, Goal<V> goal, ProjectionToComplexPlane<V> projection,
                                HValueIntegrator integrator, HClassProvider<V> provider, double precision) {
        this.graph = graph;
        this.goal = goal;
        this.projection = projection;
        this.integrator = integrator;
        this.lValues = new HashMap<E, Complex>();
        this.provider = provider;
        this.precision = precision;
        this.policy = new HValuePolicy() {
            @Override
            public boolean isAllowed(Complex hValue, double precision) {
                return true;
            }
        };
    }

    public HValuePolicy getPolicy() {
        return policy;
    }

    public void setPolicy(HValuePolicy policy) {
        this.policy = policy;
    }

    public HNode<V> wrapNode(V node, Complex hValue) {
        HClass hClass = provider.assignHClass(node, hValue, precision);
        return new HNode<V>(node, hValue, hClass);
    }

    @Override
    public Set<HEdge<V, E>> edgesOf(HNode<V> vertex) {
        Set<HEdge<V, E>> edges = new HashSet<HEdge<V, E>>();
        edges.addAll(incomingEdgesOf(vertex));
        edges.addAll(outgoingEdgesOf(vertex));
        return edges;
    }

    @Override
    public Set<HEdge<V, E>> incomingEdgesOf(HNode<V> target) {
        Set<HEdge<V, E>> incomingEdges = new HashSet<HEdge<V, E>>();
        Set<E> originalEdges = graph.incomingEdgesOf(target.getNode());

        V vertex = target.getNode();
        Complex hValue = target.getHValue();

        for (E edge : originalEdges) {
            V opposite = Graphs.getOppositeVertex(graph, edge, vertex);

            Complex increment = lValueIncrement(edge, opposite, vertex);
            Complex oppositeLValue = hValue.plus(increment);

            if (goal.isGoal(opposite) && !policy.isAllowed(oppositeLValue, precision)) continue;
            HNode<V> source = wrapNode(opposite, oppositeLValue);

            incomingEdges.add(new HEdge<V, E>(edge, source, target));
        }

        return incomingEdges;
    }

    @Override
    public Set<HEdge<V, E>> outgoingEdgesOf(HNode<V> source) {
        Set<HEdge<V, E>> outgoingEdges = new HashSet<HEdge<V, E>>();
        Set<E> originalEdges = graph.outgoingEdgesOf(source.getNode());

        V vertex = source.getNode();
        Complex lValue = source.getHValue();

        for (E edge : originalEdges) {
            V opposite = Graphs.getOppositeVertex(graph, edge, vertex);

            Complex increment = lValueIncrement(edge, vertex, opposite);
            Complex oppositeLValue = lValue.plus(increment);

            if (goal.isGoal(opposite) && !policy.isAllowed(oppositeLValue, precision)) continue;
            HNode<V> target = wrapNode(opposite, oppositeLValue);

            outgoingEdges.add(new HEdge<V, E>(edge, source, target));
        }

        return outgoingEdges;
    }

    private Complex lValueIncrement(E edge, V source, V target) {
        Complex lvalue = lValues.get(edge);

        if (lvalue == null) {
            Complex from = projection.complexValue(source);
            Complex to = projection.complexValue(target);

            lvalue = integrator.lineSegmentIncrement(from, to);
            lValues.put(edge, lvalue);
        }

        return lvalue;
    }

    @Override
    public HNode<V> getEdgeSource(HEdge<V, E> hEdge) {
        return hEdge.getSource();
    }

    @Override
    public HNode<V> getEdgeTarget(HEdge<V, E> hEdge) {
        return hEdge.getTarget();
    }

    @Override
    public EdgeFactory<HNode<V>, HEdge<V, E>> getEdgeFactory() {
        return null;
    }

    @Override
    public double getEdgeWeight(HEdge<V, E> hEdge) {
        return graph.getEdgeWeight(hEdge.getEdge());
    }

    @Override
    public int inDegreeOf(HNode<V> vertex) {
        return graph.inDegreeOf(vertex.getNode());
    }

    @Override
    public int outDegreeOf(HNode<V> vertex) {
        return graph.outDegreeOf(vertex.getNode());
    }
}
