package tt.euclid2i.util;

import com.google.common.collect.Iterables;
import tt.euclid2i.Point;
import tt.euclid2i.SegmentedTrajectory;
import tt.euclid2i.Trajectory;
import tt.euclidtime3i.Geometry3i;
import tt.euclidtime3i.discretization.Straight;

import java.util.*;

public class SeparationDetector {

    //TODO parse stuff for SegmentedTrajectory to separate class!

    public static boolean hasAnyPairwiseConflictAnalytic(SegmentedTrajectory thisTrajectory, SegmentedTrajectory[] otherTrajectories, int[] separations) {
        checkNonEmpty(thisTrajectory);
        Iterable<Straight> segmentsA = extendListFromMinToMaxTime(thisTrajectory);

        for (int i = 0; i < otherTrajectories.length; i++) {
            SegmentedTrajectory otherTrajectory = otherTrajectories[i];
            checkNonEmpty(otherTrajectory);

            Iterable<Straight> segmentsB = extendListFromMinToMaxTime(otherTrajectory);

            Iterator<Straight> iteratorA = segmentsA.iterator();
            Iterator<Straight> iteratorB = segmentsB.iterator();

            if (checkSegmentsForCollision(iteratorA, iteratorB, separations[i]))
                return true;
        }

        return false;
    }

    private static boolean checkSegmentsForCollision(Iterator<Straight> iteratorA, Iterator<Straight> iteratorB, int separation) {
        Straight a = null, b = null;

        do {
            if (a == null && b == null || endsAtSameTime(a, b)) {
                a = iteratorA.next();
                b = iteratorB.next();
            } else if (endsEarlier(a, b)) {
                a = iteratorA.next();
            } else {
                b = iteratorB.next();
            }

            if (collides(a, b, separation))
                return true;
        }
        while (iteratorA.hasNext() || iteratorB.hasNext());

        return false;
    }

    private static boolean endsAtSameTime(Straight a, Straight b) {
        return a.getEnd().getTime() == b.getEnd().getTime();
    }

    private static boolean collides(Straight a, Straight b, int separation) {
        return Geometry3i.distance(a, b) < separation;
    }

    private static boolean endsEarlier(Straight a, Straight b) {
        return a.getEnd().getTime() < b.getEnd().getTime();
    }

    private static void checkNonEmpty(SegmentedTrajectory segmentedTrajectory) {
        if (segmentedTrajectory == null)
            throw new IllegalArgumentException("Null trajectory passed to SeparationDetector");

        List<Straight> segments = segmentedTrajectory.getSegments();
        if (segments == null || segments.isEmpty())
            throw new IllegalArgumentException("Empty trajectory");
    }

    private static Iterable<Straight> extendListFromMinToMaxTime(SegmentedTrajectory trajectory) {
        //TODO clean up this method
        List<Straight> segments = trajectory.getSegments();
        int size = segments.size();

        LinkedList<Collection<Straight>> listsOfSegments = new LinkedList<Collection<Straight>>();
        listsOfSegments.add(segments);

        tt.euclidtime3i.Point startPoint3i = segments.get(0).getStart();
        tt.euclidtime3i.Point endPoint3i = segments.get(size - 1).getEnd();

        int start = startPoint3i.getTime();
        int end = endPoint3i.getTime();

        int minTime = trajectory.getMinTime();
        int maxTine = trajectory.getMaxTime();

        Point startPosition = startPoint3i.getPosition();
        Point endPosition = endPoint3i.getPosition();

        if (minTime < start) {
            Straight waitFromMinTime = new Straight(new tt.euclidtime3i.Point(startPosition, minTime), new tt.euclidtime3i.Point(startPosition, start));
            listsOfSegments.addFirst(Collections.singletonList(waitFromMinTime));
        }

        if (maxTine > end) {
            Straight waitToMaxTime = new Straight(new tt.euclidtime3i.Point(endPosition, end), new tt.euclidtime3i.Point(endPosition, maxTine));
            listsOfSegments.addLast(Collections.singletonList(waitToMaxTime));
        }

        return Iterables.concat(listsOfSegments);
    }

