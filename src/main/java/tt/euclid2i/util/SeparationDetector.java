package tt.euclid2i.util;

import tt.euclid2i.Point;
import tt.euclid2i.SegmentedTrajectory;
import tt.euclid2i.Trajectory;
import tt.euclidtime3i.discretization.Straight;

import java.util.*;

public class SeparationDetector {

    public static boolean hasAnyPairwiseConflictAnalytic(SegmentedTrajectory thisTrajectory, SegmentedTrajectory[] otherTrajectories, int[] separations) {

        for (int otherTrajId = 0; otherTrajId < otherTrajectories.length; otherTrajId++) {

            SegmentedTrajectory otherTrajectory = otherTrajectories[otherTrajId];

            //TODO it would be faster not to copy those sets all the time by using some condition for extendShorterList() in while cycle
            LinkedList<Straight> segmentsA = new LinkedList<Straight>(thisTrajectory.getSegments());
            LinkedList<Straight> segmentsB = new LinkedList<Straight>(otherTrajectory.getSegments());

            extendShorterList(segmentsA, segmentsB);
            prependShorterList(segmentsA, segmentsB);

            ListIterator<Straight> iteratorA = segmentsA.listIterator();
            ListIterator<Straight> iteratorB = segmentsB.listIterator();

            int i = 0;
            int j = 0;

            Straight straightA = iteratorA.next();
            Straight straightB = iteratorB.next();

            while (i < segmentsA.size() && j < segmentsB.size()) {

                int startTimeA = straightA.getStart().getTime();
                int startTimeB = straightB.getStart().getTime();

                int endTimeA = straightA.getEnd().getTime();
                int endTimeB = straightB.getEnd().getTime();

                int t1 = Math.max(startTimeA, startTimeB);
                int t2 = Math.min(endTimeA, endTimeB);

                if (hasConflict(straightA.cut(t1, t2), straightB.cut(t1, t2), separations[otherTrajId])) {
                    return true;
                }

                if (endTimeA == t2) {
                    if (iteratorA.hasNext())
                        straightA = iteratorA.next();
                    i++;
                }
                if (endTimeB == t2) {
                    if (iteratorB.hasNext())
                        straightB = iteratorB.next();
                    j++;
                }
            }
        }

        return false;
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
                        if (thisTrajectoryPos.distance(otherTrajectoryPos) <= separations[j]) {
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
                        if (a.get(t).distance(b.get(t)) <= separation) {
                            return true;
                        }
                    }

                }
            }
        }
        return false;
    }

    private static void prependShorterList(LinkedList<Straight> segmentsA, LinkedList<Straight> segmentsB) {
        tt.euclidtime3i.Point firstA = segmentsA.get(0).getStart();
        int startA = firstA.getTime();
        tt.euclidtime3i.Point firstB = segmentsB.get(0).getStart();
        int startB = firstB.getTime();

        if (startA > startB) {
            Point extend = firstA.getPosition();
            segmentsA.addFirst(new Straight(new tt.euclidtime3i.Point(extend, startB), new tt.euclidtime3i.Point(extend, startA)));
        } else if (startB < startA) {
            Point extend = firstB.getPosition();
            segmentsB.addFirst(new Straight(new tt.euclidtime3i.Point(extend, startA), new tt.euclidtime3i.Point(extend, startB)));
        }
    }

    private static void extendShorterList(LinkedList<Straight> segmentsA, LinkedList<Straight> segmentsB) {
        tt.euclidtime3i.Point lastA = segmentsA.get(segmentsA.size() - 1).getEnd();
        int finishA = lastA.getTime();
        tt.euclidtime3i.Point lastB = segmentsB.get(segmentsB.size() - 1).getEnd();
        int finishB = lastB.getTime();

        if (finishA < finishB) {
            Point extend = lastA.getPosition();
            segmentsA.add(new Straight(new tt.euclidtime3i.Point(extend, finishA), new tt.euclidtime3i.Point(extend, finishB)));
        } else if (finishB < finishA) {
            Point extend = lastB.getPosition();
            segmentsB.add(new Straight(new tt.euclidtime3i.Point(extend, finishB), new tt.euclidtime3i.Point(extend, finishA)));
        }
    }

    private static boolean hasConflict(Straight sA, Straight sB, int separation) {
        Point a = sA.getStart().getPosition();
        Point b = sA.getEnd().getPosition();

        Point c = sB.getStart().getPosition();
        Point d = sB.getEnd().getPosition();

        int ux = a.x - c.y;
        int uy = a.y - c.y;

        int vx = b.x + c.x - d.x - a.x;
        int vy = b.y + c.y - d.y - a.y;

        int nom = -(ux * vx + uy * vy);
        int denom = vx * vx + vy * vy;

        if (denom == 0)
            return a.distance(c) < separation;

        double frac = ((double) nom) / denom;

        if (frac < 0) {
            return a.distance(c) < separation;

        } else if (frac > 1) {
            return b.distance(d) < separation;

        } else {
            int abx = (int) (a.x + (b.x - a.x) * frac);
            int aby = (int) (a.y + (b.y - a.y) * frac);

            int cdx = (int) (c.x + (d.x - c.x) * frac);
            int cdy = (int) (c.y + (d.y - c.y) * frac);

            int dx = abx - cdx;
            int dy = aby - cdy;

            return Math.sqrt(dx * dx + dy * dy) < separation;
        }
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
                        if (thisTrajectoryPos.distance(otherTrajectoryPos) <= separation) {
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
                        if (a.get(t).distance(b.get(t)) <= separation) {
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
                        if (a.get(t).distance(b.get(t)) <= separation) {
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
                        if (thisTrajectoryPos.distance(otherTrajectoryPos) <= separation) {
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
                            if (thisTrajectoryPos.distance(otherTrajectoryPos) <= separation) {
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
                        if (thisTrajectoryPos.distance(otherTrajectoryPos) <= separation) {
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
