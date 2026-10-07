import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * ============================================================================
 * INTERVIEW TOPIC: Java Thread Pools & ExecutorService
 * ============================================================================
 * Key Interview Questions:
 * 1. Why should we NEVER use `new Thread()` in enterprise production applications?
 *    (Creating a thread is expensive: ~1MB memory, OS context switch overhead, risking OutOfMemoryError).
 * 2. What are the common types of thread pools in java.util.concurrent.Executors?
 *    - newFixedThreadPool(n): Reuses a fixed number of threads.
 *    - newCachedThreadPool(): Creates threads as needed, reclaims idle ones.
 *    - newSingleThreadExecutor(): Single worker thread executing sequentially.
 *    - newScheduledThreadPool(n): For cron/delayed tasks.
 * 3. What is the difference between execute() and submit()?
 *    - execute(Runnable): Fire-and-forget; returns void, cannot capture results.
 *    - submit(Callable/Runnable): Returns a Future<T> to check status or get result.
 * 4. What is the difference between shutdown() and shutdownNow()?
 *    - shutdown(): Stops accepting new tasks, finishes already submitted tasks.
 *    - shutdownNow(): Attempts to cancel active tasks immediately via thread interrupt.
 */

public class ExecutorServiceDemo {
    public static void main(String[] args) {
        System.out.println("Available CPU Cores: " + Runtime.getRuntime().availableProcessors());

        // Create a fixed pool with 3 reusable worker threads
        ExecutorService executor = Executors.newFixedThreadPool(3);
        List<Future<String>> futures = new ArrayList<>();

        System.out.println("Submitting 6 tasks to a pool of 3 threads...\n");

        for (int i = 1; i <= 6; i++) {
            final int taskId = i;
            Callable<String> task = () -> {
                String threadName = Thread.currentThread().getName();
                System.out.println("[Start] Task " + taskId + " running on: " + threadName);
                Thread.sleep(300); // Simulate network or DB processing
                System.out.println("[End]   Task " + taskId + " completed on: " + threadName);
                return "Result from Task-" + taskId;
            };

            // submit() queues the task and immediately returns a Future handle
            futures.add(executor.submit(task));
        }

        System.out.println("Gathering results from Future objects:\n");
        for (Future<String> future : futures) {
            try {
                // future.get() blocks until that particular task completes
                String result = future.get();
                System.out.println("-> Retrieved: " + result);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        // Standard Graceful Shutdown Sequence:
        System.out.println("\nInitiating graceful shutdown...");
        executor.shutdown(); // Reject any incoming new tasks

        try {
            // Wait up to 5 seconds for existing tasks to terminate
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                System.out.println("Tasks did not finish within timeout, forcing shutdownNow()...");
                executor.shutdownNow();
            } else {
                System.out.println("ExecutorService closed cleanly. All threads safely terminated.");
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}

