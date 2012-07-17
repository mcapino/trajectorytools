package cz.agents.alite.trajectorytools.experiments;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cz.agents.alite.creator.Creator;
import cz.agents.alite.trajectorytools.alterantiveplanners.AlternativePathPlanner;
import cz.agents.alite.trajectorytools.alterantiveplanners.AlternativePlannerSelector;
import cz.agents.alite.trajectorytools.alterantiveplanners.DifferentStateMetricPlanner;
import cz.agents.alite.trajectorytools.alterantiveplanners.ObstacleExtensions;
import cz.agents.alite.trajectorytools.alterantiveplanners.TrajectoryDistanceMaxMinMetricPlanner;
import cz.agents.alite.trajectorytools.alterantiveplanners.TrajectoryDistanceMetricPlanner;
import cz.agents.alite.trajectorytools.alterantiveplanners.VoronoiDelaunayPlanner;
import cz.agents.alite.trajectorytools.graph.maneuver.EightWayConstantSpeedGridGraph;
import cz.agents.alite.trajectorytools.graph.maneuver.Maneuver;
import cz.agents.alite.trajectorytools.graph.maneuver.ManeuverGraph;
import cz.agents.alite.trajectorytools.graph.maneuver.ManeuverGraphInterface;
import cz.agents.alite.trajectorytools.graph.maneuver.ObstacleGraphView;
import cz.agents.alite.trajectorytools.graph.maneuver.ObstacleGraphView.ChangeListener;
import cz.agents.alite.trajectorytools.graph.spatialwaypoint.SpatialWaypoint;
import cz.agents.alite.trajectorytools.planner.AStarPlanner;
import cz.agents.alite.trajectorytools.planner.HeuristicFunction;
import cz.agents.alite.trajectorytools.planner.PlannedPath;
import cz.agents.alite.trajectorytools.trajectorymetrics.DifferentStateMetric;
import cz.agents.alite.trajectorytools.trajectorymetrics.ManeuverTrajectoryMetric;
import cz.agents.alite.trajectorytools.trajectorymetrics.ObstacleAvoidanceMetric;
import cz.agents.alite.trajectorytools.trajectorymetrics.TrajectoryDistanceMetric;
import cz.agents.alite.trajectorytools.trajectorymetrics.TrajectorySetMetrics;
import cz.agents.alite.trajectorytools.util.Point;

public class AlternativePlanners1Creator implements Creator {

    private static final int NUM_OF_OBSTACLES_MIN = 2;
    private static final int NUM_OF_OBSTACLES_MAX = 16;
    private static final int NUM_OF_OBSTACLES_STEP = 2;
    private static final int NUM_OF_REPEATS = 5;
    
    private static final int NUM_OF_THREADS = 5;
    private static final ExecutorService executor = Executors.newFixedThreadPool(NUM_OF_THREADS);

    private static final int PATH_SOLUTION_LIMIT = 5;

    private static final int WORLD_SIZE = 10;

    private static final AStarPlanner<SpatialWaypoint, Maneuver> planner = new AStarPlanner<SpatialWaypoint, Maneuver>();
    {
        planner.setHeuristicFunction(new HeuristicFunction<SpatialWaypoint>() {
        @Override
            public double getHeuristicEstimate(SpatialWaypoint current, SpatialWaypoint goal) {
                return current.distance(goal) + ( current.x > current.y ? 0.1 : -0.1 );
            }
        });
    }

    private static final AlternativePathPlanner[] alternativePlanners = new AlternativePathPlanner[] {
        new DifferentStateMetricPlanner( planner, PATH_SOLUTION_LIMIT ),
        new TrajectoryDistanceMetricPlanner( planner, PATH_SOLUTION_LIMIT, 2),
        new TrajectoryDistanceMaxMinMetricPlanner( planner, PATH_SOLUTION_LIMIT, 2 ),
        new ObstacleExtensions(planner),
        new AlternativePlannerSelector( new ObstacleExtensions(planner), PATH_SOLUTION_LIMIT),
        new VoronoiDelaunayPlanner( planner ),
        new AlternativePlannerSelector( new VoronoiDelaunayPlanner(planner), PATH_SOLUTION_LIMIT),
    };

    private static final ManeuverTrajectoryMetric[] trajectoryMetrics = new ManeuverTrajectoryMetric [] {
        new DifferentStateMetric(),
        new TrajectoryDistanceMetric(),
        new ObstacleAvoidanceMetric()
    };

    @Override
    public void init(String[] args) {
    }

