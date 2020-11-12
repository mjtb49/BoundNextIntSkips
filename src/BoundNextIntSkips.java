import java.util.TreeSet;
import java.util.LinkedList;

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
        TreeSet<Long> distancesOfOffenders = new TreeSet<>();

        long firstSkipSeed = ((1L << 31) - ((1L << 31) % n)) << 17;
        long mask = (1L << 48) - 1;

        for (long s = firstSkipSeed; s <= mask; s++) {
            /*if (s % 100000 == 0)
                System.out.println("Progress " + (s - firstSkipSeed) / (double) (mask - firstSkipSeed));*/
            distancesOfOffenders.add(DiscreteLog.distanceFromZero(s));
        }

        //TODO lazily done so doesn't detect if the worst case crosses the 0 seed.
        LinkedList<Long> lastDistances = new LinkedList<Long>();
        lastDistances.add(distancesOfOffenders.last());
        int currentScore = 1;
        int bestScore = 1;
        long bestDistance = distancesOfOffenders.last();
        while (!distancesOfOffenders.isEmpty()) {
            long nextDistance = distancesOfOffenders.pollFirst();
            lastDistances.add(nextDistance);
            if (((nextDistance - lastDistances.peekFirst()) & mask) <= numCalls + currentScore) {
                currentScore++; // we had a skip so distance in seeds is gonna go up.
                if (currentScore > bestScore) {
                    bestScore = currentScore;
                    bestDistance = lastDistances.peekFirst();
                   /* System.out.println("Found new best!");
                    for (long l : lastDistances)
                        System.out.print(l + " ");
                    System.out.println();*/
                }
            } else {
                lastDistances.removeFirst();
                while (((nextDistance - lastDistances.peekFirst()) & mask) > numCalls + currentScore) {
                    currentScore--;
                    lastDistances.removeFirst();
                }
            }
        }
        //System.out.println("There is at least one run of length " + bestScore + " starting at " + bestDistance +" calls after 0");
        return new BNIResult(bestScore, bestDistance);
    }

    public static int getBoundOnNextIntSkips(int n, long numCalls) {
        return boundNextIntSkipsAndGetDFZ(n, numCalls).bound;
    }

    public static long getDFZForMaxSkips(int n, long numCalls) {
        return boundNextIntSkipsAndGetDFZ(n, numCalls).distance;
    }

    public static void main(String[] args) {
        long NUM_CALLS = 5000000000L;
        BNIResult r = boundNextIntSkipsAndGetDFZ(3, NUM_CALLS);
        System.out.println("There is a maximal run of " + NUM_CALLS +" seeds having "
                + r.bound + " skips starting at " + r.distance +" calls after 0");
    }
}
