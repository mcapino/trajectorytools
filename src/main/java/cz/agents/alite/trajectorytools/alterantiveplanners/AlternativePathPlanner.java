package cz.agents.alite.trajectorytools.alterantiveplanners;

import java.util.Collection;

import cz.agents.alite.trajectorytools.graph.maneuver.Maneuver;
import cz.agents.alite.trajectorytools.graph.maneuver.ManeuverGraphWithObstacles;
import cz.agents.alite.trajectorytools.graph.spatialwaypoint.SpatialWaypoint;
import cz.agents.alite.trajectorytools.planner.PlannedPath;

public interface AlternativePathPlanner {

    Collection<PlannedPath<SpatialWaypoint, Maneuver>> planPath(ManeuverGraphWithObstacles graph, SpatialWaypoint startVertex, SpatialWaypoint endVertex);
}