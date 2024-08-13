package hasOver;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;


public class HasOver {
    /**
     * Use the ForkJoin framework to write the following method in Java.
     *
     * Returns true if arr has any elements strictly larger than val.
     * For example, if arr is [21, 17, 35, 8, 17, 1], then
     * main.java.hasOver(21, arr) == true and main.java.hasOver(35, arr) == false.
     *
     * Your code must have O(n) work, O(lg n) span, where n is the length of arr, and actually use the sequentialCutoff
     * argument.
     */

    private static int CUTOFF;
    private static ForkJoinPool POOL = new ForkJoinPool();
    public static boolean hasOver(int val, int[] arr, int sequentialCutoff) {
        CUTOFF = sequentialCutoff;
        return POOL.invoke(new maximumTasks(val, arr, 0, arr.length, sequentialCutoff));
    }

    /* TODO: Add a sequential method and parallel task here */

    static class maximumTasks extends RecursiveTask<Boolean>{
        int low;
        int high;
        int value;
        int[] arr;
        int CUTOFF;
        public maximumTasks(int val, int[] arrParse, int lo, int hi, int CUTOFF){
            this.arr = arrParse;
            this.value = val;
            this.low = lo;
            this.high = hi;
            this.CUTOFF = CUTOFF;
        }

        protected Boolean compute(){
            if (high - low <= CUTOFF){
                if(returnMaxium(arr,low, high)>value){
                    return true;
                }
                else
                {
                    return false;
                }
            }
            int mid = low + (high - low)/2;
            maximumTasks ri = new maximumTasks(value, arr, low, mid, CUTOFF);
            maximumTasks le = new maximumTasks(value, arr, mid, high, CUTOFF);
            ri.fork();
            return ri.compute() || le.compute();
        }
    }

    private static int returnMaxium(int[]arr, int l, int h){
        int max = arr[l];
        for (int i = l +1;  i < h; i++){
            if(arr[i] > max){
                max = arr[i];
            }
        }
        return max;
    }

    private static void usage() {
        System.err.println("USAGE: HasOver <number> <array> <sequential cutoff>");
        System.exit(2);
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            usage();
        }

        int val = 0;
        int[] arr = null;

        try {
            val = Integer.parseInt(args[0]);
            String[] stringArr = args[1].replaceAll("\\s*", "").split(",");
            arr = new int[stringArr.length];
            for (int i = 0; i < stringArr.length; i++) {
                arr[i] = Integer.parseInt(stringArr[i]);
            }
            System.out.println(hasOver(val, arr, Integer.parseInt(args[2])));
        } catch (NumberFormatException e) {
            usage();
        }

    }
}
