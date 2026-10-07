
import java.util.List;   

public class parallelism{

    private static int fun(int num){
        int sum=0;
        for(int i=0;i<1_000_000_000_000L;i++){
            sum+=i;
        }
        return sum+num;
    }

    public static void main(String[] args) {
        
        List<Integer> numbers=List.of(1,2,3,4);
        
        System.out.println("available processors: "+Runtime.getRuntime().availableProcessors());

        long start=System.currentTimeMillis();

        numbers.parallelStream().forEach(num->{
            System.out.println("processing of "+num+" is on the thread "+Thread.currentThread().getName());
            fun(num);
        });

        long end=System.currentTimeMillis();

        System.out.println("time taken: "+(end-start)+" ms");
    }
}