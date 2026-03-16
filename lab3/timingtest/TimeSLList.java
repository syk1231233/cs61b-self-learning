package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();
        final int M = 10000;
        for(int n = 1000; n <= 100000; n*=2){
            SLList<Integer> testArray = new SLList<>();
            for(int i = 0; i < n; i++){
                testArray.addLast(i);
            }
            Stopwatch clock = new Stopwatch();
            for(int m = M; m >= 0; m--){
                testArray.getLast();
            }
            times.addLast(clock.elapsedTime());
            Ns.addLast(n);
            opCounts.addLast(M);
        }
        printTimingTable(Ns,times,opCounts);
    }
}


