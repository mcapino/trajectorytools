package tt.parallel;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ParallelLauncher {

    public static interface Executor {
        public void execute(String[] args);
    }

    private Executor provider;
    private Iterator<String[]> tasks;
    private ExecutorService executor;

    public ParallelLauncher(Executor provider, Iterator<String[]> parameters, int threads) {
        this.provider = provider;
        this.tasks = parameters;
        this.executor = Executors.newFixedThreadPool(threads);
    }

    public void run() {

        while (tasks.hasNext()) {
            final String[] next = tasks.next();
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    provider.execute(next);
                }
            });
            System.out.println("added");
        }

        executor.shutdown();

        boolean finished;
        do {
            try {
                finished = executor.awaitTermination(100, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                finished = false;
            }
        } while (!finished);

    }
}
