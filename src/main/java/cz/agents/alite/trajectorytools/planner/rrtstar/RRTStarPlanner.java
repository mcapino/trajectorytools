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

import cz.agents.alite.trajectorytools.graph.spatiotemporal.maneuvers.Straight;
import cz.agents.alite.trajectorytools.util.NotImplementedException;
import cz.agents.alite.trajectorytools.util.TimePoint;

public class RRTStarPlanner<S,E> implements Graph<S,E> {

    Domain<S,E> domain;

    int nSamples;
    Vertex<S,E> root;
    double gamma;

    Vertex<S,E> bestVertex;

    Map<E,S> edgeSources = new HashMap<E,S>();
    Map<E,S> edgeTargets = new HashMap<E,S>();

    public RRTStarPlanner(Domain<S,E> domain, S initialState, double gamma) {
        super();
        this.domain = domain;
        this.gamma = gamma;
        this.root = new Vertex<S,E>(initialState);
        this.nSamples = 1;

        this.bestVertex = null;
    }


    public void iterate() {
        // 1. Sample a new state
        S randomSample = domain.sampleState();

        // 2. Compute the set of all near vertices
        Collection<Vertex<S,E>> nearVertices = getNear(randomSample, nSamples);

        // 3. Find the best parent and extend from that parent
        BestParentSearchResult result = null;
        if(nearVertices.isEmpty()) {
            // 3.a Extend the nearest
            Vertex<S,E> parent = getNearestVertex(randomSample);
            Extension<S, E> extension = domain.extendTo(parent.getState(), randomSample);
            if (extension != null) {
                result = new BestParentSearchResult(parent, extension);
            }
        } else {
            // 3.b Extend the best parent within the near vertices
            result = findBestParent(randomSample, nearVertices);
        }

        if (result != null) {
            // 3.c add the trajectory from the best parent to the tree
            Vertex<S,E> newVertex = insertExtension(result.parent, result.extension);
            if (newVertex != null) {
                // 4. rewire the tree
                rewire(newVertex, nearVertices);
            }
        }

    }


    private Vertex<S,E> insertExtension(Vertex<S,E> parent, Extension<S, E> extension) {

        if (bestVertex != null) {
            if (bestVertex.getCostFromRoot() < parent.getCostFromRoot() + domain.estimateCostToGo(parent.getState())) {
                return null;
            }
        }

        Vertex<S,E> newVertex = new Vertex<S,E>(extension.target);
        nSamples++;

        insertExtension(parent, extension, newVertex);

        return newVertex;
    }

    private void insertExtension(Vertex<S,E> parent, Extension<S, E> extension, Vertex<S,E> target) {
        if (target.parent != null) {
            target.parent.removeChild(target);
        }

        parent.addChild(target);
        target.setParent(parent);
        target.setEdgeFromParent(extension.edge);

        target.setCostFromParent(extension.cost);
        target.setCostFromRoot(parent.getCostFromRoot() + extension.cost);


        // DEBUG TEST

        if (extension.edge instanceof Straight) {
            Straight straight = (Straight) extension.edge;
            assert(straight.getStart().distance((TimePoint) parent.getState()) <= 0.001);
            assert(straight.getEnd().distance((TimePoint) target.getState()) <= 0.001);
        }

        ////////

        checkBestVertex(target);

        edgeSources.put(extension.edge, parent.getState());
        edgeTargets.put(extension.edge, target.getState());
    }

    class BestParentSearchResult{
        final Vertex<S,E> parent;
        final Extension<S, E> extension;
        public BestParentSearchResult(Vertex<S,E> parent, Extension<S, E> extension) {
            this.parent = parent;
            this.extension = extension;
        }
    }

