package cz.agents.alite.trajectorytools.graph.maneuver;

import cz.agents.alite.trajectorytools.graph.spatialwaypoint.SpatialWaypoint;

public class CopyManeuverGraphWithObstacles {
    
    private CopyManeuverGraphWithObstacles() {}

    public static ManeuverGraphWithObstacles create(ManeuverGraphWithObstacles other) {
        ObstacleGraphView graph = new ObstacleGraphView( 
                CopyManeuverGraph.create( other ),
                new ObstacleGraphView.ChangeListener() {
                    @Override
                    public void graphChanged() {
                    }
                });

        for (SpatialWaypoint obstacle : other.getObstacles()) {
            graph.addObstacle(obstacle);
        }
        
        return graph;
    }
}
