package tt.jointeuclidean2ni.probleminstance;

import java.util.Collection;
import java.util.Random;

import tt.euclid2i.Point;
import tt.euclid2i.probleminstance.Environment;
import tt.euclid2i.region.Rectangle;
import tt.euclid2i.region.Region;
import tt.euclid2i.util.Util;

public class ShortestPathProblem {

    public static class CannotPlaceAgentsException extends RuntimeException {};

    final protected int targetRegionSide = 30;
    protected int seed;
    protected int nAgents;
    protected int agentSizeRadius;
    protected Environment environment;
    protected Point starts[];
    protected Region targetRegions[];
    protected Point targetPoints[];
    private Random random;

    public ShortestPathProblem(int nAgents, int agentSizeRadius, int seed) {
        this.seed = seed;
        this.nAgents = nAgents;
        this.agentSizeRadius = agentSizeRadius;
        this.environment = new Environment(40, 200,seed);
        this.random = new Random(seed);

        starts = new Point[nAgents];
        targetPoints = new Point[nAgents];
        targetRegions = new Region[nAgents];

        generateMissions();
    }

    protected void generateMissions() {
        int trials = 0;
        for (int i=0; i<nAgents; i++) {
            Point start;
            Point target;

            do {
                start  = Util.sampleFreeSpace(getBounds(), getObstacles(), random);
                target =  Util.sampleFreeSpace(getBounds(), getObstacles(), random);

                trials++;
                if (trials > 10000) {
                    throw new CannotPlaceAgentsException();
                }
            }
            while (!isUniqueStart(start) || !isUniqueTarget(target));

            starts[i] = start;
            targetPoints[i] = target;
            targetRegions[i] = new Rectangle(
                    new Point(target.x - targetRegionSide / 2, target.y - targetRegionSide / 2),
                    new Point(target.x + targetRegionSide / 2, target.y + targetRegionSide / 2));
        }
    }

    public boolean isUniqueStart(Point point) {
        for (int i=0; i<nAgents; i++ ) {
            if (starts[i] != null) {
                if (starts[i].distance(point) < agentSizeRadius*2) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isUniqueTarget(Point point) {
        for (int i=0; i<nAgents; i++ ) {
            if (targetPoints[i] != null) {
                if (targetPoints[i].distance(point) < agentSizeRadius*2) {
                    return false;
                }
            }
        }

        return true;
    }

    public int nAgents() {
        return nAgents;
    }

    public Point getStart(int i) {
        return starts[i];
    }

    public Point getTargetPoint(int i) {
        return targetPoints[i];
    }

    public Region getTargetRegions(int i) {
        return targetRegions[i];
    }

    public Region[] getTargetRegions() {
        return targetRegions;
    }

    public Point[] getStarts() {
        return starts;
    }


    public Collection<Region> getObstacles() {
        return environment.getObstacles();
    }

    public Rectangle getBounds() {
        return environment.getBounds();
    }

    public int getSeed() {
        return seed;
    }

    public int getTargetRegionRadius() {
        return targetRegionSide / 2;
    }

    public Environment getEnvironment() {
        return environment;
    }

}
