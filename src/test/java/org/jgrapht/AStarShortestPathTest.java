package org.jgrapht;
import junit.framework.Assert;

import org.jgrapht.alg.AStarShortestPath;
import org.jgrapht.alg.DijkstraShortestPath;
import org.junit.Test;

import cz.agents.alite.trajectorytools.graph.maneuver.FourWayConstantSpeedGridGraph;
import cz.agents.alite.trajectorytools.graph.maneuver.Maneuver;
import cz.agents.alite.trajectorytools.graph.maneuver.ManeuverGraph;
import cz.agents.alite.trajectorytools.graph.maneuver.RandomWaypointGraph;
import cz.agents.alite.trajectorytools.graph.spatialwaypoint.SpatialWaypoint;
import cz.agents.alite.trajectorytools.util.Point;

public class AStarShortestPathTest {

    @Test
    public void test() {
        ManeuverGraph graph = FourWayConstantSpeedGridGraph.create(5, 5, 2, 2, 1.0);
        SpatialWaypoint start = graph.getNearestWaypoint(new Point(0, 0, 0));
        SpatialWaypoint end = graph.getNearestWaypoint(new Point(5.0, 5.0, 0));

        AStarShortestPath.Heuristic<SpatialWaypoint> heuristic = new AStarShortestPath.Heuristic<SpatialWaypoint>() {
            @Override
            public double getHeuristicEstimate(SpatialWaypoint current, SpatialWaypoint goal) {
                return current.distance(goal);
            }
        };

        AStarShortestPath<SpatialWaypoint, Maneuver> astar = new AStarShortestPath<SpatialWaypoint, Maneuver>(
                graph, start, end, heuristic);

        GraphPath<SpatialWaypoint, Maneuver> path = astar.getPath();
        System.out.println("Found path:");
        printPath(path);
    }

    @Test
    public void testAgainstDijkstra() {
        ManeuverGraph graph = FourWayConstantSpeedGridGraph.create(5, 5, 2, 2, 1.0);
        SpatialWaypoint start = graph.getNearestWaypoint(new Point(0, 0, 0));
        SpatialWaypoint end = graph.getNearestWaypoint(new Point(5.0, 5.0, 0));

        AStarShortestPath.Heuristic<SpatialWaypoint> heuristic = new AStarShortestPath.Heuristic<SpatialWaypoint>() {
            @Override
            public double getHeuristicEstimate(SpatialWaypoint current, SpatialWaypoint goal) {
                return current.distance(goal);
            }
        };

        long startTime = System.nanoTime();
        AStarShortestPath<SpatialWaypoint, Maneuver> astar = new AStarShortestPath<SpatialWaypoint, Maneuver>(
                graph, start, end, heuristic);

        GraphPath<SpatialWaypoint, Maneuver> astarPath = astar.getPath();
        System.out.println("A* search");
        printPath(astarPath);
        System.out.println("time:" + (System.nanoTime() - startTime)/1000000 + " ms. Length:" + astarPath.getWeight());

        System.out.println();

        startTime = System.nanoTime();
        DijkstraShortestPath<SpatialWaypoint, Maneuver> dijkstra = new DijkstraShortestPath<SpatialWaypoint, Maneuver>(
                graph, start, end);

        GraphPath<SpatialWaypoint, Maneuver> dijkstraPath = dijkstra
                .getPath();
        System.out.println("Dijkstra");
        printPath(dijkstraPath);
        System.out.println("search time:" + (System.nanoTime() - startTime)/1000000 + " ms Length:" + dijkstraPath.getWeight());


        if (astarPath.getEdgeList().equals(dijkstraPath.getEdgeList())) {
            System.out.println("Both methods found identical paths.");
        }

        Assert.assertEquals(dijkstraPath.getWeight() + " vs. " + astarPath.getWeight(), dijkstraPath.getWeight(), astarPath.getWeight(), 0.001 );
    }

    @Test
    public void testRandomWaypointGraphsAgainstDijkstra() {
        final int N = 100;

        long astarTime = 0;
        long dijkstraTime = 0;

        for (int seed=0; seed<N; seed++) {

            ManeuverGraph graph = RandomWaypointGraph.create(5, 5, 350, 6, seed);
            SpatialWaypoint start = graph.getNearestWaypoint(new Point(0, 0, 0));
            SpatialWaypoint end = graph.getNearestWaypoint(new Point(5.0, 5.0, 0));

            AStarShortestPath.Heuristic<SpatialWaypoint> heuristic = new AStarShortestPath.Heuristic<SpatialWaypoint>() {
                @Override
                public double getHeuristicEstimate(SpatialWaypoint current, SpatialWaypoint goal) {
                    return current.distance(goal);
                }
            };

            long startTime = System.nanoTime();
            AStarShortestPath<SpatialWaypoint, Maneuver> astar = new AStarShortestPath<SpatialWaypoint, Maneuver>(
                    graph, start, end, heuristic);

            GraphPath<SpatialWaypoint, Maneuver> astarPath = astar.getPath();

            astarTime += System.nanoTime()-startTime;



            startTime = System.nanoTime();
            DijkstraShortestPath<SpatialWaypoint, Maneuver> dijkstra = new DijkstraShortestPath<SpatialWaypoint, Maneuver>(
                    graph, start, end);

            GraphPath<SpatialWaypoint, Maneuver> dijkstraPath = dijkstra.getPath();

            dijkstraTime += System.nanoTime()-startTime;

            if (astarPath != dijkstraPath && !astarPath.getEdgeList().equals(dijkstraPath.getEdgeList())) {
                System.out.println("The two methods found different paths.");
            }

            if (dijkstraPath != null) {
                if (Math.abs(dijkstraPath.getWeight()-astarPath.getWeight()) > 0.0001) {
                    System.out.println("The paths found by dijkstra and A* have different length!");
                    System.out.println(" --------- " + seed);
                    System.out.println(" g: " + graph);
                    System.out.println("Dijkstra");
                    printPath(dijkstraPath);
                    System.out.println("A*");
                    printPath(astarPath);
                }
            }

            if (dijkstraPath != null)
                Assert.assertEquals(dijkstraPath.getWeight() + " vs. " + astarPath.getWeight(), dijkstraPath.getWeight(), astarPath.getWeight(), 0.001 );
        }

        System.out.println("Examined " + N + " random graphs. Dijkstra avg. time: " + dijkstraTime/(1000*N) + "ms" + ". A* avg. time: " + astarTime/(1000*N)+"ms.");
    }

    private void printPath(GraphPath<SpatialWaypoint, Maneuver> path) {
         if (path == null) {
             System.out.println("no path found");
             return;
         }
         System.out.print(path.getStartVertex() + ", ");
         for (Maneuver edge : path.getEdgeList()) {
             if (edge == null) {
                 System.out.println("Edge is null");
             }
             System.out.print(edge.getSource() + "-"
                     + edge.getTarget() + ", ");
         }
         System.out.print(path.getEndVertex());
         System.out.print(" Path weight:" + path.getWeight());
         System.out.println();
    }

}
