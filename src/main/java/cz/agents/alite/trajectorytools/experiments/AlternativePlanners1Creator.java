package cz.agents.alite.trajectorytools.experiments;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import cz.agents.alite.creator.Creator;
import cz.agents.alite.trajectorytools.alterantiveplanners.AlternativePathPlanner;
import cz.agents.alite.trajectorytools.alterantiveplanners.AlternativePlannerSelector;
import cz.agents.alite.trajectorytools.alterantiveplanners.DifferentStateMetricPlanner;
import cz.agents.alite.trajectorytools.alterantiveplanners.ObstacleExtensions;
import cz.agents.alite.trajectorytools.alterantiveplanners.TrajectoryDistanceMaxMinMetricPlanner;
import cz.agents.alite.trajectorytools.alterantiveplanners.TrajectoryDistanceMetricPlanner;
import cz.agents.alite.trajectorytools.alterantiveplanners.VoronoiDelaunayPlanner;
import cz.agents.alite.trajectorytools.graph.ObstacleGraphView;
import cz.agents.alite.trajectorytools.graph.ObstacleGraphView.ChangeListener;
import cz.agents.alite.trajectorytools.graph.spatial.SpatialGraphs;
import cz.agents.alite.trajectorytools.graph.spatial.SpatialGridFactory;
import cz.agents.alite.trajectorytools.graph.spatial.maneuvers.SpatialManeuver;
import cz.agents.alite.trajectorytools.planner.AStarPlanner;
import cz.agents.alite.trajectorytools.planner.HeuristicFunction;
import cz.agents.alite.trajectorytools.planner.PlannedPath;
import cz.agents.alite.trajectorytools.trajectorymetrics.DifferentStateMetric;
import cz.agents.alite.trajectorytools.trajectorymetrics.TrajectoryDistanceMetric;
import cz.agents.alite.trajectorytools.trajectorymetrics.TrajectoryMetric;
import cz.agents.alite.trajectorytools.trajectorymetrics.TrajectorySetMetrics;
import cz.agents.alite.trajectorytools.util.Point;
import cz.agents.alite.trajectorytools.util.Waypoint;

public class AlternativePlanners1Creator implements Creator {

    private static final int NUM_OF_OBSTACLES_MIN = 2;
    private static final int NUM_OF_OBSTACLES_MAX = 16;
    private static final int NUM_OF_OBSTACLES_STEP = 2;
    private static final int NUM_OF_REPEATS = 5;
    
    private static final int NUM_OF_THREADS = 5;
    private static final ExecutorService executor = Executors.newFixedThreadPool(NUM_OF_THREADS);

    private static final int PATH_SOLUTION_LIMIT = 5;

    private static final int WORLD_SIZE = 10;

    private static final AStarPlanner<Point, DefaultWeightedEdge> planner = new AStarPlanner<Point, DefaultWeightedEdge>();
    {
        planner.setHeuristicFunction(new HeuristicFunction<Point>() {
        @Override
            public double getHeuristicEstimate(Point current, Point goal) {
                return current.distance(goal) + ( current.x > current.y ? 0.1 : -0.1 );
            }
        });
    }
    
    private static final List<AlternativePathPlanner<Point, DefaultWeightedEdge>> alternativePlanners = new ArrayList<AlternativePathPlanner<Point,DefaultWeightedEdge>>();
    {
        alternativePlanners.add( 
                new DifferentStateMetricPlanner<Point, DefaultWeightedEdge>( planner, PATH_SOLUTION_LIMIT )
                );
        alternativePlanners.add( 
                new TrajectoryDistanceMetricPlanner<Point, DefaultWeightedEdge>( planner, PATH_SOLUTION_LIMIT, 2)
                );
        alternativePlanners.add( 
                new TrajectoryDistanceMaxMinMetricPlanner<Point, DefaultWeightedEdge>( planner, PATH_SOLUTION_LIMIT, 2 )
                );
        alternativePlanners.add( 
                new ObstacleExtensions<Point, DefaultWeightedEdge>(planner) 
                );
        alternativePlanners.add( 
                new AlternativePlannerSelector<Point, DefaultWeightedEdge>( new ObstacleExtensions<Point, DefaultWeightedEdge>(planner), PATH_SOLUTION_LIMIT)
                );
        alternativePlanners.add( 
                new VoronoiDelaunayPlanner<Point, DefaultWeightedEdge>( planner )
                );
        alternativePlanners.add( 
                new AlternativePlannerSelector<Point, DefaultWeightedEdge>( new VoronoiDelaunayPlanner<Point, DefaultWeightedEdge>(planner), PATH_SOLUTION_LIMIT)
                );
    }

