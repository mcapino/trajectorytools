package cz.agents.alite.trajectorytools.graph.spatial;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.graph.GraphPathImpl;

import cz.agents.alite.trajectorytools.planner.HeuristicFunction;
import cz.agents.alite.trajectorytools.util.SpatialPoint;

public class SpatialGraphs {
    public static <V extends SpatialPoint, E> V getNearestVertex(Graph<V, E> graph, SpatialPoint pos) {
        V nearestVertex = null;
        double nearestDistance = Double.POSITIVE_INFINITY;
        for (V currentVertex : graph.vertexSet()) {
            double distance = currentVertex.distance(pos);
            if (distance < nearestDistance || nearestVertex == null) {
                nearestVertex = currentVertex;
                nearestDistance = distance;
            }
        }

        return nearestVertex;
    }
    
    @SuppressWarnings("unchecked")
	public static <V extends SpatialPoint, E> V getRandomVertex(Graph<V, E> graph, Random random) {
    	int n = random.nextInt(graph.vertexSet().size());
    	return (V) graph.vertexSet().toArray()[n];
    }
    
    public static <V extends SpatialPoint, E> GraphPath<V,E> greedySearch(Graph<V, E> graph, V start, V target, HeuristicFunction<V> h, double maxCost) { 
    	List<E> edges = new LinkedList<E>();
    	
    	V current = start;
    	double cost = 0.0;
    	
    	while (!current.equals(target) && cost < maxCost) {
    		Set<E> outgoingEdges = graph.edgesOf(target);
    		
    		E bestEdge = null;
    		// TODO: find an edge that leads to a vertex having the highest heuristic value
    		
    		for (E edge : outgoingEdges) {
    			Graphs.getOppositeVertex(graph, edge, current);
    		}
    	}
    	
    	return new GraphPathImpl(graph, start, end, edges, cost);
    }

    public static <V, E> Graph<V, E>  clone(Graph<V, E> other) {
    	SimpleGraphWithObstacles<V, E> graph = new SimpleGraphWithObstacles<V,E>(other.getEdgeFactory());

    	for (V vertex : other.vertexSet()) {
    		graph.addVertex(vertex);
    	}

    	for (E edge : other.edgeSet()) {
    		graph.addEdge(other.getEdgeSource(edge), other.getEdgeTarget(edge), edge);
    	}

    	if (other instanceof GraphWithObstacles) {
    		graph.addObstacles(((GraphWithObstacles<V, ?>) other).getObstacles() );
    	} 
    	return graph;
    }
}

class SimpleGraphWithObstacles<V, E> extends DirectedWeightedMultigraph<V, E> implements GraphWithObstacles<V, E> {
	private static final long serialVersionUID = 1L;

	Set<V> obstacles = new HashSet<V>();
	
	public SimpleGraphWithObstacles(EdgeFactory<V, E> edgeFactory) {
		super(edgeFactory);
	}
	
	@Override
	public Set<V> getObstacles() {
		return obstacles;
	}

	@Override
	public void addObstacle(V obstacle) {
		obstacles.add(obstacle);
	}

	public void addObstacles(Collection<V> obstacle) {
		obstacles.addAll(obstacle);
	}

	@Override
	public void refresh() {
		throw new UnsupportedOperationException("Not implemented");
	}
	
}
