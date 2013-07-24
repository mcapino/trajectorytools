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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class HomotopyGraphWrapper<V, E> extends AbstractDirectedGraphWrapper<HNode<V>, HEdge<V, E>> {

    private final DirectedGraph<V, E> graph;
    private final HValueIntegrator integrator;
    private final ProjectionToComplexPlane<V> projection;
    private final Goal<V> goal;
    private final HClassProvider provider;
    private final double precision;

    private final HashMap<E, Complex> lValues;
    private final Set<Complex> forbiddenValues;

    public HomotopyGraphWrapper(DirectedGraph<V, E> graph, Goal<V> goal, ProjectionToComplexPlane<V> projection,
                                HValueIntegrator integrator, HClassProvider provider, double precision) {
        this.graph = graph;
        this.goal = goal;
        this.projection = projection;
        this.integrator = integrator;
        this.forbiddenValues = new HashSet<Complex>();
        this.lValues = new HashMap<E, Complex>();
        this.provider = provider;
        this.precision = precision;
    }

    public void forbidLValue(Complex lValue) {
        forbiddenValues.add(lValue);
    }

    public void forbidAllLValues(Collection<Complex> lValues) {
        forbiddenValues.addAll(lValues);
    }

    public HNode<V> wrapNode(V node, Complex hValue) {
        HClass hClass = provider.assignHClass(hValue, precision);
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

            if (goal.isGoal(opposite) && !isAllowed(oppositeLValue)) continue;
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

            if (goal.isGoal(opposite) && !isAllowed(oppositeLValue)) continue;
            HNode<V> target = wrapNode(opposite, oppositeLValue);

            outgoingEdges.add(new HEdge<V, E>(edge, source, target));
        }

        return outgoingEdges;
    }

    private boolean isAllowed(Complex oppositeLValue) {
        for (Complex value : forbiddenValues) {
            double diff = value.minus(oppositeLValue).magnitude();
            double abs = value.plus(oppositeLValue).divide(2).magnitude();

            if (diff / abs < precision) return false;
        }

        return true;
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
