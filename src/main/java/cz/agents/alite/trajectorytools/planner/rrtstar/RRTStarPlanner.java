package cz.agents.alite.trajectorytools.planner.rrtstar;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.GraphPathImpl;

import cz.agents.alite.trajectorytools.util.NotImplementedException;

public class RRTStarPlanner<P,E> implements Graph<P,E> {

    Domain<P,E> domain;

    int nSamples;
    Vertex<P,E> root;
    double gamma;

    Vertex<P,E> bestVertex;

    Map<E,P> edgeSources = new HashMap<E,P>();
    Map<E,P> edgeTargets = new HashMap<E,P>();

    public RRTStarPlanner(Domain<P,E> domain, P initialPoint, double gamma) {
        super();
        this.domain = domain;
        this.gamma = gamma;
        this.root = new Vertex<P,E>(initialPoint);
        this.nSamples = 1;

        this.bestVertex = null;
    }


    public void iterate() {
        // 1. Sample a new state
        P randomSample = domain.sampleState();

        // 2. Compute the set of all near vertices
        Collection<Vertex<P,E>> nearVertices = getNear(randomSample, nSamples);

        // 3. Find the best parent and extend from that parent
        BestParentSearchResult result = null;
        if(nearVertices.isEmpty()) {
            // 3.a Extend the nearest
            Vertex<P,E> parent = getNearestVertex(randomSample);
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
            Vertex<P,E> newVertex = insertExtension(result.parent, result.extension);
            if (newVertex != null) {
                // 4. rewire the tree
                rewire(newVertex, nearVertices);
            }
        }

    }


    private Vertex<P,E> insertExtension(Vertex<P,E> parent, Extension<P, E> extension) {

        if (bestVertex != null) {
            if (bestVertex.getCostFromRoot() < parent.getCostFromRoot() + domain.estimateCostToGo(parent.getPoint())) {
                return null;
            }
        }

        Vertex<P,E> newVertex = new Vertex<P,E>(extension.target);
        nSamples++;

        insertExtension(parent, extension, newVertex);

        return newVertex;
    }

    private void insertExtension(Vertex<P,E> parent, Extension<P, E> extension, Vertex<P,E> target) {
        if (target.parent != null) {
            target.parent.removeChild(target);
        }

        parent.addChild(target);
        target.setParent(parent);
        target.setEdgeFromParent(extension.edge);

        target.setCostFromParent(extension.cost);
        target.setCostFromRoot(parent.getCostFromRoot() + extension.cost);

        checkBestVertex(target);

        edgeSources.put(extension.edge, parent.getPoint());
        edgeTargets.put(extension.edge, target.getPoint());
    }

    class BestParentSearchResult{
        final Vertex<P,E> parent;
        final Extension<P, E> extension;
        public BestParentSearchResult(Vertex<P,E> parent, Extension<P, E> extension) {
            this.parent = parent;
            this.extension = extension;
        }
    }

    private BestParentSearchResult findBestParent(P randomSample, Collection<Vertex<P,E>> nearVertices) {

        class VertexCost implements Comparable<VertexCost> {
            Vertex<P,E> vertex;
            double cost;

            public VertexCost(Vertex<P,E> vertex, double cost) {
                super();
                this.vertex = vertex;
                this.cost = cost;
            }

            @Override
            public int compareTo(VertexCost other) {
                // Smallest cost will be first
                return (int) Math.signum(this.cost - other.cost);
            }

            @Override
            public String toString() {
                return "VertexCost [vertex=" + vertex + ", cost=" + cost + "]";
            }
        }

        // Sort according to the cost of nearby vertices
        List<VertexCost> vertexCosts = new LinkedList<VertexCost>();

        for (Vertex<P,E> vertex : nearVertices) {
            vertexCosts.add(new VertexCost(vertex,
                    vertex.getCostFromRoot() + domain.estimateExtension(vertex.getPoint(), randomSample).cost));
        }

        // Sort according to the vertex costs
        Collections.sort(vertexCosts);

        // Try to establish an edge to vertices in increasing order of costs
        for (VertexCost vertexCost : vertexCosts) {
            Vertex<P,E> vertex = vertexCost.vertex;
            Extension<P, E> extension = domain.extendTo(vertex.getPoint(), randomSample);
            if (extension != null) {
                return new BestParentSearchResult(vertex, extension);
            }
        }

        return null;
    }



    private void rewire(Vertex<P,E> candidateParent, Collection<Vertex<P,E>> vertices) {
        for (Vertex<P,E> nearVertex : vertices) {
            if (nearVertex != candidateParent) {
                ExtensionEstimate<P, E> extensionEst = domain.estimateExtension(candidateParent.getPoint(), nearVertex.getPoint());
                double costToRootOverNew = candidateParent.getCostFromRoot() + extensionEst.cost;
                if (extensionEst.exact && costToRootOverNew < nearVertex.getCostFromRoot()) {
                    Extension<P, E> extension = domain.extendTo(candidateParent.getPoint(), nearVertex.getPoint());
                    if (extension != null)  {
                        insertExtension(candidateParent, extension, nearVertex);
                        updateBranchCost(nearVertex);
                    }
                }

            }
        }
    }

