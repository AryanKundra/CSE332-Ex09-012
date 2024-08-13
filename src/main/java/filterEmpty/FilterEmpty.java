package filterEmpty;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class FilterEmpty {
    static ForkJoinPool POOL = new ForkJoinPool();
    private static int CUTOFF = 1;

    /**
     * Use the ForkJoin framework to write the following method in Java.
     *
     * Returns an array with the lengths of the non-empty strings from arr (in order)
     * For example, if arr is ["", "", "cse", "332", "", "hw", "", "7", "rox"], then
     * main.java.filterEmpty(arr) == [3, 3, 2, 1, 3].
     *
     * A parallel algorithm to solve this problem in O(lg n) span and O(n) work is the following:
     * (1) Do a parallel map to produce a bit set
     * (2) Do a parallel prefix over the bit set
     * (3) Do a parallel map to produce the output
     *
     * In lecture, we wrote parallelPrefix together, and it is included in the gitlab repository.
     * Rather than reimplementing that piece yourself, you should just use it. For the other two
     * parts though, you should write them.
     *
     * Do not bother with a sequential cutoff for this exercise, just have a base case that processes a single element.
     */
    public static int[] filterEmpty(String[] arr) {
        int[] bits = mapToBitSet(arr);
        int[] bitsum = ParallelPrefixSum.parallelPrefixSum(bits);
        return mapToOutput(arr, bitsum, bits);
    }

    public static int[] mapToBitSet(String[] arr) {
        int[] result = new int[arr.length];
        POOL.invoke(new mapTask(arr, result, 0, arr.length));
        return result;
    }

    static class mapTask extends RecursiveAction {
        int low;
        int high;
        String[] val;
        int[] result;

        public mapTask(String[] arr, int[] result, int l, int h) {
            low = l;
            high = h;
            val = arr;
            this.result = result;
        }

        protected void compute() {
            if (high - low <= CUTOFF) {
                for (int i = low; i < high; i++) {
                    result[i] = val[i].isEmpty() ? 0 : 1;
                }
                return;
            }
            int mid = low + (high - low) / 2;

            mapTask left = new mapTask(val, result, low, mid);
            mapTask right = new mapTask(val, result, mid, high);
            left.fork();
            right.compute();
            left.join();
        }
    }

    public static int[] mapToOutput(String[] input, int[] bitsum, int[] bitset) {
        if (bitsum.length == 0) {
            return new int[0];
        }
        int length = bitsum[bitsum.length - 1];
        int[] ret = new int[length];
        POOL.invoke(new OutputTask(input, ret, bitsum, bitset, 0, input.length));
        return ret;
    }

    public static class OutputTask extends RecursiveAction {
        int[] bitset;
        int[] bitsum;
        String[] og;
        int[] out;
        int low;
        int high;

        public OutputTask(String[] input, int[] output, int[] bitsum, int[] bitset, int l, int h) {
            this.og = input;
            this.bitsum = bitsum;
            this.bitset = bitset;
            this.out = output;
            this.low = l;
            this.high = h;
        }

        protected void compute() {
            if (high - low <= CUTOFF) {
                for (int i = low; i < high; i++) {
                    if (bitset[i] == 1) {
                        out[bitsum[i] - 1] = og[i].length();
                    }
                }
                return;
            }
            int mid = low + (high - low) / 2;
            OutputTask left = new OutputTask(og, out, bitsum, bitset, low, mid);
            OutputTask right = new OutputTask(og, out, bitsum, bitset, mid, high);
            left.fork();
            right.compute();
            left.join();
        }
    }


    private static void usage() {
        System.err.println("USAGE: FilterEmpty <String array>");
        System.exit(1);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            usage();
        }

        String[] arr = args[0].replaceAll("\\s*", "").split(",");
        System.out.println(Arrays.toString(filterEmpty(arr)));
    }
}