    @Override
    public void create() {
        
        try {
            // detailed results
            final BufferedWriter out = new BufferedWriter(new FileWriter( "results.csv"));
    
            // aggregated results
//            BufferedWriter aggrOut = new BufferedWriter(new FileWriter(
//                    "results_aggr.csv"));
    
            out.write("WORLD_SIZE;" + WORLD_SIZE + ";Repeats;" + NUM_OF_REPEATS + ";Obst. cases;" + ( (NUM_OF_OBSTACLES_MAX - NUM_OF_OBSTACLES_MIN)/NUM_OF_OBSTACLES_STEP + 1)  );
            out.newLine();
            out.write( "numObstacles;experiment name;planner;duration;num of paths;average path lenth" );
            for (ManeuverTrajectoryMetric metric : trajectoryMetrics) {
                out.write(";" + metric.getName());
            }
            out.newLine();
    
            for (int numObstacles = NUM_OF_OBSTACLES_MIN; numObstacles <= NUM_OF_OBSTACLES_MAX; numObstacles+=NUM_OF_OBSTACLES_STEP) {
            
//                System.out.println("numObstacles: " + numObstacles);
                
                for (int repeat = 0; repeat < NUM_OF_REPEATS; repeat++) {
//                    System.out.println("repeat: " + repeat);
                       
                    final List<Point> obstacles = generateRandomObstacles(numObstacles);

                    int plannerNum = 0;
                    for (final AlternativePathPlanner planner : alternativePlanners) {
//                        System.out.println("planner.getName(): " + planner.getName());
                        final String runStr = numObstacles + "/" + repeat;

                        plannerNum++;
                        
                        final int curPlannerNum = plannerNum;
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("run: " + runStr);
                                runExperiment(out, obstacles, planner, curPlannerNum);
                            }
                        });
                    }
                    
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        executor.shutdown();
    }

    private List<Point> generateRandomObstacles(int number) {
        
        while (true) {
            List<Point> obstacles = new ArrayList<Point>(number);
        
    	    for (int i=0; i<number; i++) {
    	        obstacles.add(new Point(Math.random() * WORLD_SIZE, Math.random() * WORLD_SIZE, 0.0 ));
    	    }

    	    //
    	    // Check whether a path exists
    	    //
    	    ManeuverGraph graph = createGraph();
    	    
            PlannedPath<SpatialWaypoint, Maneuver> planPath = planner.planPath(
                    graph, 
                    graph.getNearestWaypoint(new Point(0, 0, 0)),
                    graph.getNearestWaypoint(new Point(WORLD_SIZE, WORLD_SIZE, 0))
                    );
            if (planPath != null) {
                return obstacles;
            }
        }
    }
    
    private void runExperiment(
            final BufferedWriter out,
            final List<Point> obstacles,
            final AlternativePathPlanner planner, 
            int curPlannerNum) {
        ManeuverGraphInterface originalGraph = createGraph(); 

        ObstacleGraphView graph = new ObstacleGraphView( originalGraph, new ChangeListener() {
            @Override
            public void graphChanged() {
            }
        } );

        for (Point obstacle : obstacles) {
            graph.addObstacle(obstacle);
        }

        long startTime = System.currentTimeMillis();
        Collection<PlannedPath<SpatialWaypoint, Maneuver>> paths = 
                planner.planPath(
                        graph, 
                        graph.getNearestWaypoint(new Point(0, 0, 0)),
                        graph.getNearestWaypoint(new Point(WORLD_SIZE, WORLD_SIZE, 0))
                        );
        long duration = System.currentTimeMillis() - startTime;

        double averageLength = 0;
        for (PlannedPath<SpatialWaypoint, Maneuver> path : paths) {
            if (path == null) {
                System.out.println("paths: " + paths);
                System.out.println("graph.getObstacles(): "
                        + graph.getObstacles());
                System.out.println( obstacles.size() + ";" + planner.getName() + "-" + obstacles.size() + ";" + planner.getName() + ";" + duration + ";" + paths.size() + ";" + averageLength);
            }
            averageLength += path.getWeight();
        }
        if (paths.size() != 0) {
            averageLength /= paths.size();
        }
        try {
            synchronized (out) {
                out.write( "" + obstacles.size() + ";" + curPlannerNum + "-" + planner.getName() + "-" + String.format("%03d", obstacles.size()) + ";" + planner.getName() + ";" + duration + ";" + paths.size() + ";" + averageLength);

                for (ManeuverTrajectoryMetric metric : trajectoryMetrics) {
                    out.write(";" + TrajectorySetMetrics.getPlanSetAvgDiversity(paths, metric));
                }

                out.newLine();
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ManeuverGraph createGraph() {
        return EightWayConstantSpeedGridGraph.create(WORLD_SIZE, WORLD_SIZE, WORLD_SIZE, WORLD_SIZE, 1.0);
    }
}