    private void updateBranchCost(Vertex<P,E> vertex) {
        checkBestVertex(vertex);
        for (Vertex<P,E> child : vertex.getChildren()) {
            child.setCostFromRoot(vertex.getCostFromRoot() + child.getCostFromParent());
            updateBranchCost(child);
        }
    }

    private void checkBestVertex(Vertex<P,E> vertex) {
        if (domain.isInTargetRegion(vertex.getPoint())) {
            if (bestVertex == null || vertex.costFromRoot < bestVertex.costFromRoot) {
                bestVertex = vertex;
            }
        }
    }


    private Collection<Vertex<P,E>> getNear(P x, int n) {
        double radius = gamma * Math.pow(Math.log(n+1)/(n+1),1/domain.nDimensions());
        return dfsNearSearch(x, radius);
    }



    private Vertex<P,E>  getNearestVertex(P x) {
        return dfsNearestSearch(x);
    }

    Collection<Vertex<P,E>> dfsNearSearch(P center, double radius) {
        Queue<Vertex<P,E>> queue = new LinkedList<Vertex<P,E>>();
        LinkedList<Vertex<P,E>> result = new LinkedList<Vertex<P,E>>();
        queue.add(root);

        while(!queue.isEmpty()) {
            Vertex<P,E> current = queue.poll();
            if (domain.distance(center, current.getPoint()) <= radius) {
                result.add(current);
            }

            for (Vertex<P,E> child : current.getChildren()) {
                queue.offer(child);
            }
        }

        return result;
    }

    Vertex<P,E> dfsNearestSearch(P center) {
        Queue<Vertex<P,E>> queue = new LinkedList<Vertex<P,E>>();
        Vertex<P,E> minDistVertex = null;
        double minDist = Double.POSITIVE_INFINITY;

        queue.add(root);

        while(!queue.isEmpty()) {
            Vertex<P,E> current = queue.poll();
            double distance = domain.distance(center, current.getPoint());
            if (distance <= minDist) {
                minDistVertex = current;
                minDist = distance;
            }

            for (Vertex<P,E> child : current.getChildren()) {
                queue.offer(child);
            }
        }

        return minDistVertex;
    }

    public Vertex<P,E> getRoot() {
        return root;
    }

    public GraphPath<P, E> getBestPath() {
        LinkedList<E> edges = new LinkedList<E>();

        P end;
        P start;

        if (bestVertex == null) {
            return null;
        } else {
            end = bestVertex.getPoint();
            Vertex<P,E> current = bestVertex;
            while (current.getParent() != null) {
                edges.addFirst(current.getEdgeFromParent());
                current = current.getParent();
            }
            start = current.getPoint();
        }

        return new GraphPathImpl<P, E>(this, start, end, edges, bestVertex.getCostFromRoot());

    }

    public Vertex<P, E> getBestVertex() {
        return bestVertex;
    }

    public boolean foundSolution() {
        return bestVertex != null;
    }


    @Override
    public E addEdge(P arg0, P arg1) {
        throw new NotImplementedException();
    }


    @Override
    public boolean addEdge(P arg0, P arg1, E arg2) {
        throw new NotImplementedException();
    }


    @Override
    public boolean addVertex(P arg0) {
        throw new NotImplementedException();
    }


    @Override
    public boolean containsEdge(E arg0) {
        throw new NotImplementedException();
    }


    @Override
    public boolean containsEdge(P arg0, P arg1) {
        throw new NotImplementedException();
    }


    @Override
    public boolean containsVertex(P arg0) {
        throw new NotImplementedException();
    }


    @Override
    public Set<E> edgeSet() {
        throw new NotImplementedException();
    }


    @Override
    public Set<E> edgesOf(P arg0) {
        throw new NotImplementedException();
    }


    @Override
    public Set<E> getAllEdges(P arg0, P arg1) {
        throw new NotImplementedException();
    }


    @Override
    public E getEdge(P arg0, P arg1) {
        throw new NotImplementedException();
    }


    @Override
    public EdgeFactory<P, E> getEdgeFactory() {
        throw new NotImplementedException();
    }


    @Override
    public P getEdgeSource(E edge) {
        return edgeSources.get(edge);
    }


    @Override
    public P getEdgeTarget(E edge) {
        return edgeTargets.get(edge);
    }


    @Override
    public double getEdgeWeight(E arg0) {
        throw new NotImplementedException();
    }


    @Override
    public boolean removeAllEdges(Collection<? extends E> arg0) {
         throw new NotImplementedException();
    }


    @Override
    public Set<E> removeAllEdges(P arg0, P arg1) {
         throw new NotImplementedException();
    }


    @Override
    public boolean removeAllVertices(Collection<? extends P> arg0) {
         throw new NotImplementedException();
    }


    @Override
    public boolean removeEdge(E arg0) {
         throw new NotImplementedException();
    }


    @Override
    public E removeEdge(P arg0, P arg1) {
         throw new NotImplementedException();
    }


    @Override
    public boolean removeVertex(P arg0) {
         throw new NotImplementedException();
    }


    @Override
    public Set<P> vertexSet() {
         throw new NotImplementedException();
    }

}
