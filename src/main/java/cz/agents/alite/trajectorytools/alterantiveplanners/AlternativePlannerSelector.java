package cz.agents.alite.trajectorytools.alterantiveplanners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cz.agents.alite.trajectorytools.graph.maneuver.Maneuver;
import cz.agents.alite.trajectorytools.graph.maneuver.ManeuverGraphWithObstacles;
import cz.agents.alite.trajectorytools.graph.spatialwaypoint.SpatialWaypoint;
import cz.agents.alite.trajectorytools.planner.PlannedPath;

public class AlternativePlannerSelector implements AlternativePathPlanner {

    private final AlternativePathPlanner planner;
    private final int limit;

    public AlternativePlannerSelector(AlternativePathPlanner planner, int limit) {
        this.planner = planner;
        this.limit = limit;
    }
    
    @Override
    public Collection<PlannedPath<SpatialWaypoint, Maneuver>> planPath(
            ManeuverGraphWithObstacles graph, SpatialWaypoint startVertex,
            SpatialWaypoint endVertex) {
        
        Collection<PlannedPath<SpatialWaypoint, Maneuver>> plannedPaths = planner.planPath(graph, startVertex, endVertex);

        if (plannedPaths.size() <= limit) {
            return plannedPaths;
        }
        
        List<PlannedPath<SpatialWaypoint, Maneuver>> paths = new ArrayList<PlannedPath<SpatialWaypoint,Maneuver>>(plannedPaths);
        
        Collections.sort(paths, new Comparator<PlannedPath<SpatialWaypoint, Maneuver>>() {
            @Override
            public int compare(PlannedPath<SpatialWaypoint, Maneuver> o1,
                    PlannedPath<SpatialWaypoint, Maneuver> o2) {
                return Double.compare(o1.getWeight(), o2.getWeight() );
            }
        });
        
        return paths.subList(0, limit);
    }

    @Override
    public String getName() {
        return planner.getName() + " - limit (" + limit + ")";
    }

}
