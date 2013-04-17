package tt.euclidtime3i.util;

import java.util.Collection;
import java.util.Collections;

import tt.euclid2i.util.SeparationDetector;
import tt.euclidtime3i.Region;
import tt.euclidtime3i.region.MovingCircle;
import tt.util.NotImplementedException;

public class IntersectionChecker {

    public static boolean intersect(Region thisRegion, Collection<Region> obstacleCollection) {
        assert(thisRegion != null);
        assert(!obstacleCollection.contains(null));

        Region[] obstacles = obstacleCollection.toArray(new Region[1]);

        for (int j = 0; j < obstacles.length; j++) {
            if (obstacles[j] != null) {
            	if (/*thisRegion.getBoundingBox().intersects(obstacles[j].getBoundingBox())*/ true) {
            		if (intersect(thisRegion, obstacles[j])) {
            			return true;
            		}
            	}
            }
        }

        return false;
    }

	public static boolean intersect(Region thisRegion, Region otherRegion) {
		if (thisRegion instanceof MovingCircle && otherRegion instanceof MovingCircle) {
			MovingCircle thisMc = (MovingCircle) thisRegion;
			MovingCircle otherMc = (MovingCircle) otherRegion;

			return SeparationDetector.hasConflict(
					thisMc.getTrajectory(),
					Collections.singleton(otherMc.getTrajectory()),
					thisMc.getRadius() + otherMc.getRadius(),
					Math.min(thisMc.getRadius(), otherMc.getRadius())/4);
		}

		throw new NotImplementedException("The conflict checking of " + thisRegion.getClass() + " vs. " + otherRegion.getClass() + " not implemented yet");
	}
}
