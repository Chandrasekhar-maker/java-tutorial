/**
 * ============================================================================
 * INTERVIEW TOPIC: Thread Coordination, join(), Lifecycle States & Daemon Threads
 * ============================================================================
 * Key Interview Questions:
 * 1. What does thread.join() do?
 *    (Causes the caller thread to pause execution until the target thread dies).
 * 2. What are the 6 Thread States in Java (Thread.State enum)?
 *    - NEW
 *    - RUNNABLE
 *    - BLOCKED
 *    - WAITING
 *    - TIMED_WAITING
 *    - TERMINATED
 * 3. What is a Daemon Thread? How does it differ from a User Thread?
 *    (JVM does NOT wait for daemon threads to finish. When all user threads terminate,
 *     the JVM halts immediately, killing any active daemon threads like Garbage Collector).
 */

public class ThreadCoordinationJoin {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("==========================================================");
        System.out.println("1. Parallel Task Coordination using join()");
        System.out.println("==========================================================");
        System.out.println("Scenario: Downloading 3 independent data chunks in parallel...\n");

        Thread worker1 = new Thread(() -> simulateDownload("User-Service Data", 600));
        Thread worker2 = new Thread(() -> simulateDownload("Payment-Service Data", 400));
        Thread worker3 = new Thread(() -> simulateDownload("Inventory-Service Data", 800));

        // Print thread states before starting
        System.out.println("Worker 1 State before start(): " + worker1.getState()); // NEW

        worker1.start();
        worker2.start();
        worker3.start();

        System.out.println("Worker 1 State after start(): " + worker1.getState()); // RUNNABLE / TIMED_WAITING

        // join() instructs the main thread to WAIT until each worker finishes
        worker1.join();
        worker2.join();
        worker3.join();

        System.out.println("\nAll 3 downloads finished! Now main thread can aggregate report.");
        System.out.println("Worker 1 Final State: " + worker1.getState()); // TERMINATED

        System.out.println("\n==========================================================");
        System.out.println("2. Daemon Thread vs User Thread");
        System.out.println("==========================================================");

        Thread daemonHeartbeat = new Thread(() -> {
            while (true) {
                System.out.println("    [Daemon Thread] Background heartbeat pulse...");
                try { Thread.sleep(200); } catch (InterruptedException e) { break; }
            }
        });

        // Mark as daemon BEFORE starting
        daemonHeartbeat.setDaemon(true);
        daemonHeartbeat.start();

        System.out.println("Main thread is finishing work now.");
        System.out.println("Notice: As soon as main terminates, JVM will immediately exit without waiting for daemon.");
        Thread.sleep(500); // Allow 2 pulses to print before main exits
        System.out.println("Main thread exited successfully.");
    }

    private static void simulateDownload(String serviceName, int durationMs) {
        System.out.println("Started fetching from: " + serviceName);
        try {
            Thread.sleep(durationMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Completed fetching from: " + serviceName + " (" + durationMs + "ms)");
    }
}