    public static boolean hasAnyPairwiseConflict(Trajectory thisTrajectory, Trajectory[] otherTrajectories, int[] separations, int samplingInterval) {

        for (int t = thisTrajectory.getMinTime(); t < thisTrajectory
                .getMaxTime(); t += samplingInterval) {
            Point thisTrajectoryPos = thisTrajectory.get(t);
            for (int j = 0; j < otherTrajectories.length; j++) {

                if (otherTrajectories[j] != null) {
                    if (t >= otherTrajectories[j].getMinTime()
                            && t <= otherTrajectories[j].getMaxTime()) {
                        Point otherTrajectoryPos = otherTrajectories[j]
                                .get(t);
                        if (thisTrajectoryPos.distance(otherTrajectoryPos) < separations[j]) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean hasAnyPairwiseConflict(Trajectory[] trajectories, int separations[], int samplingInterval) {

        int minTime = Integer.MAX_VALUE;
        for (int i = 0; i < trajectories.length; i++) {
            if (trajectories[i].getMinTime() < minTime) {
                minTime = trajectories[i].getMinTime();
            }
        }

        int maxTime = Integer.MIN_VALUE;
        for (int i = 0; i < trajectories.length; i++) {
            if (trajectories[i].getMaxTime() > maxTime) {
                maxTime = trajectories[i].getMaxTime();
            }
        }

        // iterate over all time points
        for (int t = minTime; t <= maxTime; t += samplingInterval) {
            // check all pairs of agents for conflicts at timepoint t
            for (int j = 0; j < trajectories.length; j++) {
                for (int k = j + 1; k < trajectories.length; k++) {
                    // check the distance between j and k
                    Trajectory a = trajectories[j];
                    Trajectory b = trajectories[k];

                    if (t >= a.getMinTime() && t <= a.getMaxTime() &&
                            t >= b.getMinTime() && t <= b.getMaxTime()) {
                        if (a.get(t).distance(b.get(t)) < separations[j] + separations[k]) {
                            return true;
                        }
                    }

                }
            }
        }
        return false;
    }

    public static boolean hasAnyPairwiseConflict(Collection<Trajectory> trajectoriesCollection, int separation, int samplingInterval) {

        List<Trajectory> trajectories = new ArrayList<Trajectory>(trajectoriesCollection);

        int minTime = Integer.MAX_VALUE;
        for (Trajectory trajectory : trajectories) {
            if (trajectory.getMinTime() < minTime) {
                minTime = trajectory.getMinTime();
            }
        }

        int maxTime = Integer.MIN_VALUE;
        for (Trajectory trajectory : trajectories) {
            if (trajectory.getMaxTime() > maxTime) {
                maxTime = trajectory.getMaxTime();
            }
        }

        // iterate over all time points
        for (int t = minTime; t <= maxTime; t += samplingInterval) {
            // check all pairs of agents for conflicts at timepoint t
            for (int j = 0; j < trajectories.size(); j++) {
                for (int k = j + 1; k < trajectories.size(); k++) {
                    // check the distance between j and k
                    Trajectory a = trajectories.get(j);
                    Trajectory b = trajectories.get(k);

                    if (t >= a.getMinTime() && t <= a.getMaxTime() &&
                            t >= b.getMinTime() && t <= b.getMaxTime()) {
                        if (a.get(t).distance(b.get(t)) < separation) {
                            return true;
                        }
                    }

                }
            }
        }
        return false;
    }

    /**
     * Computes pairwise conflicts between thisTrajectory and otherTrajectories.
     */
    public static List<tt.euclidtime3i.Point> computePairwiseConflicts(Trajectory thisTrajectory,
                                                                       Collection<Trajectory> otherTrajectoriesCollection,
                                                                       int separation, int samplingInterval) {


        List<Trajectory> otherTrajectories = new ArrayList<Trajectory>(otherTrajectoriesCollection);
        List<tt.euclidtime3i.Point> conflicts = new LinkedList<tt.euclidtime3i.Point>();

        for (int t = thisTrajectory.getMinTime(); t < thisTrajectory.getMaxTime(); t += samplingInterval) {
            Point thisTrajectoryPos = thisTrajectory.get(t);
            for (int j = 0; j < otherTrajectories.size(); j++) {

                if (otherTrajectories.get(j) != null) {
                    if (t >= otherTrajectories.get(j).getMinTime() && t <= otherTrajectories.get(j).getMaxTime()) {
                        Point otherTrajectoryPos = otherTrajectories.get(j).get(t);
                        if (thisTrajectoryPos.distance(otherTrajectoryPos) < separation) {
                            conflicts.add(new tt.euclidtime3i.Point(thisTrajectoryPos, t));
                        }
                    }
                }
            }
        }
        return conflicts;
    }


    /**
     * Computes all pairwise conflicts between all pairs of trajectories from trajectoriesCollection.
     */
    public static List<tt.euclidtime3i.Point> computeAllPairwiseConflicts(Collection<Trajectory> trajectoriesCollection, int separation, int samplingInterval) {

        List<Trajectory> trajectories = new ArrayList<Trajectory>(trajectoriesCollection);
        List<tt.euclidtime3i.Point> conflicts = new LinkedList<tt.euclidtime3i.Point>();

        int minTime = Integer.MAX_VALUE;
        for (Trajectory trajectory : trajectories) {
            if (trajectory.getMinTime() < minTime) {
                minTime = trajectory.getMinTime();
            }
        }

        int maxTime = Integer.MIN_VALUE;
        for (Trajectory trajectory : trajectories) {
            if (trajectory.getMaxTime() > maxTime) {
                maxTime = trajectory.getMaxTime();
            }
        }

        // iterate over all time points
        for (int t = minTime; t <= maxTime; t += samplingInterval) {
            // check all pairs of agents for conflicts at timepoint t
            for (int j = 0; j < trajectories.size(); j++) {
                for (int k = j + 1; k < trajectories.size(); k++) {
                    // check the distance between j and k
                    Trajectory a = trajectories.get(j);
                    Trajectory b = trajectories.get(k);

                    if (t >= a.getMinTime() && t <= a.getMaxTime() &&
                            t >= b.getMinTime() && t <= b.getMaxTime()) {
                        if (a.get(t).distance(b.get(t)) < separation) {
                            conflicts.add(new tt.euclidtime3i.Point(a.get(t), t));
                            conflicts.add(new tt.euclidtime3i.Point(b.get(t), t));
                        }
                    }

                }
            }
        }
        return conflicts;
    }


    /**
     * finds the first conflicts and returns ids of the two agents involved in the conflict
     *
     * @return array containing ids of agents involved in the conflict, null if no conflicts are found
     */
    public static int[] findFirstConflict(Collection<Trajectory> trajectoriesCollection, int separation, int samplingInterval) {

        List<Trajectory> trajectories = new ArrayList<Trajectory>(trajectoriesCollection);

        int minTime = Integer.MAX_VALUE;
        for (Trajectory trajectory : trajectories) {
            if (trajectory.getMinTime() < minTime) {
                minTime = trajectory.getMinTime();
            }
        }

        int maxTime = Integer.MIN_VALUE;
        for (Trajectory trajectory : trajectories) {
            if (trajectory.getMaxTime() > maxTime) {
                maxTime = trajectory.getMaxTime();
            }
        }

        // iterate over all time points
        for (int t = minTime; t <= maxTime; t += samplingInterval) {
            // check all pairs of agents for conflicts at timepoint t
            for (int j = 0; j < trajectories.size(); j++) {
                for (int k = j + 1; k < trajectories.size(); k++) {
                    // check the distance between j and k
                    Trajectory a = trajectories.get(j);
                    Trajectory b = trajectories.get(k);

                    if (t >= a.getMinTime() && t <= a.getMaxTime() &&
                            t >= b.getMinTime() && t <= b.getMaxTime()) {
                        if (a.get(t).distance(b.get(t)) < separation) {
                            return new int[]{j, k};
                        }
                    }

                }
            }
        }

        return null;
    }

    public static boolean hasConflict(Collection<Trajectory> trajectoriesCollection, int separation, int samplingInterval) {
        return hasAnyPairwiseConflict(trajectoriesCollection, separation, samplingInterval);
    }

    public static boolean hasConflict(Collection<Trajectory> trajectoriesCollection, int separation, int samplingInterval, int maxSpeed) {
        if (mayHaveConflict(trajectoriesCollection, separation, maxSpeed)) {
            return hasAnyPairwiseConflict(trajectoriesCollection, separation, samplingInterval);
        } else {
            return false;
        }

    }

    /**
     * Quickly determines if the trajectories are close enough for a conflict to occur.
     */
    public static boolean mayHaveConflict(Collection<Trajectory> trajectoriesCollection, int separation, int maxSpeed) {
        if (trajectoriesCollection.isEmpty()) return false;

        List<Trajectory> trajectories = new ArrayList<Trajectory>(trajectoriesCollection);

        Trajectory t = trajectories.get(0);

        int criticalDist = (t.getMaxTime() - t.getMinTime()) * maxSpeed + separation;

        for (int j = 0; j < trajectories.size(); j++) {
            for (int k = j + 1; k < trajectories.size(); k++) {
                // check the distance between j and k
                Trajectory a = trajectories.get(j);
                Trajectory b = trajectories.get(k);

                if (a.get(a.getMinTime()).distance(b.get(b.getMinTime())) < criticalDist) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Determines if thisTrajectory has conflict with any of the trajectories from otherTrajectoriesCollection.
     */
    public static boolean hasConflict(Trajectory thisTrajectory, Collection<Trajectory> otherTrajectoriesCollection, int separation, int samplingInterval) {

        assert (thisTrajectory != null);
        assert (!otherTrajectoriesCollection.contains(null));

        List<Trajectory> otherTrajectories = new ArrayList<Trajectory>(otherTrajectoriesCollection);

        for (int t = thisTrajectory.getMinTime(); t < thisTrajectory.getMaxTime(); t += samplingInterval) {
            Point thisTrajectoryPos = thisTrajectory.get(t);
            for (int j = 0; j < otherTrajectories.size(); j++) {

                if (otherTrajectories.get(j) != null) {
                    if (t >= otherTrajectories.get(j).getMinTime() && t <= otherTrajectories.get(j).getMaxTime()) {
                        Point otherTrajectoryPos = otherTrajectories.get(j).get(t);
                        if (thisTrajectoryPos.distance(otherTrajectoryPos) < separation) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Determines if thisTrajectory has conflict with any of the trajectories from otherTrajectoriesCollection.
     */
    public static boolean hasConflict(Trajectory thisTrajectory, Collection<Trajectory> otherTrajectoriesCollection, int separation, int samplingInterval, int maxSpeed) {

        assert (thisTrajectory != null);
        assert (!otherTrajectoriesCollection.contains(null));

        List<Trajectory> otherTrajectories = new ArrayList<Trajectory>(otherTrajectoriesCollection);

        int criticalDist = (thisTrajectory.getMaxTime() - thisTrajectory.getMinTime()) * maxSpeed + separation;

        boolean approximateConflict = false;

        for (int j = 0; j < otherTrajectories.size(); j++) {
            Trajectory b = otherTrajectories.get(j);
            if (b != null) {
                if (thisTrajectory.get(thisTrajectory.getMinTime()).distance(b.get(b.getMinTime())) < criticalDist) {
                    approximateConflict = true;
                }
            }
        }

        if (approximateConflict) {
            for (int t = thisTrajectory.getMinTime(); t < thisTrajectory.getMaxTime(); t += samplingInterval) {
                Point thisTrajectoryPos = thisTrajectory.get(t);
                for (int j = 0; j < otherTrajectories.size(); j++) {

                    if (otherTrajectories.get(j) != null) {
                        if (t >= otherTrajectories.get(j).getMinTime() && t <= otherTrajectories.get(j).getMaxTime()) {
                            Point otherTrajectoryPos = otherTrajectories.get(j).get(t);
                            if (thisTrajectoryPos.distance(otherTrajectoryPos) < separation) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Counts the number of trajectories from the given collection that have a conflict with the given trajectory
     */
    public static int countConflictingTrajectories(Trajectory thisTrajectory, Collection<Trajectory> otherTrajectoriesCollection, int separation, int samplingInterval) {

        int count = 0;

        assert (thisTrajectory != null);
        assert (!otherTrajectoriesCollection.contains(null));

        List<Trajectory> otherTrajectories = new ArrayList<Trajectory>(otherTrajectoriesCollection);

        for (int t = thisTrajectory.getMinTime(); t < thisTrajectory.getMaxTime(); t += samplingInterval) {
            Point thisTrajectoryPos = thisTrajectory.get(t);
            for (int j = 0; j < otherTrajectories.size(); j++) {

                if (otherTrajectories.get(j) != null) {
                    if (t >= otherTrajectories.get(j).getMinTime() && t <= otherTrajectories.get(j).getMaxTime()) {
                        Point otherTrajectoryPos = otherTrajectories.get(j).get(t);
                        if (thisTrajectoryPos.distance(otherTrajectoryPos) < separation) {
                            count++;
                            continue;
                        }
                    }
                }
            }
        }

        return count;
    }

}
