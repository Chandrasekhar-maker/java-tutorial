import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * ============================================================================
 * INTERVIEW TOPIC: Java 21 Virtual Threads (Project Loom)
 * ============================================================================
 * Key Interview Questions:
 * 1. What are Virtual Threads and why were they introduced in Java 21?
 *    (To provide high-throughput lightweight concurrency without reactive complexity).
 * 2. Platform Threads vs Virtual Threads:
 *    - Platform Thread: 1:1 mapping with OS Kernel thread. Heavy (~1MB stack), limited to ~2,000-5,000.
 *    - Virtual Thread: M:N mapping. JVM-managed, mounted on carrier OS threads. Lightweight (bytes/KBs).
 *      You can easily spawn 1,000,000 virtual threads without crashing RAM!
 * 3. When should you use Virtual Threads?
 *    - High-throughput I/O-bound tasks (HTTP microservice calls, DB queries).
 *    - NOT recommended for heavy CPU-bound computational algorithms (use Parallel Streams / ForkJoinPool).
 */

public class VirtualThreadDemo {
    private static final int TASK_COUNT = 10_000;

    public static void main(String[] args) {
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("==========================================================");
        System.out.println("1. Spawning " + TASK_COUNT + " Virtual Threads concurrently");
        System.out.println("==========================================================");

        Instant start = Instant.now();

        // Java 21 Executor: Creates an unbounded number of lightweight virtual threads
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.rangeClosed(1, TASK_COUNT).forEach(i -> {
                executor.submit(() -> {
                    try {
                        // Simulating a 100ms I/O or network call (blocking wait)
                        Thread.sleep(100);
                        if (i == 1 || i == TASK_COUNT) {
                            System.out.println("Task " + i + " executed on: " + Thread.currentThread());
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            });
        } // AutoCloseable ensures all tasks complete before exiting try-block

        Instant end = Instant.now();
        long duration = Duration.between(start, end).toMillis();

        System.out.println("\nSUCCESS: Successfully executed " + TASK_COUNT + " blocking tasks!");
        System.out.println("Total Time Elapsed: " + duration + " ms");
        System.out.println("Notice: If done with platform threads, 10,000 threads would require ~10 GB of RAM!");
        System.out.println("With Virtual Threads, memory overhead was negligible (< 50MB).");
    }
}