    private static final List<TrajectoryMetric<Point, DefaultWeightedEdge>> trajectoryMetrics = new ArrayList<TrajectoryMetric<Point,DefaultWeightedEdge>>();
    {
        trajectoryMetrics.add( 
                new DifferentStateMetric<Point, DefaultWeightedEdge>()
                );
        trajectoryMetrics.add( 
                new TrajectoryDistanceMetric<Point, DefaultWeightedEdge>()
                );
    }

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
            for (TrajectoryMetric<Point, DefaultWeightedEdge> metric : trajectoryMetrics) {
                out.write(";" + metric.getName());
            }
            out.newLine();
    
            for (int numObstacles = NUM_OF_OBSTACLES_MIN; numObstacles <= NUM_OF_OBSTACLES_MAX; numObstacles+=NUM_OF_OBSTACLES_STEP) {
            
//                System.out.println("numObstacles: " + numObstacles);
                
                for (int repeat = 0; repeat < NUM_OF_REPEATS; repeat++) {
//                    System.out.println("repeat: " + repeat);
                       
                    final List<Point> obstacles = generateRandomObstacles(numObstacles);

                    int plannerNum = 0;
                    for (final AlternativePathPlanner<Point, DefaultWeightedEdge> planner : alternativePlanners) {
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
            ObstacleGraphView graph = ObstacleGraphView.createFromGraph(createGraph(), new ChangeListener() {
                @Override
                public void graphChanged() {
                }
            } );
    	    
            PlannedPath<Point, DefaultWeightedEdge> planPath = planner.planPath(
                    graph, 
                    SpatialGraphs.getNearestVertex(graph, new Point(0, 0, 0)),
                    SpatialGraphs.getNearestVertex(graph, new Point(WORLD_SIZE, WORLD_SIZE, 0))
                    );
            if (planPath != null) {
                return obstacles;
            }
        }
    }
    
    private void runExperiment(
            final BufferedWriter out,
            final List<Point> obstacles,
            final AlternativePathPlanner<Point, DefaultWeightedEdge> planner, 
            int curPlannerNum) {
        Graph<Waypoint, SpatialManeuver> originalGraph = createGraph(); 

        ObstacleGraphView graph = ObstacleGraphView.createFromGraph(originalGraph, new ChangeListener() {
            @Override
            public void graphChanged() {
            }
        } );

        for (Point obstacle : obstacles) {
            graph.addObstacle(obstacle);
        }

        long startTime = System.currentTimeMillis();
        Collection<PlannedPath<Point, DefaultWeightedEdge>> paths = 
                planner.planPath(
                        graph, 
                        SpatialGraphs.getNearestVertex(graph, new Point(0, 0, 0)),
                        SpatialGraphs.getNearestVertex(graph, new Point(WORLD_SIZE, WORLD_SIZE, 0))
                        );
        long duration = System.currentTimeMillis() - startTime;

        double averageLength = 0;
        for (PlannedPath<Point, DefaultWeightedEdge> path : paths) {
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

                for (TrajectoryMetric<Point, DefaultWeightedEdge> metric : trajectoryMetrics) {
                    out.write(";" + TrajectorySetMetrics.getPlanSetAvgDiversity(paths, metric));
                }

                out.newLine();
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Graph<Waypoint, SpatialManeuver> createGraph() {
        return SpatialGridFactory.create4WayGrid(WORLD_SIZE, WORLD_SIZE, WORLD_SIZE, WORLD_SIZE, 1.0);
    }
}
