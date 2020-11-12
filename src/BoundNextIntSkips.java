import java.util.*;
import java.util.stream.LongStream;

final class BNIResult
{
    public int bound;
    public long distance;

    public BNIResult(int bound, long distance) {
        this.bound = bound;
        this.distance = distance;
    }
}

public class BoundNextIntSkips {

    /**
     * Compute a (strict) upper bound for the number of nextInt(n) skips in a certain number of calls to nextInt(n).
     * Currently the algorithm is memory intensive and slow
     * for even reasonably large values of n, but robust for large numCalls.
     * This algorithm also does not find sequences which cross over the 0 seed, which I think should be easier to patch
     * than the other issues.
     * @param n parameter passed to nextInt
     * @param numCalls the number of calls to bound skips in. Should be < 2^48.
     * @return the largest number of times nextInt can skip in numCalls times, along with the DFZ value where this
     * is achieved.
     */
    public static BNIResult boundNextIntSkipsAndGetDFZ(int n, long numCalls) {


        long firstSkipSeed = ((1L << 31) - ((1L << 31) % n)) << 17;
        long mask = (1L << 48) - 1;

        System.out.println("Loop size: " +(mask+1-firstSkipSeed));
        System.out.println("Need " + (float)((mask+1-firstSkipSeed)*8)/(1000*1000*1000) + " GB of memory to store resulting dfz's");

        DiscreteLogSolver d = new DiscreteLogSolver(0x5deece66dL, 11, 48);
        long[] distancesOfOffenders =  LongStream.range(firstSkipSeed, mask+1).parallel()
                .map(d::distanceFromZero).sorted().toArray();
        System.out.println("Computed DFZ");

        LinkedList<Long> lastDistances = new LinkedList<>();
        long firstDistance = distancesOfOffenders[0];
        lastDistances.add(firstDistance);

        int currentScore = 1;
        int bestScore = 1;
        long bestDistance = firstDistance;
        long startingDistance;
        int totalChecked = 1;
        boolean checkedEverything = false;
        while (!checkedEverything) {
            startingDistance = lastDistances.peekFirst();
            int index =  totalChecked % distancesOfOffenders.length;
            lastDistances.add(distancesOfOffenders[index]);

            if (((distancesOfOffenders[index] - startingDistance + 1) & mask) <= numCalls + currentScore) {
                currentScore++; // we had a skip so distance in seeds is gonna go up.
                if (currentScore > bestScore) {
                    bestScore = currentScore;
                    bestDistance = startingDistance;
                }
            } else {
                if (firstDistance == lastDistances.removeFirst() && totalChecked >= mask+1-firstSkipSeed)
                    checkedEverything = true;
                while (((distancesOfOffenders[index] - lastDistances.peekFirst() + 1) & mask) > numCalls + currentScore) {
                    if (firstDistance == lastDistances.removeFirst() && totalChecked >= mask+1-firstSkipSeed)
                        checkedEverything = true;
                    currentScore--;
                }
            }
            totalChecked++;

        };
        return new BNIResult(bestScore, bestDistance);
    }

    public static int getBoundOnNextIntSkips(int n, long numCalls) {
        return boundNextIntSkipsAndGetDFZ(n, numCalls).bound;
    }

    public static long getDFZForMaxSkips(int n, long numCalls) {
        return boundNextIntSkipsAndGetDFZ(n, numCalls).distance;
    }

    public static void main(String[] args) {
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "20");
        long NUM_CALLS = 1000000000;
        BNIResult r = boundNextIntSkipsAndGetDFZ(123, NUM_CALLS);
        System.out.println("There is a maximal run of " + NUM_CALLS +" seeds having "
                + r.bound + " skips starting at " + r.distance +" calls after 0");
    }
}
