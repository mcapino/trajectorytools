package cz.agents.alite.trajectorytools.graph.spatial.rrtstar;

import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

import org.junit.Test;

import cz.agents.alite.trajectorytools.graph.spatial.maneuvers.SpatialManeuver;
import cz.agents.alite.trajectorytools.graph.spatial.region.BoxRegion;
import cz.agents.alite.trajectorytools.graph.spatial.region.SpaceRegion;
import cz.agents.alite.trajectorytools.planner.rrtstar.Domain;
import cz.agents.alite.trajectorytools.planner.rrtstar.RRTStarPlanner;
import cz.agents.alite.trajectorytools.util.SpatialPoint;

public class RRTStar2dTest {

    @Test
    public void test() {

        RRTStarPlanner<SpatialPoint, SpatialManeuver> rrtstar;

        SpatialPoint initialPoint = new SpatialPoint(100, 100, 0);
        BoxRegion bounds = new BoxRegion(new SpatialPoint(0, 0, 0),
                new SpatialPoint(1000, 1000, 1000));
        Collection<SpaceRegion> obstacles = new LinkedList<SpaceRegion>();
        SpaceRegion target = new BoxRegion(new SpatialPoint(500, 850, -1000),
                new SpatialPoint(600, 870, 1000));

        // Generate obstacles
        int n = 300;
        int maxSize = 60;
        Random random = new Random(1);
        for (int i = 0; i < n; i++) {

            double size = random.nextDouble() * maxSize;
            double x = bounds.getCorner1().x + random.nextDouble()
                    * (bounds.getCorner2().x - bounds.getCorner1().x);
            double y = bounds.getCorner1().y + random.nextDouble()
                    * (bounds.getCorner2().y - bounds.getCorner1().y);
            SpaceRegion obstacle = new BoxRegion(new SpatialPoint(x, y, 0),
                    new SpatialPoint(x + size, y + size, 750));
            if (!obstacle.isInside(initialPoint)) {
                obstacles.add(obstacle);
            }
        }

        Domain<SpatialPoint, SpatialManeuver> domain = new SpatialStraightLineDomain(
                bounds, obstacles, target, 1.0);
        rrtstar = new RRTStarPlanner<SpatialPoint, SpatialManeuver>(domain,
                initialPoint, 1300);

        int iterations = 1000;
        for (int i = 0; i < iterations; i++) {
            rrtstar.iterate();
        }

        assertTrue(rrtstar.foundSolution());
    }
}
