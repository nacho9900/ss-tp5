package grupo4.ss.itba.edu.ar;

import grupo4.ss.itba.edu.ar.model.Environment;

/**
 * Hello world!
 */
public class App
{
    public static void main( String[] args ) {
        Environment environment = Environment.builder()
                                             .withSeed( 94 )
                                             .withOpening( 1.2 )
                                             .withParticlesQuantity( 200 )
                                             .withDt( 1e-2 )
                                             .build();
        environment.run();
        environment.printToFile();
    }
}
