import java.util.LinkedList;
import java.util.Queue;

/**
 * ============================================================================
 * INTERVIEW TOPIC: Classic Producer-Consumer Pattern (wait() & notifyAll())
 * ============================================================================
 * Key Interview Questions:
 * 1. Why are wait(), notify(), and notifyAll() defined in Object class, not Thread?
 *    (Because locks/monitors are associated with objects on the heap, not threads).
 * 2. Why must wait() and notify() be called inside a synchronized context?
 *    (To avoid IllegalMonitorStateException and race conditions on the lock).
 * 3. Why should wait() ALWAYS be called inside a while loop, never an if condition?
 *    (To protect against Spurious Wakeups and re-verify state when multiple threads wake up).
 * 4. What is the difference between notify() and notifyAll()?
 *    (notify wakes up one random waiting thread; notifyAll wakes all, preventing deadlocks).
 */

class SharedBuffer {
    private final Queue<Integer> buffer = new LinkedList<>();
    private final int capacity;

    public SharedBuffer(int capacity) {
        this.capacity = capacity;
    }

    // Producer method
    public synchronized void produce(int item) throws InterruptedException {
        // ALWAYS use while loop, never 'if' - handles spurious wakeups!
        while (buffer.size() == capacity) {
            System.out.println("Buffer is FULL (" + capacity + "). Producer waiting...");
            wait(); // Releases the monitor lock and suspends execution
        }

        buffer.add(item);
        System.out.println("Produced item: " + item + " | Buffer size: " + buffer.size());

        // Notify waiting consumers that data is now available
        notifyAll();
    }

    // Consumer method
    public synchronized int consume() throws InterruptedException {
        // ALWAYS use while loop, never 'if'
        while (buffer.isEmpty()) {
            System.out.println("Buffer is EMPTY. Consumer waiting...");
            wait(); // Releases the monitor lock and suspends execution
        }

        int item = buffer.poll();
        System.out.println("Consumed item: " + item + " | Buffer size: " + buffer.size());

        // Notify waiting producers that a slot has freed up
        notifyAll();
        return item;
    }
}

public class ProducerConsumerDemo {
    public static void main(String[] args) {
        SharedBuffer buffer = new SharedBuffer(2); // Small capacity of 2 to trigger wait/notify

        // Producer Thread
        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 6; i++) {
                    buffer.produce(i);
                    Thread.sleep(150); // Simulating time to manufacture item
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Producer-Thread");

        // Consumer Thread
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 6; i++) {
                    buffer.consume();
                    Thread.sleep(400); // Simulating time to process item (slower than producer)
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Consumer-Thread");

        producer.start();
        consumer.start();
    }
}

