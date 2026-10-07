import java.util.concurrent.atomic.AtomicInteger;

/**
 * ============================================================================
 * INTERVIEW TOPIC: Race Conditions, Synchronization, & Atomic Operations
 * ============================================================================
 * Key Interview Questions:
 * 1. What is a Race Condition and Critical Section?
 * 2. Why is counter++ NOT thread-safe in Java? (It is 3 operations: Read -> Update -> Write).
 * 3. What does the `synchronized` keyword do internally? (Intrinsic Monitor Lock).
 * 4. What is the difference between synchronized block vs synchronized method?
 * 5. How does AtomicInteger achieve thread-safety without locks? (CAS: Compare-And-Swap).
 */

class CounterDemo {
    // 1. Unsafe shared variable (Causes Race Condition)
    public int unsafeCount = 0;

    // 2. Synchronized counter (Thread-Safe using Monitor Lock)
    public int syncCount = 0;

    // 3. Atomic counter (Thread-Safe using Hardware CAS - Lock-Free)
    public AtomicInteger atomicCount = new AtomicInteger(0);

    // Unsafe increment: Not atomic! Multiple threads overwrite each other's increments
    public void incrementUnsafe() {
        unsafeCount++;
    }

    // Synchronized method: Only ONE thread can execute this on the instance at a time
    public synchronized void incrementSync() {
        syncCount++;
    }

    // Atomic increment: Lock-free, uses low-level CPU Compare-And-Swap instructions
    public void incrementAtomic() {
        atomicCount.incrementAndGet();
    }
}

public class SynchronizationAndRaceCondition {
    private static final int ITERATIONS = 10_000;

    public static void main(String[] args) throws InterruptedException {
        CounterDemo demo = new CounterDemo();

        System.out.println("Starting 2 threads, each performing " + ITERATIONS + " increments...");
        System.out.println("Expected Final Count for each = " + (ITERATIONS * 2) + "\n");

        // -------------------------------------------------------------
        // Test 1: UNSAFE COUNTER (Race Condition Demonstration)
        // -------------------------------------------------------------
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < ITERATIONS; i++) demo.incrementUnsafe();
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < ITERATIONS; i++) demo.incrementUnsafe();
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("[1. Unsynchronized Counter Result] = " + demo.unsafeCount);
        if (demo.unsafeCount < ITERATIONS * 2) {
            System.out.println("    -> DATA LOSS DETECTED! Race condition corrupted values.\n");
        } else {
            System.out.println("    -> Coincidentally matched (try running again or with more threads).\n");
        }

        // -------------------------------------------------------------
        // Test 2: SYNCHRONIZED COUNTER (Thread-Safe using Monitor Lock)
        // -------------------------------------------------------------
        Thread t3 = new Thread(() -> {
            for (int i = 0; i < ITERATIONS; i++) demo.incrementSync();
        });
        Thread t4 = new Thread(() -> {
            for (int i = 0; i < ITERATIONS; i++) demo.incrementSync();
        });

        t3.start();
        t4.start();
        t3.join();
        t4.join();

        System.out.println("[2. Synchronized Counter Result]   = " + demo.syncCount);
        System.out.println("    -> Guaranteed accuracy via intrinsic monitor locks.\n");

        // -------------------------------------------------------------
        // Test 3: ATOMIC INTEGER (Thread-Safe, Lock-Free High Performance)
        // -------------------------------------------------------------
        Thread t5 = new Thread(() -> {
            for (int i = 0; i < ITERATIONS; i++) demo.incrementAtomic();
        });
        Thread t6 = new Thread(() -> {
            for (int i = 0; i < ITERATIONS; i++) demo.incrementAtomic();
        });

        t5.start();
        t6.start();
        t5.join();
        t6.join();

        System.out.println("[3. AtomicInteger Counter Result]  = " + demo.atomicCount.get());
        System.out.println("    -> Guaranteed accuracy via Lock-Free CPU Compare-And-Swap (CAS).\n");
    }
}