    private BestParentSearchResult findBestParent(S randomSample, Collection<Vertex<S,E>> nearVertices) {

        class VertexCost implements Comparable<VertexCost> {
            Vertex<S,E> vertex;
            double cost;

            public VertexCost(Vertex<S,E> vertex, double cost) {
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

        for (Vertex<S,E> vertex : nearVertices) {
            vertexCosts.add(new VertexCost(vertex,
                    vertex.getCostFromRoot() + domain.estimateExtension(vertex.getState(), randomSample).cost));
        }

        // Sort according to the vertex costs
        Collections.sort(vertexCosts);

        // Try to establish an edge to vertices in increasing order of costs
        for (VertexCost vertexCost : vertexCosts) {
            Vertex<S,E> vertex = vertexCost.vertex;
            Extension<S, E> extension = domain.extendTo(vertex.getState(), randomSample);
            if (extension != null) {
                return new BestParentSearchResult(vertex, extension);
            }
        }

        return null;
    }



    private void rewire(Vertex<S,E> candidateParent, Collection<Vertex<S,E>> vertices) {
        for (Vertex<S,E> nearVertex : vertices) {
            if (nearVertex != candidateParent) {
                ExtensionEstimate extensionEst = domain.estimateExtension(candidateParent.getState(), nearVertex.getState());
                double costToRootOverNew = candidateParent.getCostFromRoot() + extensionEst.cost;
                if (extensionEst.exact && costToRootOverNew < nearVertex.getCostFromRoot()) {
                    Extension<S, E> extension = domain.extendTo(candidateParent.getState(), nearVertex.getState());
                    if (extension != null && extension.exact)  {
                        insertExtension(candidateParent, extension, nearVertex);
                        updateBranchCost(nearVertex);
                    }
                }

            }
        }
    }

    private void updateBranchCost(Vertex<S,E> vertex) {
        checkBestVertex(vertex);
        for (Vertex<S,E> child : vertex.getChildren()) {
            child.setCostFromRoot(vertex.getCostFromRoot() + child.getCostFromParent());
            updateBranchCost(child);
        }
    }

    private void checkBestVertex(Vertex<S,E> vertex) {
        if (domain.isInTargetRegion(vertex.getState())) {
            if (bestVertex == null || vertex.costFromRoot < bestVertex.costFromRoot) {
                bestVertex = vertex;
            }
        }
    }


    private Collection<Vertex<S,E>> getNear(S x, int n) {
        double radius = gamma * Math.pow(Math.log(n+1)/(n+1),1/domain.nDimensions());
        return dfsNearSearch(x, radius);
    }



    private Vertex<S,E>  getNearestVertex(S x) {
        return dfsNearestSearch(x);
    }

    Collection<Vertex<S,E>> dfsNearSearch(S center, double radius) {
        Queue<Vertex<S,E>> queue = new LinkedList<Vertex<S,E>>();
        LinkedList<Vertex<S,E>> result = new LinkedList<Vertex<S,E>>();
        queue.add(root);

        while(!queue.isEmpty()) {
            Vertex<S,E> current = queue.poll();
            if (domain.distance(center, current.getState()) <= radius) {
                result.add(current);
            }

            for (Vertex<S,E> child : current.getChildren()) {
                queue.offer(child);
            }
        }

        return result;
    }

    Vertex<S,E> dfsNearestSearch(S center) {
        Queue<Vertex<S,E>> queue = new LinkedList<Vertex<S,E>>();
        Vertex<S,E> minDistVertex = null;
        double minDist = Double.POSITIVE_INFINITY;

        queue.add(root);

        while(!queue.isEmpty()) {
            Vertex<S,E> current = queue.poll();
            double distance = domain.distance(center, current.getState());
            if (distance <= minDist) {
                minDistVertex = current;
                minDist = distance;
            }

            for (Vertex<S,E> child : current.getChildren()) {
                queue.offer(child);
            }
        }

        return minDistVertex;
    }

    public Vertex<S,E> getRoot() {
        return root;
    }

    public GraphPath<S, E> getBestPath() {
        LinkedList<E> edges = new LinkedList<E>();

        S end;
        S start;

        if (bestVertex == null) {
            return null;
        } else {
            end = bestVertex.getState();
            Vertex<S,E> current = bestVertex;
            while (current.getParent() != null) {
                edges.addFirst(current.getEdgeFromParent());
                current = current.getParent();
            }
            start = current.getState();
        }

        return new GraphPathImpl<S, E>(this, start, end, edges, bestVertex.getCostFromRoot());
    }

    public Vertex<S, E> getBestVertex() {
        return bestVertex;
    }

    public boolean foundSolution() {
        return bestVertex != null;
    }


    @Override
    public E addEdge(S arg0, S arg1) {
        throw new NotImplementedException();
    }


    @Override
    public boolean addEdge(S arg0, S arg1, E arg2) {
        throw new NotImplementedException();
    }


    @Override
    public boolean addVertex(S arg0) {
        throw new NotImplementedException();
    }


    @Override
    public boolean containsEdge(E arg0) {
        throw new NotImplementedException();
    }


    @Override
    public boolean containsEdge(S arg0, S arg1) {
        throw new NotImplementedException();
    }


    @Override
    public boolean containsVertex(S arg0) {
        throw new NotImplementedException();
    }


    @Override
    public Set<E> edgeSet() {
        throw new NotImplementedException();
    }


    @Override
    public Set<E> edgesOf(S arg0) {
        throw new NotImplementedException();
    }


    @Override
    public Set<E> getAllEdges(S arg0, S arg1) {
        throw new NotImplementedException();
    }


    @Override
    public E getEdge(S arg0, S arg1) {
        throw new NotImplementedException();
    }


    @Override
    public EdgeFactory<S, E> getEdgeFactory() {
        throw new NotImplementedException();
    }


    @Override
    public S getEdgeSource(E edge) {
        return edgeSources.get(edge);
    }


    @Override
    public S getEdgeTarget(E edge) {
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
    public Set<E> removeAllEdges(S arg0, S arg1) {
         throw new NotImplementedException();
    }


    @Override
    public boolean removeAllVertices(Collection<? extends S> arg0) {
         throw new NotImplementedException();
    }


    @Override
    public boolean removeEdge(E arg0) {
         throw new NotImplementedException();
    }


    @Override
    public E removeEdge(S arg0, S arg1) {
         throw new NotImplementedException();
    }


    @Override
    public boolean removeVertex(S arg0) {
         throw new NotImplementedException();
    }


    @Override
    public Set<S> vertexSet() {
         throw new NotImplementedException();
    }

}
