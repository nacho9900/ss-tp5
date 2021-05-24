package grupo4.ss.itba.edu.ar.utils;

import java.util.Random;

public class MathHelper
{
    public static double randBetween( Random r, double low, double high ) {
        return ( r.nextDouble() * ( high - low ) ) + low;
    }
}
