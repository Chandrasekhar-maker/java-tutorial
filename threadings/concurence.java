
public class concurence{
    public static void main(String[] args) {
        // Code for concurrency example

        Thread A=new Thread(()->{
            System.out.println("processing thread name is: "+Thread.currentThread().getName());
            for(int i=0;i<5;i++){
                System.out.println("Thread A: "+i);
                try{
                    Thread.sleep(100);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        });

        Thread B =new Thread(()->{
            System.out.println("Thread B: is woring for couple of seconds by the time a thread A is working");
            try{
                Thread.sleep(500);
            }
            catch(InterruptedException e){
                e.printStackTrace();
            }
        });
        B.start();
        A.start();
    }
}