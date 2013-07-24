package tt.planner.homotopy;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.Graphs;
import org.jgrapht.graph.AbstractDirectedGraphWrapper;
import org.jgrapht.util.Goal;
import org.jscience.mathematics.number.Complex;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class HomotopyGraphWrapper<V, E> extends AbstractDirectedGraphWrapper<HNode<V>, HEdge<V, E>> {

    private DirectedGraph<V, E> graph;
    private LValueIntegrator integrator;
    private ProjectionToComplexPlane<V> projection;
    private Goal<V> goal;

    private HashMap<E, Complex> lValues;
    private Set<Complex> forbiddenValues;

    public HomotopyGraphWrapper(DirectedGraph<V, E> graph, Goal<V> goal, ProjectionToComplexPlane<V> projection, LValueIntegrator integrator) {
        this.graph = graph;
        this.goal = goal;
        this.projection = projection;
        this.integrator = integrator;
        this.forbiddenValues = new HashSet<Complex>();
        this.lValues = new HashMap<E, Complex>();
    }

    public void forbidLValue(Complex lValue) {
        forbiddenValues.add(lValue);
    }

    public void forbidAllLValues(Collection<Complex> lValues) {
        forbiddenValues.addAll(lValues);
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
        Complex lValue = target.getlValue();

        for (E edge : originalEdges) {
            V opposite = Graphs.getOppositeVertex(graph, edge, vertex);

            Complex increment = lValueIncrement(edge, opposite, vertex);
            Complex oppositeLValue = lValue.plus(increment);

            if (goal.isGoal(opposite) && !isAllowed(oppositeLValue)) continue;

            HNode<V> source = new HNode<V>(opposite, oppositeLValue, target.getPrecision());

            incomingEdges.add(new HEdge<V, E>(edge, increment, source, target));
        }

        return incomingEdges;
    }

    @Override
    public Set<HEdge<V, E>> outgoingEdgesOf(HNode<V> source) {
        Set<HEdge<V, E>> outgoingEdges = new HashSet<HEdge<V, E>>();
        Set<E> originalEdges = graph.outgoingEdgesOf(source.getNode());

        V vertex = source.getNode();
        Complex lValue = source.getlValue();

        for (E edge : originalEdges) {
            V opposite = Graphs.getOppositeVertex(graph, edge, vertex);

            Complex increment = lValueIncrement(edge, vertex, opposite);
            Complex oppositeLValue = lValue.plus(increment);

            if (goal.isGoal(opposite) && !isAllowed(oppositeLValue)) continue;

            HNode<V> target = new HNode<V>(opposite, oppositeLValue, source.getPrecision());

            outgoingEdges.add(new HEdge<V, E>(edge, increment, source, target));
        }

        return outgoingEdges;
    }

    private boolean isAllowed(Complex oppositeLValue) {
        for (Complex value : forbiddenValues) {
            double diff = value.minus(oppositeLValue).magnitude();
            double abs = value.plus(oppositeLValue).divide(2).magnitude();
            //TODO shitty method better, to create a constant or parameter
            if (diff / abs < 0.01) return false;
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
