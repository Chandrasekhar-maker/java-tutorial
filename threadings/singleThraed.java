
public class singleThraed{
    public static void main(String[] args) {
        // Code for single-threaded example
        System.out.println("This is a single-threaded example.");

        System.out.println("Processing in the main thread: " + Thread.currentThread().getName());
        System.out.println("Performing some tasks in the main thread...");
        try {
            Thread.sleep(2000); // Simulating a time-consuming task
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Task 1 completed.");


        System.out.println("Processing in the main thread: " + Thread.currentThread().getName());

        System.out.println("Performing another task in the main thread...");
        try {
            Thread.sleep(1000); // Simulating another time-consuming task
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Task 2 completed.");

    }
}