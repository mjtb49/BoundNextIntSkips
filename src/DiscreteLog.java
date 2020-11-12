import java.math.BigInteger;

public class DiscreteLog {

    private static long A = 0x5deece66dL;
    private static long B = 11;
    private static int exp = 48;

    private static long modInverse(long a, long k) {
        long x = a;
        x += x - a * x * x;
        x += x - a * x * x;
        x += x - a * x * x;
        x += x - a * x * x;
        return x & ((1L << k) - 1);
    }

    //a 2-adic analogue to ln(x), computed to the precision we care about
    // (normally ln is only defined if x % 4 = 1, some trickery needed).
    private static long theta(long num) {
        if (num % 4 == 3) {
            num = (1L << (exp + 2)) - num;
        }
        BigInteger xhat = BigInteger.valueOf(num);

        // lim n -> 0 of (x^n - 1)/n = ln x. Taken in the 2-adic sense over next 4 lines.
        xhat = xhat.modPow(BigInteger.TWO.pow(exp + 1), BigInteger.TWO.pow(2*exp + 3));
        xhat = xhat.subtract(BigInteger.ONE);
        xhat = xhat.divide(BigInteger.TWO.pow(exp + 3));
        xhat = xhat.mod(BigInteger.TWO.pow(exp));
        return xhat.longValue();
    }

    public static long distanceFromZero(long seed) {
        long a = A;
        long b = (((seed * (A - 1)) * modInverse(B, exp)) + 1) & ((1L<<(exp + 2))-1);
        long abar = theta(a);
        long bbar = theta(b);
        return (bbar * modInverse(abar,exp) & ((1L << exp)-1));
    }

   /* public static void main(String[] args) {
       //for (int i = 0; i < 20; i++) {
           System.out.println( distanceFromZero(11) );
       //}
    }*/
}