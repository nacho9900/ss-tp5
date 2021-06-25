package grupo4.ss.itba.edu.ar;

import grupo4.ss.itba.edu.ar.model.Environment;

/**
 * Hello world!
 */
public class App
{
    public static void main( String[] args ) {
        defaultRun();
    }

    public static void defaultRun() {
        Environment environment = Environment.builder()
                                             .withSeed( 28 )
                                             .withParticlesQuantity( 80 )
                                             .withDt( 1e-2 )
                                             .withDt2( 1e-2 )
                                             .build();
        environment.run();
        environment.printToFile();
        // environment.printDensityOverTime();
    }
}
