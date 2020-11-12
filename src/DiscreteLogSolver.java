import java.math.BigInteger;

public class DiscreteLogSolver {

    private final long abarInv;
    private final long binvTimesALessOne;
    private final long mask;
    private final long bigMask;
    private final long bigMod;
    private final BigInteger mod;
    private final BigInteger nInLimit;
    private final BigInteger nInLimitTimes4;
    private final BigInteger neededNumeratorPrecisionMod;

    public DiscreteLogSolver(long a, long b, int exp) {
        mask = (1L << exp) - 1;
        bigMask = (1L << (exp + 2)) - 1;
        bigMod = bigMask + 1;
        mod = BigInteger.TWO.pow(exp);
        nInLimit = BigInteger.TWO.pow(exp + 1);
        nInLimitTimes4 = BigInteger.TWO.pow(exp + 3);
        neededNumeratorPrecisionMod = BigInteger.TWO.pow(2*exp + 3);
        abarInv = modInverse(theta(a),exp);
        binvTimesALessOne = (a - 1) * modInverse(b, exp);
    }

    private long modInverse(long a, long k) {
        long x = a;
        x += x - a * x * x;
        x += x - a * x * x;
        x += x - a * x * x;
        x += x - a * x * x;
        return x & ((1L << k) - 1);
    }

    //a 2-adic analogue to ln(x), computed to the precision we care about
    // (normally ln is only defined if x % 4 = 1, some trickery needed).
    private long theta(long num) {
        if (num % 4 == 3) {
            num = bigMod - num;
        }
        BigInteger xhat = BigInteger.valueOf(num);

        // lim n -> 0 of (x^n - 1)/n = ln x. Taken in the 2-adic sense over next 4 lines.
        xhat = xhat.modPow(nInLimit, neededNumeratorPrecisionMod);
        xhat = xhat.subtract(BigInteger.ONE);
        xhat = xhat.divide(nInLimitTimes4);
        xhat = xhat.mod(mod);
        return xhat.longValue();
    }

    public long distanceFromZero(long seed) {
        long b = ((seed * binvTimesALessOne) + 1) & bigMask;
        return (theta(b) * abarInv) & mask;
    }
}