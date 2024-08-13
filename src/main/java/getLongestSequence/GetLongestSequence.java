package getLongestSequence;


import java.util.concurrent.RecursiveTask;


public class GetLongestSequence {

    /**

     * Use the ForkJoin framework to write the following method in Java.
     *
     * Returns the length of the longest consecutive sequence of val in arr.
     * For example, if arr is [2, 17, 17, 8, 17, 17, 17, 0, 17, 1], then
     * getLongestSequence(17, arr) == 3 and getLongestSequence(35, arr) == 0.
     *
     * Your code must have O(n) work, O(lg n) span, where n is the length of arr, and actually use the sequentialCutoff
     * argument. We have provided you with an extra class SequenceRange. We recommend you use this class as
     * your return value, but this is not required.
     */

    public static int getLongestSequence(int val, int[] arr, int sequentialCutoff) {
        if (arr.length <= sequentialCutoff) {
            return getLongestSequenceSequential(val, arr);
        } else {
            SequenceTask task = new SequenceTask(val, arr, 0, arr.length, sequentialCutoff);
            return task.invoke().longestRange;

        }

    }


    private static int getLongestSequenceSequential(int val, int[] arr){
        int curSequence = 0;
        int longestSequence = 0;
        for (int n : arr){
            if (n == val){
                curSequence++;
                longestSequence = Math.max(longestSequence, curSequence);
            }
            else{
                curSequence = 0;
            }
        }
        return longestSequence;
    }

    private static class SequenceTask extends RecursiveTask<SequenceRange>{
        private final int val;
        private final int[] arr;
        private final int start;
        private final int end;
        private final int sequentialCutoff;

        public SequenceTask(int val, int[] arr, int start, int end, int sequentialCutoff){
            this.val = val;
            this.arr = arr;
            this.start = start;
            this.end = end;
            this.sequentialCutoff = sequentialCutoff;

        }


        protected SequenceRange compute(){
            if (end - start <= sequentialCutoff){
                return new SequenceRange(0,0,getLongestSequenceSequential(val, arr));
            }

            else{
                int mid = start + (end - start) / 2;
                SequenceTask leTask = new SequenceTask(val, arr, start, mid, sequentialCutoff);
                SequenceTask riTask = new SequenceTask(val, arr, mid , end, sequentialCutoff);
                leTask.fork();
                SequenceRange riRes = riTask.compute();
                SequenceRange leRes = leTask.join();
                int matchingOnLeft = (arr[mid - 1] == val) ? leRes.matchingOnLeft + riRes.matchingOnLeft : 0;
                int matchingOnRight = (arr[mid] == val) ? leRes.matchingOnRight + riRes.matchingOnRight : 0;
                int longestRange = Math.max(Math.max(leRes.longestRange, riRes.longestRange), matchingOnLeft + matchingOnRight);
                return new SequenceRange(matchingOnLeft, matchingOnRight, longestRange);

            }

        }

    }


    private static void usage() {
        System.err.println("USAGE: GetLongestSequence <number> <array> <sequential cutoff>");
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

            System.out.println(getLongestSequence(val, arr, Integer.parseInt(args[2])));
        } catch (NumberFormatException e) {
            usage();
        }

    }

}
