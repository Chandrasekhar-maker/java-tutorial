/**
 * ============================================================================
 * INTERVIEW TOPIC: Deadlock Simulation & Resolution Strategies
 * ============================================================================
 * Key Interview Questions:
 * 1. What is a Deadlock and what are the 4 Coffman conditions required for it?
 *    (Mutual Exclusion, Hold and Wait, No Preemption, Circular Wait).
 * 2. How can you detect a deadlock in production?
 *    (Thread dump via `jstack <pid>`, Java VisualVM, or JConsole).
 * 3. How do you prevent/resolve deadlocks in code?
 *    (Strategy 1: Lock Ordering - acquire locks in identical global order;
 *     Strategy 2: Lock Timeout - ReentrantLock.tryLock() with timeouts).
 */

class Resource {
    private final String name;

    public Resource(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

public class DeadlockDemo {

    public static void main(String[] args) throws InterruptedException {
        Resource resourceA = new Resource("Resource-A (Database Connection)");
        Resource resourceB = new Resource("Resource-B (File System Handle)");

        System.out.println("==========================================================");
        System.out.println("1. Demonstration of DEADLOCK RESOLUTION via Lock Ordering");
        System.out.println("==========================================================");

        // SOLUTION: Both threads acquire Resource A FIRST, then Resource B.
        // Circular wait is broken! Deadlock cannot happen.

        Thread safeThread1 = new Thread(() -> {
            synchronized (resourceA) {
                System.out.println("Thread 1: Locked " + resourceA.getName());
                try { Thread.sleep(50); } catch (InterruptedException e) {}

                synchronized (resourceB) {
                    System.out.println("Thread 1: Successfully locked " + resourceB.getName() + " and completed work!");
                }
            }
        }, "SafeThread-1");

        Thread safeThread2 = new Thread(() -> {
            // Notice: Also acquires resourceA first!
            synchronized (resourceA) {
                System.out.println("Thread 2: Locked " + resourceA.getName());
                try { Thread.sleep(50); } catch (InterruptedException e) {}

                synchronized (resourceB) {
                    System.out.println("Thread 2: Successfully locked " + resourceB.getName() + " and completed work!");
                }
            }
        }, "SafeThread-2");

        safeThread1.start();
        safeThread2.start();

        safeThread1.join();
        safeThread2.join();

        System.out.println("\nAll tasks finished safely without any deadlock because locks followed a strict hierarchy!\n");

        System.out.println("==========================================================");
        System.out.println("2. Code Pattern that WOULD CAUSE Deadlock (Informational):");
        System.out.println("==========================================================");
        System.out.println(
            "// Thread 1: synchronized(resourceA) { synchronized(resourceB) { ... } }\n" +
            "// Thread 2: synchronized(resourceB) { synchronized(resourceA) { ... } }\n" +
            "// Result: Thread 1 holds A waiting for B, while Thread 2 holds B waiting for A.\n" +
            "// Both threads freeze permanently (Deadlock)."
        );
    }
}

