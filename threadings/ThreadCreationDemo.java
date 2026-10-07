import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * ============================================================================
 * INTERVIEW TOPIC: 3 Ways to Create Threads in Java & start() vs run()
 * ============================================================================
 * Key Interview Questions:
 * 1. What are the differences between extending Thread vs implementing Runnable?
 * 2. Why is Runnable generally preferred over extending Thread? (Java lacks multiple inheritance)
 * 3. What is the difference between Runnable and Callable?
 * 4. What happens if you call run() directly instead of start()?
 */

// Approach 1: Extending the Thread class
class WorkerThread extends Thread {
    @Override
    public void run() {
        System.out.println("[Approach 1 - Thread Class] Executing in: " + Thread.currentThread().getName());
    }
}

// Approach 2: Implementing the Runnable interface (Decouples task from runner)
class WorkerRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("[Approach 2 - Runnable] Executing in: " + Thread.currentThread().getName());
    }
}

// Approach 3: Implementing Callable<V> (Can return a value and throw checked exceptions)
class WorkerCallable implements Callable<String> {
    @Override
    public String call() throws Exception {
        System.out.println("[Approach 3 - Callable] Computing result in: " + Thread.currentThread().getName());
        Thread.sleep(500); // Simulating work
        return "Calculated Report Data";
    }
}

public class ThreadCreationDemo {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("1. Demonstrating Thread Creation Approaches");
        System.out.println("==================================================");

        // Approach 1: Thread class
        WorkerThread t1 = new WorkerThread();
        t1.start();

        // Approach 2: Runnable interface (also possible via lambda)
        Thread t2 = new Thread(new WorkerRunnable());
        t2.start();

        // Approach 2 (Modern Lambda variant):
        Thread t2Lambda = new Thread(() -> {
            System.out.println("[Approach 2 - Lambda Runnable] Executing in: " + Thread.currentThread().getName());
        });
        t2Lambda.start();

        // Approach 3: Callable with FutureTask (To retrieve return values)
        Callable<String> callableTask = new WorkerCallable();
        FutureTask<String> futureTask = new FutureTask<>(callableTask);
        Thread t3 = new Thread(futureTask);
        t3.start();

        try {
            // .get() blocks until the thread finishes and returns the value
            String result = futureTask.get();
            System.out.println("[Approach 3 Result] Received: " + result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("\n==================================================");
        System.out.println("2. The Crucial Interview Trap: start() vs run()");
        System.out.println("==================================================");

        Thread trapThread = new Thread(() -> {
            System.out.println("Executing thread: " + Thread.currentThread().getName());
        });

        System.out.print("Calling trapThread.run() directly -> ");
        // DOES NOT create a new OS thread! Runs synchronously in the caller's thread (main).
        trapThread.run();

        System.out.print("Calling trapThread.start() -> ");
        // CREATES a new thread, allocates a stack, and tells OS scheduler to run run().
        trapThread.start();
    }
}

