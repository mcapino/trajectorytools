package cz.agents.alite.trajectorytools.planner.rrtstar;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;

import cz.agents.alite.trajectorytools.util.NotImplementedException;
import cz.agents.alite.trajectorytools.util.Point;
import cz.agents.alite.trajectorytools.util.TimePoint;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.Bounds;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.maneuvers.*;
import cz.agents.alite.trajectorytools.graph.spatiotemporal.region.Region;

public class RRTStarPlanner<P,E> {
	
	Domain<P,E> domain;
	
	int nSamples;
	Vertex<P> root;
	double gamma;
	
	Vertex<P> bestVertex;
	
	public RRTStarPlanner(Domain<P,E> domain, P initialPoint, double gamma) {
		super();
		this.domain = domain;
		this.gamma = gamma;
		this.root = new Vertex<P>(initialPoint);
		this.nSamples = 1;
		this.bestVertex = null;
	}

	
	public void iterate() {
		// 1. Sample a new state
		P randomSample = domain.getRandomSample();
		
		// 2. Compute the set of all near vertices
		Collection<Vertex<P>> nearVertices = getNear(randomSample, nSamples);
		
		// 3. Find the best parent and extend from that parent
		BestParentSearchResult result = null;
		if(nearVertices.isEmpty()) {
			// 3.a Extend the nearest
			Vertex<P> parent = getNearestVertex(randomSample);
			Extension<P, E> extension = domain.extendTo(parent.getPoint(), randomSample);
			if (extension != null) {
				result = new BestParentSearchResult(parent, extension);
			}
		} else {
			// 3.b Extend the best parent within the near vertices
			result = findBestParent(randomSample, nearVertices);
		}
		
		if (result != null) {
			// 3.c add the trajectory from the best parent to the tree
			Vertex<P> newVertex = insertExtension(result);
			
			// 4. rewire the tree
			rewire(newVertex, nearVertices);
		}
		
	}
		
		
	private Vertex<P> insertExtension(BestParentSearchResult bestParentExtension) {
		Vertex<P> newVertex = new Vertex<P>(bestParentExtension.extension.target);		
		bestParentExtension.parent.addChild(newVertex);
		newVertex.setParent(bestParentExtension.parent);
		newVertex.setCostToParent(bestParentExtension.extension.cost);
		newVertex.setCostToRoot(bestParentExtension.parent.getCostToRoot() + bestParentExtension.extension.cost);
		nSamples++;
		return newVertex;
	}

	class BestParentSearchResult{
		final Vertex<P> parent;
		final Extension<P, E> extension;
		public BestParentSearchResult(Vertex<P> parent, Extension<P, E> extension) {
			this.parent = parent;
			this.extension = extension;
		}		
	}

	private BestParentSearchResult findBestParent(P randomSample, Collection<Vertex<P>> nearVertices) {
		
		class VertexCost implements Comparable<VertexCost> {
			Vertex<P> vertex;
			double cost;
			
			public VertexCost(Vertex<P> vertex, double cost) {
				super();
				this.vertex = vertex;
				this.cost = cost;
			}

			@Override
			public int compareTo(VertexCost other) {
				// Smallest cost will be first
				return (int) Math.abs(this.cost - other.cost);
			}		
		}
		
		// Sort according to the cost of nearby vertices
		List<VertexCost> vertexCosts = new LinkedList<VertexCost>();		
		
		for (Vertex<P> vertex : nearVertices) {
			vertexCosts.add(new VertexCost(vertex, 
					vertex.getCostToRoot() + domain.evaluateExtensionCost(vertex.getPoint(), randomSample)));
		}
		
		// Sort according to the vertex costs
		Collections.sort(vertexCosts);
		
		// Try to establish an edge to vertices in increasing order of costs
		for (VertexCost vertexCost : vertexCosts) {
			Vertex<P> vertex = vertexCost.vertex;
			Extension<P, E> extension = domain.extendTo(vertex.getPoint(), randomSample);
			if (extension != null) {
				return new BestParentSearchResult(vertex, extension);
			}
		}
		
		return null;
	}


	
	private void rewire(Vertex<P> candidateParent, Collection<Vertex<P>> vertices) {
		for (Vertex<P> nearVertex : vertices) {
			if (nearVertex != candidateParent) {
				if (domain.isVisible(candidateParent.getPoint(), nearVertex.getPoint())) {
					double costToNew = domain.evaluateExtensionCost(candidateParent.getPoint(), nearVertex.getPoint());
					double costToRootOverNew = candidateParent.getCostToRoot() + costToNew;
					if (costToRootOverNew < nearVertex.getCostToRoot()) {
						// rewire
						Vertex<P> oldParent = nearVertex.getParent();
						oldParent.removeChild(nearVertex);
						candidateParent.addChild(nearVertex);
						nearVertex.setParent(candidateParent);
						nearVertex.setCostToParent(costToNew);
						nearVertex.setCostToRoot(costToRootOverNew);
						updateBranchCost(nearVertex);						
					}
				}
			}
		}
	}
	
	
	
	private void updateBranchCost(Vertex<P> vertex) {
		checkBestVertex(vertex);
		for (Vertex<P> child : vertex.getChildren()) {
			child.setCostToRoot(vertex.getCostToRoot() + child.getCostToParent());
			updateBranchCost(child);
		}		
	}
	
	private void checkBestVertex(Vertex<P> vertex) {
		if (domain.isInTargetRegion(vertex.getPoint())) {
			if (bestVertex == null || vertex.costToRoot < bestVertex.costToRoot) {
				bestVertex = vertex;
			} 
		}
	}


	private Collection<Vertex<P>> getNear(P x, int n) {
		double radius = gamma * Math.pow(Math.log(n+1)/(n+1),1/domain.nDimensions());
		return dfsNearSearch(x, radius);
	}



	private Vertex<P>  getNearestVertex(P x) {
		return dfsNearestSearch(x);
	}

    Collection<Vertex<P>> dfsNearSearch(P center, double radius) {
		Queue<Vertex<P>> queue = new LinkedList<Vertex<P>>();
		LinkedList<Vertex<P>> result = new LinkedList<Vertex<P>>();
		queue.add(root);
		
		while(!queue.isEmpty()) {
			Vertex<P> current = queue.poll();
			if (domain.distance(center, current.getPoint()) <= radius) {
				result.add(current);
			}
			
			for (Vertex<P> child : current.getChildren()) {
				queue.offer(child);
			}			
		}
		
		return result;
	}
	
	Vertex<P> dfsNearestSearch(P center) {
		Queue<Vertex<P>> queue = new LinkedList<Vertex<P>>();
		Vertex<P> minDistVertex = null;
		double minDist = Double.POSITIVE_INFINITY;
		
		queue.add(root);
		
		while(!queue.isEmpty()) {
			Vertex<P> current = queue.poll();
			double distance = domain.distance(center, current.getPoint());
			if (distance <= minDist) {
				minDistVertex = current;
				minDist = distance;
			}
			
			for (Vertex<P> child : current.getChildren()) {
				queue.offer(child);
			}			
		}
		
		return minDistVertex;
	}
	
	public Vertex<P> getRoot() {
		return root;
	}
	
	
}
