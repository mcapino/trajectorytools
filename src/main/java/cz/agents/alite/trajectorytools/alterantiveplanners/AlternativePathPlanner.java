package cz.agents.alite.trajectorytools.alterantiveplanners;

import java.util.Collection;

import org.jgrapht.Graph;

import cz.agents.alite.trajectorytools.planner.PlannedPath;

public interface AlternativePathPlanner<V, E> {

    Collection<PlannedPath<V, E>> planPath(Graph<V, E> graph, V startVertex, V endVertex);
}