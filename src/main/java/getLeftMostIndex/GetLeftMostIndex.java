package getLeftMostIndex;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class GetLeftMostIndex {
    /**
     * Use the ForkJoin framework to write the following method in Java.
     *
     * Returns the index of the left-most occurrence of needle in haystack (think of needle and haystack as
     * Strings) or -1 if there is no such occurrence.
     *
     * For example, main.java.getLeftMostIndex("cse332", "Dudecse4ocse332momcse332Rox") == 9 and
     * main.java.getLeftMostIndex("sucks", "Dudecse4ocse332momcse332Rox") == -1.
     *
     * Your code must actually use the sequentialCutoff argument. You may assume that needle.length is much
     * smaller than haystack.length. A solution that peeks across subproblem boundaries to decide partial matches
     * will be significantly cleaner and simpler than one that does not.
     */

    private static int CUTOFF;
    private static ForkJoinPool POOL = new ForkJoinPool();
    public static int getLeftMostIndex(char[] needle, char[] haystack, int sequentialCutoff) {
       CUTOFF = sequentialCutoff;
       return POOL.invoke(new leftMostIndex(needle, haystack, 0, haystack.length ));
    }


    static class leftMostIndex extends RecursiveTask<Integer>{
        int low;
        int high;
        char[] needle;
        char[] haystack;

        public leftMostIndex(char[] needle, char[] haystack, int l, int h){
            this.needle = needle;
            this.haystack = haystack;
            this.low = l;
            this.high = h;
        }

        protected Integer compute(){
            if(high - low <= CUTOFF){
                for(int i = low; i < high; i++){
                    if(i +needle.length<=haystack.length){
                        boolean needleF = true;
                        for(int j = 0; j<needle.length; j++){
                            if (haystack[i+j] != needle[j]){
                                needleF = false;
                                break;
                            }
                        }
                        if(needleF){
                            return i;
                        }
                    }
                }
                return -1;
            }
            int mid = low + (high - low )/2;
            leftMostIndex le = new leftMostIndex(needle, haystack, low, mid);
            leftMostIndex ri = new leftMostIndex(needle, haystack, mid, high);
            ri.fork();
            int ret = le.compute();
            int val = ri.join();

            if(ret == -1){
                return val;
            } else{
                return ret;
            }
        }
    }



    private static void usage() {
        System.err.println("USAGE: GetLeftMostIndex <needle> <haystack> <sequential cutoff>");
        System.exit(2);
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            usage();
        }

        char[] needle = args[0].toCharArray();
        char[] haystack = args[1].toCharArray();
        try {
            System.out.println(getLeftMostIndex(needle, haystack, Integer.parseInt(args[2])));
        } catch (NumberFormatException e) {
            usage();
        }
    }
}
